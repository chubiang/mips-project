package com.mips.domain.order.entity;

import com.mips.domain.account.entity.Account;
import com.mips.domain.comm.enums.Currency;
import com.mips.domain.order.enums.OrderMethod;
import com.mips.domain.order.enums.OrderStatus;
import com.mips.domain.order.enums.OrderType;
import com.mips.domain.order.enums.ReservationType;
import com.mips.domain.stock.entity.SecurityMaster;
import com.mips.domain.user.entity.User;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderEntityTest {

    private static final LocalDateTime OCCURRED_AT = LocalDateTime.of(2026, 11, 20, 10, 0);

    @Test
    void retryKeepsTheSameOrderId() {
        UUID requestId = UUID.randomUUID();
        TradeOrder first = order(OrderType.BUY).orderId(requestId).build();
        TradeOrder retry = order(OrderType.BUY).orderId(requestId).build();

        assertEquals(first.getOrderId(), retry.getOrderId());
        assertEquals(OrderStatus.PENDING, retry.getStatus());
        assertEquals(0L, retry.getExecutedQuantity());
        assertThrows(IllegalArgumentException.class, () -> order(OrderType.BUY).quantity(0).build());
    }

    @Test
    void reservationMustMatchOrderSideAndResource() {
        TradeOrder buy = order(OrderType.BUY).build();
        TradeOrder sell = order(OrderType.SELL).build();
        var cash = OrderReservation.builder().order(buy).reservationType(ReservationType.CASH)
                .currency(Currency.USD).remainingCash(BigDecimal.TEN);
        var stock = OrderReservation.builder().order(sell).reservationType(ReservationType.STOCK)
                .remainingQuantity(1L);

        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            assertTrue(validator.validate(cash.build()).isEmpty());
            assertTrue(validator.validate(stock.build()).isEmpty());
            assertFalse(validator.validate(cash.remainingQuantity(1L).build()).isEmpty());
            assertFalse(validator.validate(stock.order(buy).build()).isEmpty());
        }
    }

    @Test
    void partialFillKeepsItsTradingCurrencyFeeAndHistory() {
        TradeOrder order = order(OrderType.BUY).build();
        order.markValidated();

        assertThrows(IllegalArgumentException.class, () -> order.recordExecution(
                "fill-1", BigDecimal.TEN, 1, Currency.USD, new BigDecimal("-0.25"), OCCURRED_AT));
        assertEquals(0L, order.getExecutedQuantity());

        var fill = order.recordExecution(
                "fill-1", BigDecimal.TEN, 1, Currency.USD, new BigDecimal("0.25"), OCCURRED_AT);
        var history = OrderHistory.builder().order(order).previousStatus(OrderStatus.VALIDATED)
                .newStatus(order.getStatus()).occurredAt(OCCURRED_AT).build();

        assertEquals("fill-1", fill.getExecutionKey());
        assertEquals(Currency.USD, fill.getCurrency());
        assertEquals(new BigDecimal("0.2500"), fill.getFee());
        assertEquals(1L, order.getExecutedQuantity());
        assertEquals(OrderStatus.PARTIALLY_FILLED, history.getNewStatus());
        assertSame(order, history.getOrder());
    }

    private TradeOrder.TradeOrderBuilder order(OrderType orderType) {
        Account account = Account.builder().user(User.builder().build()).build();
        return TradeOrder.builder().orderId(UUID.randomUUID()).account(account)
                .securityMaster(SecurityMaster.builder().build()).orderType(orderType)
                .orderMethod(OrderMethod.MARKET).quantity(2);
    }
}
