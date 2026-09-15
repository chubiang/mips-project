package com.mips.domain.stock.service;

import com.mips.domain.stock.entity.StockDetail;
import com.mips.domain.stock.dto.RealtimeQuoteCache;
import com.mips.domain.stock.dto.RealtimeStockResponse;
import com.mips.domain.stock.entity.SecurityMaster;
import com.mips.domain.stock.enums.Exchange;
import com.mips.domain.stock.repository.SecurityMasterRepository;
import com.mips.domain.stock.repository.StockDetailRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UsStockService implements StockService{

    @PersistenceContext
    private EntityManager entityManager;

    private final StockDetailRepository stockDetailRepository;
    private final SecurityMasterRepository securityMasterRepository;
    private final RedisQuoteStore redisQuoteStore;

    public UsStockService(EntityManager entityManager,
                          StockDetailRepository stockDetailRepository,
                          SecurityMasterRepository securityMasterRepository,
                          RedisQuoteStore redisQuoteStore) {
        this.entityManager = entityManager;
        this.stockDetailRepository = stockDetailRepository;
        this.securityMasterRepository = securityMasterRepository;
        this.redisQuoteStore = redisQuoteStore;
    }

    @Override
    public StockDetail findByTicker(String ticker) {
        return null;
    }

    @Override
    public List<StockDetail> findAll() {
        return stockDetailRepository.findAll();
    }

    public List<RealtimeStockResponse> findAllRealtimeQuotes() {
        Map<String, SecurityMaster> securitiesByTicker = securityMasterRepository
                .findAllByExchangeAndIsActive(Exchange.NASDAQ, true)
                .stream()
                .collect(Collectors.toMap(
                        SecurityMaster::getTicker,
                        Function.identity(),
                        (first, ignored) -> first
                ));

        return redisQuoteStore.findAllLatest().stream()
                .map(quote -> toRealtimeResponse(quote, securitiesByTicker.get(quote.ticker())))
                .sorted(java.util.Comparator.comparing(RealtimeStockResponse::ticker))
                .toList();
    }

    private RealtimeStockResponse toRealtimeResponse(
            RealtimeQuoteCache quote,
            SecurityMaster security
    ) {
        return new RealtimeStockResponse(
                quote.ticker(),
                security == null ? quote.ticker() : security.getName(),
                security == null ? null : security.getSecurityType(),
                quote.currentPrice(),
                quote.quotedAt()
        );
    }

}
