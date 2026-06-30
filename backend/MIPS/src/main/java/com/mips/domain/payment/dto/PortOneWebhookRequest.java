package com.mips.domain.payment.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PortOneWebhookRequest {
    private String imp_uid;      // 포트원 결제 고유 번호
    private String merchant_uid; // 우리 서버에서 생성한 주문 번호
    private String status;       // 결제 상태 (paid, failed, ready 등)
}