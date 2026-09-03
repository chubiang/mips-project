package com.mips.domain.stock.dto;

import com.mips.domain.stock.entity.SecurityMaster;
import com.mips.domain.stock.entity.SecurityQuote;

import java.math.BigDecimal;

public record FinnhubQuoteResponse(
    BigDecimal c, // price
    BigDecimal d, // changePrice
    BigDecimal dp, // percentageChange
    BigDecimal h, // highPrice
    BigDecimal l, // lowPrice
    BigDecimal o, // openPrice
    BigDecimal pc, // prevClose
    long t // date
) {
}
