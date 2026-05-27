package com.mips.domain.stock.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"etf_ticker", "component_ticker"}) // 한 ETF에 동일 종목 중복 방지
}, name = "etf_component", schema = "finance")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class EtfComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticker", length = 10, unique = true, nullable = false)
    private String ticker;

    @Column(name = "company_name", length = 100)
    private String companyName;

    @Column(name = "asset_type", length = 10)
    private String assetType;

    @Column(name = "sector", length = 50)
    private String sector;

    // JPA를 통해 Insert 할 때 null이 들어가는 것을 방지하고 기본값 'ACTIVE' 유지
    @Builder.Default
    @Column(name = "status", length = 20)
    private String status = "ACTIVE";

    @Column(name = "has_components", nullable = false)
    private boolean hasComponents;

    // 💡 금융/돈 관련 소수점 데이터는 오차 방지를 위해 무조건 BigDecimal을 사용합니다!
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

    // 데이터가 업데이트될 때마다 자동으로 현재 시간을 찍어줍니다.
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}