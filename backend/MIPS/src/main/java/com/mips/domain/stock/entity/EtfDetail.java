package com.mips.domain.stock.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

//@Entity
//@Table(name = "etf_detail", schema = "finance")
public class EtfDetail {
//    @Id
    private Long id;

//    @OneToOne(fetch = FetchType.LAZY)
//    @MapsId
//    @JoinColumn(name = "security_id")
    private SecurityMaster security;

    private String issuer;

    private String benchmark;

    private BigDecimal expenseRatio;
}
