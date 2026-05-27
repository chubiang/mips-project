package com.mips.domain.order.entity;

import com.mips.domain.user.entity.User;
import com.mips.domain.comm.entity.BaseTimeEntity;
import com.mips.domain.order.enums.OrderStatus;
import com.mips.domain.order.enums.OrderType;
import com.mips.domain.stock.entity.Stock;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "trade_order", schema = "finance")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TradeOrder extends BaseTimeEntity {

    @Id
    @Column(name = "order_id", length = 36)
    private String orderId; // 카프카 중복 처리 방지를 위한 멱등성 키(UUID)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticker", referencedColumnName = "ticker")
    private Stock stock;

    @Enumerated(EnumType.STRING) // Enum 이름을 DB에 그대로 문자열로 저장
    @Column(name = "order_type", nullable = false, length = 10)
    private OrderType orderType;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal price;

    @Column(nullable = false)
    private Long quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;
    
    // ★ 마법: JPA가 DB에 INSERT 쿼리를 날리기 '직전'에 자동으로 UUID와 초기 상태를 세팅해 줍니다.
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