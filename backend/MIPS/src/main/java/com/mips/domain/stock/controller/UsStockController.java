package com.mips.domain.stock.controller;

import com.mips.domain.stock.dto.StockResponse;
import com.mips.domain.stock.entity.Stock;
import com.mips.domain.stock.service.UsStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping( "/api/stock/us/")
public class UsStockController {

    final private UsStockService usStockService;

    @GetMapping("/{ticker}")
    public Stock getStock(@PathVariable String ticker) {
        // DB에서 AAPL 종목 찾아서 리턴!
        return usStockService.findByTicker(ticker);
    }
    @GetMapping("/all")
    public List<StockResponse> getAllStocks() {
        List<Stock> list = usStockService.findAll();
        return list.stream()
                   .map(StockResponse::new)
                   .toList();
    }
}
