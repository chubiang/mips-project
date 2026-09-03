package com.mips.domain.stock.repository;

import com.mips.domain.stock.entity.StockDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockDetailRepository extends JpaRepository<StockDetail, Long> {


}
