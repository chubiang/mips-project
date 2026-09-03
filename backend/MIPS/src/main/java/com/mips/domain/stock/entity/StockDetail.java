package com.mips.domain.stock.entity;

import com.mips.domain.stock.enums.SecurityType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
// 💡 핵심: 스키마가 'finance'로 지정되어 있으므로 schema 속성을 꼭 넣어줘야 합니다!
@Table(name = "stock_detail", schema = "finance")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StockDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "security_id")
    private SecurityMaster security;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;    // 회사명

    @Column(name = "company_abbrev", nullable = false, length = 100)
    private String companyAbbrev; // 회사 축약어

    @Column(name = "market_cap")
    private BigDecimal marketCap;  // 시가총액

    @Column(name = "shares_outstanding")
    private Long sharesOutstanding; // 발행주식수

    @Column(name = "ipo_date")
    private LocalDate ipoDate;      // 상장일

    @Builder.Default
    @Column(length = 20)
    private String status = "ACTIVE";

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

}