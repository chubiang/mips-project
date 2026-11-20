package com.mips.domain.order.entity;

import com.mips.domain.account.entity.Account;
import com.mips.domain.comm.entity.BaseTimeEntity;
import com.mips.domain.order.enums.OrderMethod;
import com.mips.domain.order.enums.OrderStatus;
import com.mips.domain.order.enums.OrderType;
import com.mips.domain.stock.entity.SecurityMaster;
import com.mips.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Builder;
import java.util.Objects;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "trade_order", schema = "finance")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TradeOrder extends BaseTimeEntity {

    @Id
    @Column(name = "order_id", length = 36)
    private String orderId; // 재시도 시 같은 UUID를 전달해야 동일 주문으로 식별 가능

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

    @Column(precision = 19, scale = 4)
    private BigDecimal price;

    @Column(nullable = false)
    private Long quantity;

    @Column(name = "executed_price", precision = 19, scale = 4)
    private BigDecimal executedPrice;

    @Column(name = "executed_quantity", nullable = false)
    private Long executedQuantity = 0L;

    @Column(name = "executed_at")
    private LocalDateTime executedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;
    
    @Version
    private Long version;

    /** 동일 요청 재시도에는 반드시 같은 orderId를 사용한다. 중복 응답 처리는 서비스 책임이다. */
    @Builder
    public TradeOrder(String orderId, User user, Account account, SecurityMaster securityMaster,
                      OrderType orderType, OrderMethod orderMethod, BigDecimal price, Long quantity) {
        this.orderId = UUID.fromString(Objects.requireNonNull(orderId, "orderId")).toString();
        this.user = Objects.requireNonNull(user, "user");
        this.account = Objects.requireNonNull(account, "account");
        this.securityMaster = Objects.requireNonNull(securityMaster, "securityMaster");
        this.orderType = Objects.requireNonNull(orderType, "orderType");
        this.orderMethod = Objects.requireNonNull(orderMethod, "orderMethod");
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("주문 수량은 0보다 커야 합니다.");
        }
        if (orderMethod == OrderMethod.LIMIT && (price == null || price.signum() <= 0)) {
            throw new IllegalArgumentException("지정가 주문에는 양수 가격이 필요합니다.");
        }
        if (orderMethod == OrderMethod.MARKET && price != null) {
            throw new IllegalArgumentException("시장가 주문의 지정 가격은 비워 두세요.");
        }
        this.price = price;
        this.quantity = quantity;
        this.status = OrderStatus.PENDING;
    }

    // JPA가 DB에 INSERT 쿼리를 날리기 '직전'에 자동으로 UUID와 초기 상태를 세팅해 줍니다.
    @PrePersist
    public void prePersist() {
        if (this.orderId == null) {
            this.orderId = UUID.randomUUID().toString();
        }
        if (this.status == null) {
            this.status = OrderStatus.PENDING;
        }
    }
}