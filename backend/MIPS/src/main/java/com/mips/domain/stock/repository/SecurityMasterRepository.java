package com.mips.domain.stock.repository;

import com.mips.domain.stock.entity.SecurityMaster;
import com.mips.domain.stock.enums.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SecurityMasterRepository extends JpaRepository<SecurityMaster, Long> {

    List<SecurityMaster> findAllByExchangeAndIsActive(Exchange exchange, boolean isActive);

}
