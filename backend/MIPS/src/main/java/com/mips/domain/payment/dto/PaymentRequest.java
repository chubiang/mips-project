package com.mips.domain.payment.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaymentRequest {
    private String paymentId;
    private String storeId;
    private String email;
}
