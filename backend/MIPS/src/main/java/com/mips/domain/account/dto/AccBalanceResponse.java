package com.mips.domain.account.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import com.mips.domain.comm.enums.Currency;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccBalanceResponse {

    private String email;
    private BigDecimal balance;

    private List<Cash> cash;
    private Currency currency;

    // TODO 원화기준이기 때문에 달러기준 환율 표시 필요
    public List<Cash> getLockCashed(BigDecimal lockCash) {
        List<Cash> cash = new ArrayList<>();

        if (lockCash == null || lockCash.compareTo(BigDecimal.ZERO) <= 0) {
            for (int i = 0; i < Currency.values().length; i++) {

            }
        }

        return cash;
    }

}
