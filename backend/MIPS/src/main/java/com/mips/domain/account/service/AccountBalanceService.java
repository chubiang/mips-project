package com.mips.domain.account.service;

import com.mips.domain.account.dto.AccBalanceRequest;
import com.mips.domain.account.dto.AccBalanceResponse;
import com.mips.domain.account.entity.AccountBalance;
import com.mips.domain.account.repository.AccountBalanceRepository;
import com.mips.domain.user.entity.User;
import com.mips.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountBalanceService {

    private final AccountBalanceRepository accountBalanceRepository;
    private final UserService userService;

    public AccBalanceResponse getAccBalance(AccBalanceRequest request) {
        User user = userService.getUserByRefreshToken(request.refreshToken());
        AccountBalance acc = accountBalanceRepository.findByUserId(user.getId())
                                .orElseThrow(() -> new IllegalArgumentException("회원정보를 찾을 수 없습니다."));

        //acc.getLockedCash(); // TODO 충전금액 - 원화 기준

        return AccBalanceResponse.builder()
                .email(request.email())
                .balance(acc.getAvailableCash())
//                .lockCashes()
                .build();
    }

}
