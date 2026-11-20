package com.mips.domain.order.entity;

import com.mips.domain.comm.entity.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.mips.domain.comm.enums.Currency;
import com.mips.domain.order.enums.*;

/** 주문별 남은 예약 자원. BUY는 현금, SELL은 주식 수량을 예약한다.
 * 계좌 잔액/보유 수량과의 동기화 및 예약 소진/해제는 서비스 트랜잭션에서 처리한다.
 */
@Entity
@Table(name = "order_reservation", schema = "finance",
       indexes = @Index(name = "idx_order_reservation_order", columnList = "order_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class OrderReservation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true, updatable = false)
    @NotNull
    private TradeOrder order;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ReservationType reservationType;

    @Enumerated(EnumType.STRING)
    @Column(length = 3)
    private Currency currency;

    @NotNull @PositiveOrZero @Digits(integer = 15, fraction = 4)
    @Builder.Default
    @Column(name = "remaining_cash", nullable = false, precision = 19, scale = 4)
    private BigDecimal remainingCash = BigDecimal.ZERO;

    @NotNull @PositiveOrZero
    @Builder.Default
    @Column(name = "remaining_quantity", nullable = false)
    private Long remainingQuantity = 0L;

    @Version
    private Long version;

    @AssertTrue(message = "매수는 현금과 통화, 매도는 주식 수량만 예약해야 합니다.")
    public boolean isResourceConsistent() {
        if (order == null || reservationType == null || remainingCash == null || remainingQuantity == null) {
            return false;
        }
        return switch (reservationType) {
            case CASH -> order.getOrderType() == OrderType.BUY && currency != null && remainingQuantity == 0;
            case STOCK -> order.getOrderType() == OrderType.SELL && currency == null && remainingCash.signum() == 0;
        };
    }
}
