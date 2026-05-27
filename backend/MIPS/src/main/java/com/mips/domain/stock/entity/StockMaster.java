package com.mips.domain.stock.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stock_master", schema = "finance")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StockMaster {

    // 이번에는 DB가 번호를 매겨주는 SERIAL(Long)이 아니라, 우리가 직접 넣는 ticker가 PK입니다!
    @Id
    @Column(name = "ticker", length = 10, nullable = false)
    private String ticker;

    @Column(name = "company_name", length = 100, nullable = false)
    private String companyName;

    @Builder.Default
    @Column(name = "asset_type", length = 10)
    private String assetType = "STOCK";

    @Column(name = "sector", length = 50)
    private String sector;

    @Builder.Default
    @Column(name = "has_components", nullable = false)
    private boolean hasComponents = false;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}