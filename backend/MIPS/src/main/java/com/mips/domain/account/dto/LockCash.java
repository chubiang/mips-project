package com.mips.domain.account.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LockCash {
    private String currency;
    private BigDecimal amount;
}
