package com.mips.domain.stock.entity;

import com.mips.domain.comm.entity.BaseTimeEntity;
import com.mips.domain.stock.enums.Exchange;
import com.mips.domain.stock.enums.SecurityType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "security_master",
        schema = "finance",
        uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {
                    "ticker",
                    "exchange"
            }
        )
})
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SecurityMaster extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticker", length = 10, nullable = false)
    private String ticker;

    @Enumerated(EnumType.STRING)
    @Column(name = "exchange", length = 20)
    private Exchange exchange;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "code", length = 100)
    private String code;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "security_type", length = 10)
    private SecurityType securityType = SecurityType.STOCK;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    public void setSecurityMaster(
            String ticker,
            Exchange exchange,
            String name
    ) {
        this.ticker = ticker;
        this.exchange = exchange;
        this.name = name;
        this.securityType = SecurityType.STOCK;
        this.isActive = true;
    }

}