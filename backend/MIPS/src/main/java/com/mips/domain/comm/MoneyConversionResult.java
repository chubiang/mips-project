package com.mips.domain.comm;

import com.mips.domain.comm.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class MoneyConversionResult {
    private BigDecimal amount;
    private Currency from;
    private BigDecimal convertedAmount;
    private Currency to;
    private BigDecimal rate;
    private LocalDate rateDate;
}
