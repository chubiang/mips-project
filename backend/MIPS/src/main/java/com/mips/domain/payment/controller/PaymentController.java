package com.mips.domain.payment.controller;

import com.mips.domain.payment.dto.PaymentRequest;
import com.mips.domain.payment.dto.PortOneResponse;
import com.mips.domain.payment.service.PaymentService;
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
@RequestMapping( "/api/pay/")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/complete")
    public ResponseEntity<PortOneResponse> completePayment(@RequestBody PaymentRequest request) {
        log.info("프론트단 결제 수신: {}", request);
        PortOneResponse response = paymentService.processPaymentComplete(request);
        return ResponseEntity.ok(response);
    }


}