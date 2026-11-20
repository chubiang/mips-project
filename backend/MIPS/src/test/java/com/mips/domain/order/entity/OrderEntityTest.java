package com.mips.domain.order.entity;

import com.mips.domain.account.entity.Account;
import com.mips.domain.comm.enums.Currency;
import com.mips.domain.order.enums.*;
import com.mips.domain.stock.entity.SecurityMaster;
import com.mips.domain.user.entity.User;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class OrderEntityTest {
    private TradeOrder.TradeOrderBuilder order() {
        return TradeOrder.builder().orderId(UUID.randomUUID().toString())
                .user(mock(User.class)).account(mock(Account.class))
                .securityMaster(mock(SecurityMaster.class)).orderType(OrderType.BUY)
                .orderMethod(OrderMethod.MARKET).quantity(1L);
    }

    @Test
    void retryPreservesOrderIdentity() {
        String key = UUID.randomUUID().toString();
        TradeOrder first = order().orderId(key).build();
        TradeOrder retry = order().orderId(key).build();
        first.prePersist();
        retry.prePersist();
        assertEquals(first.getOrderId(), retry.getOrderId());
        assertEquals(OrderStatus.PENDING, first.getStatus());
        assertEquals(0L, first.getExecutedQuantity());
    }

    @Test
    void rejectsInvalidOrderInputs() {
        assertThrows(IllegalArgumentException.class, () -> order().quantity(0L).build());
        assertThrows(IllegalArgumentException.class, () -> order().orderMethod(OrderMethod.LIMIT).build());
        assertThrows(IllegalArgumentException.class, () -> order().price(BigDecimal.TEN).build());
    }

    @Test
    void reservationMustMatchOrderSideAndResource() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            OrderReservation cash = OrderReservation.builder().order(order().build())
                    .reservationType(ReservationType.CASH).currency(Currency.USD)
                    .remainingCash(BigDecimal.TEN).build();
            assertTrue(validator.validate(cash).isEmpty());
            OrderReservation wrong = OrderReservation.builder().order(order().build())
                    .reservationType(ReservationType.STOCK).remainingQuantity(1L).build();
            assertFalse(validator.validate(wrong).isEmpty());
            OrderReservation negative = OrderReservation.builder().order(order().build())
                    .reservationType(ReservationType.CASH).currency(Currency.USD)
                    .remainingCash(BigDecimal.valueOf(-1)).build();
            assertFalse(validator.validate(negative).isEmpty());
            OrderReservation stock = OrderReservation.builder().order(order().orderType(OrderType.SELL).build())
                    .reservationType(ReservationType.STOCK).remainingQuantity(1L).build();
            assertTrue(validator.validate(stock).isEmpty());
        }
    }
}
