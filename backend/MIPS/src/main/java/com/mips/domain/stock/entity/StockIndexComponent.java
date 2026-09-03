package com.mips.domain.stock.entity;

import com.mips.domain.comm.entity.BaseTimeEntity;
import com.mips.domain.stock.enums.StockIndex;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stock_index_component",
       schema = "finance",
       uniqueConstraints = {
            @UniqueConstraint(
                name = "uk_stock_index_component",
                columnNames = {
                        "security_id",
                        "stock_index"
                }
            )
       })
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockIndexComponent extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "security_id",
            nullable = false
    )
    private SecurityMaster security;

    @Enumerated(EnumType.STRING)
    @Column(name = "stock_index", length = 50, nullable = false)
    private StockIndex stockIndex;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Builder
    public StockIndexComponent(
            SecurityMaster security,
            StockIndex stockIndex
    ) {
        this.security = security;
        this.stockIndex = stockIndex;
        this.isActive = true;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
