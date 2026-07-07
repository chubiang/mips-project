package com.mips.domain.payment.service;

import com.mips.domain.payment.entity.PaymentRawLog;
import com.mips.domain.payment.repository.PaymentRawLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PaymentRawLogService {

    private final PaymentRawLogRepository paymentRawLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveRawLog(String rawJson) {

        PaymentRawLog paymentRawLog = PaymentRawLog.setCompletePaymentRawLog(rawJson);

        paymentRawLogRepository.save(paymentRawLog);
    }
}
