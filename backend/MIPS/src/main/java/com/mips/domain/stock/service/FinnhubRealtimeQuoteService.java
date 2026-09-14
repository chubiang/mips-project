package com.mips.domain.stock.service;

import com.mips.domain.stock.dto.FinnhubTradeData;
import com.mips.domain.stock.dto.RealtimeQuoteCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class FinnhubRealtimeQuoteService {

    private final RedisQuoteStore redisQuoteStore;

    public void saveLatestPrice(FinnhubTradeData data) {
        if (data.s() == null || data.p() == null || data.t() == null) {
            return;
        }

        RealtimeQuoteCache quote = new RealtimeQuoteCache(
                data.s(),
                data.p(),
                Instant.ofEpochMilli(data.t())
        );

        redisQuoteStore.save(quote);
    }
}
