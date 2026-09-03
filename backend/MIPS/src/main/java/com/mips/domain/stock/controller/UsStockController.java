package com.mips.domain.stock.controller;

import com.mips.domain.comm.dto.ApiResponse;
import com.mips.domain.stock.dto.StockResponse;
import com.mips.domain.stock.entity.StockDetail;
import com.mips.domain.stock.service.UsStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping( "/api/stock/us/")
public class UsStockController {

    final private UsStockService usStockService;

    @GetMapping("/{ticker}")
    public StockDetail getStock(@PathVariable String ticker) {
        // DB에서 AAPL 종목 찾아서 리턴!
        return usStockService.findByTicker(ticker);
    }
//    @GetMapping("/all")
//    public List<StockResponse> getAllStocks() {
//        List<StockDetail> list = usStockService.findAll();
//        return list.stream()
//                   .map(StockResponse::new)
//                   .toList();
//    }

}
