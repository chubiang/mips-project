package com.mips.domain.account.dto;

public record AccBalanceRequest(
        String email,
        String refreshToken
) {
}
