package com.mips.domain.charge.dto;

import com.mips.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@AllArgsConstructor
public class ChargeRequest {
    private String chargeId;

    private String paymentId;

    private String storeId;

    private Integer amount;

    private String currency;

    private String email;
}
