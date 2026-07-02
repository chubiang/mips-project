package com.mips.domain.payment.service;

import com.mips.domain.charge.entity.Charge;
import com.mips.domain.charge.repository.ChargeRepository;
import com.mips.domain.payment.dto.PaymentRequest;
import com.mips.domain.payment.dto.PortOneResponse;
import com.mips.domain.payment.entity.PaymentRawLog;
import com.mips.domain.payment.enums.PaymentStatus;
import com.mips.domain.payment.repository.PaymentRawLogRepository;
import com.mips.global.config.PortOneSecretProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class PaymentService {

    private final RestClient portoneRestClient;
    private String webhookVerifier;
    private final PaymentRawLogRepository paymentRawLogRepository;
    private final ChargeRepository chargeRepository;

    public PaymentService(@Autowired @Qualifier("portoneRestClient") RestClient portoneRestClient,
                          PortOneSecretProperties properties,
                          PaymentRawLogRepository paymentRawLogRepository,
                          ChargeRepository chargeRepository) {
        this.portoneRestClient = portoneRestClient;
        this.webhookVerifier = properties.getWebhook();
        this.paymentRawLogRepository = paymentRawLogRepository;
        this.chargeRepository = chargeRepository;
    }


    @Transactional
    public PortOneResponse processPaymentComplete(PaymentRequest request) {

        // 1. RAW DATA 저장
        String rawJson = portoneRestClient.get()
                .uri("/payments/{paymentId}?storeId={storeId}", request.getPaymentId(), request.getStoreId())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);
        PaymentRawLog prl = PaymentRawLog.setCompletePaymentRawLog(rawJson);
        paymentRawLogRepository.save(prl);


        // 2. Payment에 update
        PortOneResponse resPayment = portoneRestClient.get()
                            .uri("/payments/{paymentId}?storeId={storeId}", request.getPaymentId(), request.getStoreId())
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .body(PortOneResponse.class);

         // 3. Charge에 update
        Optional<Charge> charge = chargeRepository.findByChargeId(request.getChargeId());

        charge.ifPresent(c -> {
            if (resPayment != null) {
                // status가 지불일때
                if (resPayment.getStatus().equals(PaymentStatus.PAID.name())) {
                    c.setPaidAt(LocalDateTime.ofInstant(resPayment.getPaidPayment().getPaidAt(), ZoneId.of("Asia/Seoul")));
                }
                else if (resPayment.getStatus().equals(PaymentStatus.CANCELLED.name())) {
                    c.setCanceledAt(LocalDateTime.ofInstant(resPayment.getCancelledPayment().getCancelledAt(), ZoneId.of("Asia/Seoul")));
                }
                c.setTransactionId(resPayment.getTransactionId());
                c.setStatus(PaymentStatus.valueOf(resPayment.getStatus()));
            }
            chargeRepository.save(c);
        });


        return null;
    }
}
