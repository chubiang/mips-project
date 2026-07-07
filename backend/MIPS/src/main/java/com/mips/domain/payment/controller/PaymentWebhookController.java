package com.mips.domain.payment.controller;

import com.mips.domain.comm.dto.PaymentWebhook;
import com.mips.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentWebhookController {

    private final PaymentService paymentService;

    @PostMapping("/webhook")
    public ResponseEntity<String> handlePortOneWebhook(@RequestBody PaymentWebhook request) {
        log.info("포트원 웹훅 수신: {}", request);

        try {
            // 1. 상태가 "paid"(결제 완료)인 경우만 처리
            if (request.status().equals("PAID")) {
                // 2. 비즈니스 로직(결제 검증 및 카프카 이벤트 발행) 호출
//                paymentService.processPaymentComplete(request.getImp_uid(), request.getMerchant_uid());
            }

            // 3. 포트원 서버에 정상 수신되었음을 알림 (매우 중요!)
            return ResponseEntity.ok("success");

        } catch (Exception e) {
            log.error("웹훅 처리 중 에러 발생: {}", e.getMessage());
            // 에러가 발생해도 포트원에게는 200을 줘서 재시도를 막고,
            // 실패한 데이터는 내부적으로 DLQ(Dead Letter Queue)나 DB에 남겨 수동 처리하는 것이 안전합니다.
            return ResponseEntity.ok("handled_with_error");
        }
    }
}