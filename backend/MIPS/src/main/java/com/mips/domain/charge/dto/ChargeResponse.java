package com.mips.domain.charge.dto;

import com.mips.domain.charge.entity.Charge;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChargeResponse {

    private String chargeId; // charge PK

    private String transactionId; // 포트원 ID

    private String storeId; // 가맹점 ID

    private String paymentId; // payment PK

    private String email; // 이메일

    private Integer amount; // 금액

    private String currency; // 화폐 단위

    private String status; // 충전 진행상태

    public ChargeResponse(Charge charge) {
        this.chargeId = charge.getChargeId();
        this.transactionId = charge.getTransactionId();
        this.paymentId = charge.getPayment().getPaymentId();
        this.email = charge.getUser().getEmail();
        this.amount = charge.getAmount();
        this.currency = charge.getCurrency();
        this.status = charge.getStatus().toString();
    }


}
