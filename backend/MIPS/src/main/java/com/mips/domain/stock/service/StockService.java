package com.mips.domain.stock.service;

import com.mips.domain.stock.entity.StockDetail;

import java.util.List;

public interface StockService {
    public StockDetail findByTicker(String ticker);
    public List<StockDetail> findAll();
}
