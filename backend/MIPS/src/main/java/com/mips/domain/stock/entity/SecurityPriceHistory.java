package com.mips.domain.stock.entity;

import com.mips.domain.comm.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "security_price_history",
        schema = "finance",
        indexes = {
                @Index(
                        name = "idx_security_price_history_security_time",
                        columnList = "security_id, quoted_at"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SecurityPriceHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "security_id", nullable = false)
    private SecurityMaster security;

    @Column(name = "price", nullable = false, precision = 19, scale = 4)
    private BigDecimal price; // 시가

    @Column(name = "change_amount", precision = 19, scale = 4)
    private BigDecimal changeAmount; // 변동금액

    @Column(name = "percentage_change", precision = 8, scale = 4)
    private BigDecimal percentageChange; // 변동율

    @Column(name = "volume")
    private Long volume; // 거래량

    @Column(name = "quoted_at", nullable = false)
    private LocalDateTime quotedAt; // 결정시간

    @Builder
    public SecurityPriceHistory(SecurityMaster security,
                                BigDecimal price,
                                BigDecimal changeAmount,
                                BigDecimal percentageChange,
                                Long volume,
                                LocalDateTime quotedAt) {
        this.security = security;
        this.price = price;
        this.changeAmount = changeAmount;
        this.percentageChange = percentageChange;
        this.volume = volume;
        this.quotedAt = quotedAt;
    }
}