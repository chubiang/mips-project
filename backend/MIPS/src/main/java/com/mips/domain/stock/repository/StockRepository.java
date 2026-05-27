package com.mips.domain.stock.repository;

import com.mips.domain.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByTicker(String ticker);

    List<Stock> findBySector(String sector);
}
