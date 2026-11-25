package com.mips.domain.order.entity;

import com.mips.domain.comm.entity.BaseTimeEntity;
import com.mips.domain.comm.enums.Currency;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "trade_execution", schema = "finance", indexes = {
        @Index(name = "idx_trade_execution_order", columnList = "order_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TradeExecution extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "execution_key", nullable = false, updatable = false, unique = true, length = 100)
    private String executionKey;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, updatable = false)
    private TradeOrder order;

    @Column(name = "executed_price", nullable = false, updatable = false,
            precision = TradeOrder.PRICE_PRECISION, scale = TradeOrder.PRICE_SCALE)
    private BigDecimal executedPrice;

    @Column(name = "executed_quantity", nullable = false, updatable = false)
    private long executedQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, length = 3)
    private Currency currency;

    @Column(nullable = false, updatable = false,
            precision = TradeOrder.PRICE_PRECISION, scale = TradeOrder.PRICE_SCALE)
    private BigDecimal fee;

    @Column(name = "executed_at", nullable = false, updatable = false)
    private LocalDateTime executedAt;

    TradeExecution(TradeOrder order, String executionKey, BigDecimal executedPrice, long executedQuantity,
                   Currency currency, BigDecimal fee, LocalDateTime executedAt) {
        if (executionKey == null || executionKey.isBlank() || executionKey.length() > 100) {
            throw new IllegalArgumentException("체결 키는 비어 있을 수 없으며 100자 이하여야 합니다.");
        }
        if (fee == null || fee.signum() < 0) {
            throw new IllegalArgumentException("체결 수수료는 0 이상이어야 합니다.");
        }
        this.order = order;
        this.executionKey = executionKey;
        this.executedPrice = executedPrice;
        this.executedQuantity = executedQuantity;
        this.currency = Objects.requireNonNull(currency, "체결 통화는 필수입니다.");
        this.fee = TradeOrder.normalizeAmount(fee);
        this.executedAt = Objects.requireNonNull(executedAt, "체결 시각은 필수입니다.");
    }
}
