package com.mips.domain.account.controller;

import com.mips.domain.account.dto.AccBalanceRequest;
import com.mips.domain.account.dto.AccBalanceResponse;
import com.mips.domain.account.service.AccountBalanceService;
import com.mips.domain.comm.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping( "/api/acc/")
public class AccountBalanceController {

    private final AccountBalanceService accountBalanceService;

    @GetMapping("/get")
    public ResponseEntity<?>  getAccBalance(AccBalanceRequest request) {
        AccBalanceResponse response = accountBalanceService.getAccBalance(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
