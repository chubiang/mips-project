package com.mips.domain.account.service;

import com.mips.domain.account.dto.AccBalanceRequest;
import com.mips.domain.account.dto.AccBalanceResponse;
import com.mips.domain.account.entity.Account;
import com.mips.domain.account.entity.AccountBalance;
import com.mips.domain.account.repository.AccountBalanceRepository;
import com.mips.domain.account.repository.AccountRepository;
import com.mips.domain.comm.service.CurrencyConversionService;
import com.mips.domain.user.entity.User;
import com.mips.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountBalanceService {

    private final AccountBalanceRepository accountBalanceRepository;
    private final UserService userService;
    private final CurrencyConversionService currencyConversionService;
    private final AccountRepository accountRepository;

    public AccBalanceResponse getAccBalance(AccBalanceRequest request) {
        User user = userService.getUserByRefreshToken(request.refreshToken());
        Account acc = accountRepository.findByUserId(user.getId())
                                .orElseThrow(() -> new IllegalArgumentException("회원정보를 찾을 수 없습니다."));
        AccountBalance accBalance = accountBalanceRepository.findByAccountId(acc.getId())
                .orElseThrow(() -> new IllegalArgumentException("계좌정보를 찾을 수 없습니다."));
        // 투자금액 조회

        Map<String, BigDecimal> lockCashes = null;



        return AccBalanceResponse.builder()
                .email(request.email())
                .balance(accBalance.getAvailableCash())
//                .lockCashes()
                .build();
    }

}
