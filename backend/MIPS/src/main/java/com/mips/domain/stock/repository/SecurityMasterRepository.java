package com.mips.domain.stock.repository;

import com.mips.domain.stock.entity.SecurityMaster;
import com.mips.domain.stock.enums.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SecurityMasterRepository extends JpaRepository<SecurityMaster, Long> {

    List<SecurityMaster> findAllByExchangeAndIsActive(Exchange exchange, boolean isActive);

    @Query("""
    select sm
      from SecurityMaster sm
     where sm.ticker = :ticker
       and sm.exchange = :exchange
""")
    Optional<SecurityMaster> findSecurity(
            @Param("ticker") String ticker,
            @Param("exchange") Exchange exchange
    );
}
