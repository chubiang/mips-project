package com.mips.domain.payment.controller;

import com.mips.domain.payment.dto.PaymentRequest;
import com.mips.domain.payment.dto.PaymentResponse;
import com.mips.domain.payment.dto.PortOneWebhookRequest;
import com.mips.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping( "/api/pay/")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/complete")
    public ResponseEntity<PaymentResponse> reqPayComplete(@RequestBody PaymentRequest request) {
        log.info("프론트단 결제 수신: {}", request);
        PaymentResponse response = paymentService.processPaymentComplete(request);
        return ResponseEntity.ok(response);
    }


}