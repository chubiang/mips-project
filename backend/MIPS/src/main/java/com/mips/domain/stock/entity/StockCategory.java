package com.mips.domain.stock.entity;

import com.mips.domain.comm.entity.BaseTimeEntity;
import com.mips.domain.stock.enums.CategoryLevel;
import com.mips.domain.stock.enums.ClassificationType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stock_category", schema = "finance")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class StockCategory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "classification_type", length = 10)
    private ClassificationType classificationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "category_level", nullable = false, length = 30)
    private CategoryLevel level;

    @Column(name ="parent_id")
    private Long parentId;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

}
