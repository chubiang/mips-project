package com.mips.domain.stock.dto;

import java.math.BigDecimal;

public record NasdaqDataRow(
        String symbol,
        String sector,
        String companyName,
        String marketCap,
        String lastSalePrice,
        String netChange,
        String percentageChange,
        String deltaIndicator
) {

    public BigDecimal castLastSalePrice() {
        return toBigDecimal(lastSalePrice);
    }

    public BigDecimal castNetChange() {
        return toBigDecimal(netChange);
    }

    public BigDecimal castPercentageChange() {
        if (percentageChange == null ||
                percentageChange.isBlank() ||
                "UNCH".equalsIgnoreCase(percentageChange)) { // UNCH:변동없음 일 경우
            return BigDecimal.ZERO;
        }

        return new BigDecimal(
                percentageChange
                        .replace("%", "")
                        .replace("+", "")
        );
    }

    private static BigDecimal toBigDecimal(String value) {

        if (value == null ||
                value.isBlank() ||
                "UNCH".equalsIgnoreCase(value)) { // UNCH:변동없음 일 경우
            return BigDecimal.ZERO;
        }

        return new BigDecimal(
                value
                        .replace("$", "")
                        .replace(",", "")
                        .replace("+", "")
        );
    }
}