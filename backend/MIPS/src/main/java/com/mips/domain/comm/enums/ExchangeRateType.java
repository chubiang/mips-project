package com.mips.domain.comm.enums;

public enum ExchangeRateType {
    DEAL_BASE,             // 매매기준율, 단순 조회/평가
    CUSTOMER_BUYS_FOREIGN, // 고객이 외화를 살 때
    CUSTOMER_SELLS_FOREIGN // 고객이 외화를 팔 때
}
