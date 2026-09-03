package com.mips.domain.stock.entity;

import com.mips.domain.comm.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "security_quote", schema = "finance")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SecurityQuote extends BaseTimeEntity {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "security_id")
    private SecurityMaster security;

    @Column(name = "current_price", precision = 19, scale = 4)
    private BigDecimal currentPrice;

    @Column(name = "high_price", precision = 10, scale = 2)
    private BigDecimal highPrice;

    @Column(name = "low_price", precision = 10, scale = 2)
    private BigDecimal lowPrice;

    @Column(name = "open_price", precision = 10, scale = 2)
    private BigDecimal openPrice;

    @Column(name = "prev_close", precision = 10, scale = 2)
    private BigDecimal prevClose;

    @Column(name = "change_price", precision = 10, scale = 2)
    private BigDecimal changePrice;

    @Column(name = "percentage_change", precision = 5, scale = 2)
    private BigDecimal percentageChange;

    @Column(name = "volume")
    private Long volume; // 거래량

    @Column(name = "quoted_at", nullable = false)
    private LocalDateTime quotedAt; // 주가기준일

    @Builder
    public SecurityQuote(SecurityMaster security,
                         BigDecimal currentPrice,
                         BigDecimal changePrice,
                         BigDecimal percentageChange,
                         BigDecimal highPrice,
                         BigDecimal lowPrice,
                         BigDecimal openPrice,
                         BigDecimal prevClose,
                         long quotedAt) {
        this.security = security;
        this.currentPrice = currentPrice;
        this.changePrice = changePrice;
        this.percentageChange = percentageChange;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.openPrice = openPrice;
        this.prevClose = prevClose;

        Instant instant = Instant.ofEpochSecond(quotedAt);
        this.quotedAt = LocalDateTime.ofInstant(
                instant,
                ZoneId.of("Asia/Seoul")
        );
    }

    public void changeSecurity(SecurityMaster security){
        this.security = security;
    }

    public void update(BigDecimal currentPrice,
                       BigDecimal changePrice,
                       BigDecimal percentageChange,
                       BigDecimal highPrice,
                       BigDecimal lowPrice,
                       BigDecimal openPrice,
                       BigDecimal prevClose,
                       Long volume,
                       LocalDateTime quotedAt) {
        this.currentPrice = currentPrice;
        this.changePrice = changePrice;
        this.percentageChange = percentageChange;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.openPrice = openPrice;
        this.prevClose = prevClose;
        this.volume = volume;
        this.quotedAt = quotedAt;
    }

}
