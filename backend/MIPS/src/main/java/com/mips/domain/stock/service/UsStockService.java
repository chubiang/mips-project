package com.mips.domain.stock.service;

import com.mips.domain.stock.entity.StockDetail;
import com.mips.domain.stock.repository.StockDetailRepository;
import com.mips.domain.stock.repository.SecurityMasterRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UsStockService implements StockService{

    @PersistenceContext
    private EntityManager entityManager;

    private final StockDetailRepository stockDetailRepository;
    private final SecurityMasterRepository securityMasterRepository;

    public UsStockService(EntityManager entityManager,
                          StockDetailRepository stockDetailRepository,
                          SecurityMasterRepository securityMasterRepository) {
        this.entityManager = entityManager;
        this.stockDetailRepository = stockDetailRepository;
        this.securityMasterRepository = securityMasterRepository;
    }

    @Override
    public StockDetail findByTicker(String ticker) {
        return null;
    }

    @Override
    public List<StockDetail> findAll() {
        return stockDetailRepository.findAll();
    }

}
