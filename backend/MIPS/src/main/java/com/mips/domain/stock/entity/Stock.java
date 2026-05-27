package com.mips.domain.stock.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
// 💡 핵심: 스키마가 'finance'로 지정되어 있으므로 schema 속성을 꼭 넣어줘야 합니다!
@Table(name = "stock", schema = "finance")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticker", length = 10, nullable = false, unique = true)
    private String ticker;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(name = "asset_type", length = 10)
    private String assetType; // INDEX, STOCK, ETF, Warrant

    @Column(name = "sector", length = 50)
    private String sector;

    @Column(length = 20)
    private String status = "ACTIVE";

    @Column(name = "has_components", nullable = false)
    private Boolean hasComponents = false;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "change_amount", precision = 10, scale = 2)
    private BigDecimal changeAmount;

    @Column(name = "change_rate", precision = 5, scale = 2)
    private BigDecimal changeRate;

    @Column(name = "high_price", precision = 10, scale = 2)
    private BigDecimal highPrice;

    @Column(name = "low_price", precision = 10, scale = 2)
    private BigDecimal lowPrice;

    @Column(name = "open_price", precision = 10, scale = 2)
    private BigDecimal openPrice;

    @Column(name = "prev_close", precision = 10, scale = 2)
    private BigDecimal prevClose;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

}