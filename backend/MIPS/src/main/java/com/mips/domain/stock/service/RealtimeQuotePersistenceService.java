package com.mips.domain.stock.service;

import com.mips.domain.stock.dto.RealtimeQuoteCache;
import com.mips.domain.stock.entity.SecurityMaster;
import com.mips.domain.stock.entity.SecurityQuote;
import com.mips.domain.stock.enums.Exchange;
import com.mips.domain.stock.repository.SecurityMasterRepository;
import com.mips.domain.stock.repository.SecurityQuoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RealtimeQuotePersistenceService {

    private final RedisQuoteStore redisQuoteStore;
    private final SecurityMasterRepository securityMasterRepository;
    private final SecurityQuoteRepository securityQuoteRepository;

    @Transactional
    public void saveLatestQuotes() {
        List<RealtimeQuoteCache> quotes =
                redisQuoteStore.findAllLatest();
        for (RealtimeQuoteCache cache : quotes) {
            // ticker로 SecurityMaster
            Optional<SecurityMaster> sm = securityMasterRepository.findSecurity(cache.ticker(), Exchange.NASDAQ);
            // SecurityQuote 조회
            if (sm.isPresent()) {
                Optional<SecurityQuote> sq = securityQuoteRepository.findByTickerAndExchange(sm.get().getTicker(), sm.get().getExchange());
                // 현재가, 시세 시각 갱신
                sq.ifPresent(q -> {
                    q.updateLastPrice(cache.currentPrice(),
                            cache.quotedAt());
                    securityQuoteRepository.save(q);
                });
            }
        }
    }
}