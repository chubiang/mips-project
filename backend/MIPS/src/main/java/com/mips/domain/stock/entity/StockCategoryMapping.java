package com.mips.domain.stock.entity;

import com.mips.domain.comm.entity.BaseTimeEntity;
import com.mips.domain.stock.enums.DataProvider;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stock_category_mapping", schema = "finance")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockCategoryMapping extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "security_id", nullable = false)
    private SecurityMaster security;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private StockCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_provider", nullable = false)
    private DataProvider dataProvider;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}