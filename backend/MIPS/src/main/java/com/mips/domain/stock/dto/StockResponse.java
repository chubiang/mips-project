package com.mips.domain.stock.dto;

import com.mips.domain.stock.entity.SecurityQuote;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class StockResponse {

    private final String ticker;
    private final String companyName;
    private final String assetType;

    private final BigDecimal price;
    private final BigDecimal changeAmount;
    private final BigDecimal changeRate;
    private final BigDecimal highPrice;
    private final BigDecimal lowPrice;
    private final BigDecimal openPrice;
    private final BigDecimal prevClose;
    private final LocalDateTime updatedAt;

    public StockResponse(SecurityQuote quote) {
        this.ticker = quote.getSecurity().getTicker();
        this.companyName = quote.getSecurity().getName();
        this.assetType = quote.getSecurity().getSecurityType().toString();
        this.price = quote.getCurrentPrice();
        this.changeAmount = quote.getCurrentPrice().subtract(quote.getPrevClose());
        this.changeRate = quote.getPercentageChange();
        this.highPrice = quote.getHighPrice();
        this.lowPrice = quote.getLowPrice();
        this.openPrice = quote.getOpenPrice();
        this.prevClose = quote.getPrevClose();
        this.updatedAt = quote.getUpdatedAt();
    }
}
