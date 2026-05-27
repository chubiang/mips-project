package com.mips.domain.stock.dto;

import com.mips.domain.stock.entity.Stock;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class StockResponse {

    private final String ticker;
    private final String companyName;
    private final String assetType;
    private final String sector;
    private final String status;

    private final BigDecimal price;
    private final BigDecimal changeAmount;
    private final BigDecimal changeRate;
    private final BigDecimal highPrice;
    private final BigDecimal lowPrice;
    private final BigDecimal openPrice;
    private final BigDecimal prevClose;
    private final LocalDateTime updatedAt;

    public StockResponse(Stock stock) {
        this.ticker = stock.getTicker();
        this.companyName = stock.getCompanyName();
        this.assetType = stock.getAssetType();
        this.sector = stock.getSector();
        this.status = stock.getStatus();
        this.price = stock.getPrice();
        this.changeAmount = stock.getChangeAmount();
        this.changeRate = stock.getChangeRate();
        this.highPrice = stock.getHighPrice();
        this.lowPrice = stock.getLowPrice();
        this.openPrice = stock.getOpenPrice();
        this.prevClose = stock.getPrevClose();
        this.updatedAt = stock.getUpdatedAt();
    }
}
