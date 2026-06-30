package com.mips.domain.charge.controller;


import com.mips.domain.charge.dto.ChargeRequest;
import com.mips.domain.charge.dto.ChargeResponse;
import com.mips.domain.charge.service.ChargeService;
import com.mips.domain.comm.dto.ApiResponse;
import com.mips.domain.comm.enums.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping( "/api/charge/")
public class ChargeController {

    private final ChargeService chargeService;

    @PostMapping("/create")
    public ResponseEntity<?> createCharge(@RequestBody ChargeRequest chargeRequest) {
        log.info("프론트단 결제 수신: {}", chargeRequest.toString());
        ChargeResponse chargeResponse = chargeService.createCharge(chargeRequest);
        return ResponseEntity.ok(
                ApiResponse.success(ResponseMessage.CHARGE_INIT_SUCCESS.getMessage(), chargeResponse)
        );
    }
}
