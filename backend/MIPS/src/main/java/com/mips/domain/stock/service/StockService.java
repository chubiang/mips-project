package com.mips.domain.stock.service;

import com.mips.domain.stock.entity.Stock;

import java.util.List;

public interface StockService {
    public Stock findByTicker(String ticker);
    public List<Stock> findAll();
}
