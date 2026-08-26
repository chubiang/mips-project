package com.mips.domain.account.service;

import com.mips.domain.account.dto.AccBalanceResponse;
import com.mips.domain.account.entity.Account;
import com.mips.domain.account.entity.AccountBalance;
import com.mips.domain.account.enums.AccountStatus;
import com.mips.domain.account.repository.AccountBalanceRepository;
import com.mips.domain.account.repository.AccountRepository;
import com.mips.domain.user.entity.User;
import com.mips.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountBalanceService {

    private final AccountBalanceRepository accountBalanceRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccBalanceResponse getAccBalance(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원정보를 찾을 수 없습니다."));
        Account acc = accountRepository.findByUserId(user.getId(), AccountStatus.ACTIVE)
                                .orElseThrow(() -> new IllegalArgumentException("회원정보를 찾을 수 없습니다."));
        AccountBalance accBalance = accountBalanceRepository.findByAccountId(acc.getId())
                .orElseThrow(() -> new IllegalArgumentException("계좌정보를 찾을 수 없습니다."));
        return AccBalanceResponse.builder()
                .email(email)
                .balance(accBalance.getAvailableCash())
                .currency(accBalance.getCurrency())
//                .lockCashes()
                .build();
    }

}
