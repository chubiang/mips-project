package com.mips.domain.order.entity;

import com.mips.domain.account.entity.Account;
import com.mips.domain.comm.entity.BaseTimeEntity;
import com.mips.domain.comm.enums.Currency;
import com.mips.domain.order.enums.OrderMethod;
import com.mips.domain.order.enums.OrderStatus;
import com.mips.domain.order.enums.OrderType;
import com.mips.domain.stock.entity.SecurityMaster;
import com.mips.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "trade_order", schema = "finance")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TradeOrder extends BaseTimeEntity {

    static final int PRICE_PRECISION = 19;
    static final int PRICE_SCALE = 4;

    @Id
    @Column(name = "order_id", length = 36)
    private String orderId; // 주문 요청에서 발급한 멱등성 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private SecurityMaster securityMaster;

    @Enumerated(EnumType.STRING) // Enum 이름을 DB에 그대로 문자열로 저장
    @Column(name = "order_type", nullable = false, length = 10)
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_method", nullable = false, length = 10)
    private OrderMethod orderMethod;

    @Column(precision = PRICE_PRECISION, scale = PRICE_SCALE)
    private BigDecimal price;

    @Column(nullable = false)
    private Long quantity;

    // 가장 최근 체결의 단가와 시각은 조회용으로 보관한다.
    @Column(name = "executed_price", precision = PRICE_PRECISION, scale = PRICE_SCALE)
    private BigDecimal executedPrice;

    @Column(name = "executed_quantity", nullable = false)
    private Long executedQuantity = 0L;

    @Column(name = "executed_at")
    private LocalDateTime executedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Version
    @Column(nullable = false)
    @ColumnDefault("0")
    private Long version;

    @Builder
    public static TradeOrder place(UUID orderId, Account account, SecurityMaster securityMaster,
                                  OrderType orderType, OrderMethod orderMethod, BigDecimal price, long quantity) {
        TradeOrder order = new TradeOrder();
        order.orderId = Objects.requireNonNull(orderId, "주문 ID는 필수입니다.").toString();
        order.account = Objects.requireNonNull(account, "계좌는 필수입니다.");
        order.user = Objects.requireNonNull(account.getUser(), "계좌 소유자는 필수입니다.");
        order.securityMaster = Objects.requireNonNull(securityMaster, "종목은 필수입니다.");
        order.orderType = Objects.requireNonNull(orderType, "매수·매도 구분은 필수입니다.");
        order.orderMethod = Objects.requireNonNull(orderMethod, "주문 방식은 필수입니다.");
        if (quantity <= 0) {
            throw new IllegalArgumentException("주문 수량은 0보다 커야 합니다.");
        }
        if (orderMethod == OrderMethod.MARKET && price != null) {
            throw new IllegalArgumentException("시장가 주문은 지정 가격을 받지 않습니다.");
        }
        order.price = orderMethod == OrderMethod.LIMIT ? requirePrice(price) : null;
        order.quantity = quantity;
        order.status = OrderStatus.PENDING;
        return order;
    }

    public void markValidated() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("검증 대기 주문만 검증 완료로 변경할 수 있습니다.");
        }
        status = OrderStatus.VALIDATED;
    }

    // 반환한 체결과 주문 변경을 같은 트랜잭션에서 저장한다.
    public TradeExecution recordExecution(String executionKey, BigDecimal executionPrice, long executionQuantity,
                                          Currency currency, BigDecimal fee, LocalDateTime executionTime) {
        if (status != OrderStatus.VALIDATED && status != OrderStatus.PARTIALLY_FILLED) {
            throw new IllegalStateException("체결 대기 중인 주문만 체결할 수 있습니다.");
        }
        if (executionQuantity <= 0 || executionQuantity > quantity - executedQuantity) {
            throw new IllegalArgumentException("체결 수량은 0보다 크고 잔여 주문 수량 이하여야 합니다.");
        }
        BigDecimal fillPrice = requirePrice(executionPrice);
        if (orderMethod == OrderMethod.LIMIT) {
            int priceComparison = fillPrice.compareTo(price);
            if ((orderType == OrderType.BUY && priceComparison > 0)
                    || (orderType == OrderType.SELL && priceComparison < 0)) {
                throw new IllegalArgumentException("지정가보다 불리한 가격으로 체결할 수 없습니다.");
            }
        }
        TradeExecution execution = new TradeExecution(
                this, executionKey, fillPrice, executionQuantity, currency, fee, executionTime);
        executedQuantity += executionQuantity;
        if (executedAt == null || !executionTime.isBefore(executedAt)) {
            executedPrice = fillPrice;
            executedAt = executionTime;
        }
        status = executedQuantity.equals(quantity) ? OrderStatus.EXECUTED : OrderStatus.PARTIALLY_FILLED;
        return execution;
    }

    private static BigDecimal requirePrice(BigDecimal price) {
        if (price == null || price.signum() <= 0) {
            throw new IllegalArgumentException("가격은 0보다 커야 합니다.");
        }
        return normalizeAmount(price);
    }

    static BigDecimal normalizeAmount(BigDecimal amount) {
        BigDecimal normalized = amount.stripTrailingZeros();
        long integerDigits = (long) normalized.precision() - normalized.scale();
        if (normalized.scale() > PRICE_SCALE || integerDigits > PRICE_PRECISION - PRICE_SCALE) {
            throw new IllegalArgumentException("금액은 정수 15자리, 소수 4자리까지 입력할 수 있습니다.");
        }
        return normalized.setScale(PRICE_SCALE, RoundingMode.UNNECESSARY);
    }
}
