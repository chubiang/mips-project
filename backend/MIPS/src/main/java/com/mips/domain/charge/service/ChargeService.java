package com.mips.domain.charge.service;

import com.mips.domain.charge.dto.ChargeRequest;
import com.mips.domain.charge.dto.ChargeResponse;
import com.mips.domain.charge.entity.Charge;
import com.mips.domain.charge.repository.ChargeRepository;
import com.mips.domain.comm.enums.ResponseMessage;
import com.mips.domain.user.entity.User;
import com.mips.domain.user.repository.UserRepository;
import com.mips.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChargeService {

    private final UserRepository userRepository;
    private final ChargeRepository chargeRepository;

    public ChargeResponse createCharge(ChargeRequest chargeRequest) {

        User user = userRepository.findByEmail(chargeRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException(ResponseMessage.INVALID_USER.getMessage()));

        Charge charge = Charge.createPending(user,
                chargeRequest.getPaymentId(),
                chargeRequest.getAmount(),
                chargeRequest.getCurrency());
        Charge savedCharge = chargeRepository.save(charge);
        return new ChargeResponse(savedCharge);
    }
}
