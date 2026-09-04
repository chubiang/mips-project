package com.mips.domain.stock.repository;

import com.mips.domain.stock.entity.SecurityPriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityPriceHistoryRepository extends JpaRepository<SecurityPriceHistory, Long> {

}
