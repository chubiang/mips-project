package com.mips.domain.stock.repository;

import com.mips.domain.stock.entity.SecurityQuote;
import com.mips.domain.stock.enums.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SecurityQuoteRepository extends JpaRepository<SecurityQuote, Long> {

    @Query("""
                select sq
                  from SecurityQuote sq
                 where sq.security.ticker = :ticker
                   and sq.security.exchange = :exchange
            """)
    List<SecurityQuote> findByTickerAndExchange(@Param("ticker") String ticker,
                                                @Param("exchange") Exchange exchange);

}
