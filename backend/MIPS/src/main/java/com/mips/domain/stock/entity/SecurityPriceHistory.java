package com.mips.domain.stock.entity;

import com.mips.domain.comm.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
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
    private BigDecimal price;

    @Column(name = "volume")
    private Long volume; // 거래량

    @Column(name = "quoted_at", nullable = false)
    private LocalDateTime quotedAt; // 주가기준일
}