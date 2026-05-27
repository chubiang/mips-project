package com.mips.domain.stock.service;

import com.mips.domain.stock.entity.Stock;
import com.mips.domain.stock.repository.StockRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsStockService implements StockService{

    @PersistenceContext
    private EntityManager entityManager;

    private final StockRepository stockRepository;

    @Override
    public Stock findByTicker(String ticker) {
        return null;
    }

    @Override
    public List<Stock> findAll() {
        return stockRepository.findAll();
    }
}
