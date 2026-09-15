package com.mips.domain.stock.controller;

import com.mips.domain.stock.entity.StockDetail;
import com.mips.domain.stock.dto.RealtimeStockResponse;
import com.mips.domain.comm.dto.ApiResponse;
import com.mips.domain.stock.service.UsStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.mips.domain.stock.service.RealtimeQuoteSseService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stock/us")
public class UsStockController {

    final private UsStockService usStockService;
    final private RealtimeQuoteSseService realtimeQuoteSseService;

    @GetMapping("/{ticker}")
    public StockDetail getStock(@PathVariable String ticker) {
        // DB에서 AAPL 종목 찾아서 리턴!
        return usStockService.findByTicker(ticker);
    }

    @GetMapping("/realtime")
    public ApiResponse<List<RealtimeStockResponse>> getRealtimeQuotes() {
        return ApiResponse.success(usStockService.findAllRealtimeQuotes());
    }

    @GetMapping(value = "/realtime/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamRealtimeQuotes() {
        return realtimeQuoteSseService.subscribe();
    }
//    @GetMapping("/all")
//    public List<StockResponse> getAllStocks() {
//        List<StockDetail> list = usStockService.findAll();
//        return list.stream()
//                   .map(StockResponse::new)
//                   .toList();
//    }

}
