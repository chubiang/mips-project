package com.mips.domain.payment.service;

import com.mips.domain.charge.entity.Charge;
import com.mips.domain.charge.repository.ChargeRepository;
import com.mips.domain.payment.dto.PaymentRequest;
import com.mips.domain.payment.dto.PortOneResponse;
import com.mips.domain.payment.entity.Payment;
import com.mips.domain.payment.entity.PaymentRawLog;
import com.mips.domain.payment.enums.PaymentStatus;
import com.mips.domain.payment.enums.SelectedChannelType;
import com.mips.domain.payment.repository.PaymentRawLogRepository;
import com.mips.domain.payment.repository.PaymentRepository;
import com.mips.global.config.PortOneSecretProperties;
import io.portone.sdk.server.errors.PaymentNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Slf4j
@Service
public class PaymentService {

    private final RestClient portoneRestClient;
    private String webhookVerifier;
    private final PaymentRawLogRepository paymentRawLogRepository;
    private final ChargeRepository chargeRepository;
    private final PaymentRepository paymentRepository;

    public PaymentService(@Autowired @Qualifier("portoneRestClient") RestClient portoneRestClient,
                          PortOneSecretProperties properties,
                          PaymentRawLogRepository paymentRawLogRepository,
                          ChargeRepository chargeRepository,
                          PaymentRepository paymentRepository
    ) {
        this.portoneRestClient = portoneRestClient;
        this.webhookVerifier = properties.getWebhook();
        this.paymentRawLogRepository = paymentRawLogRepository;
        this.chargeRepository = chargeRepository;
        this.paymentRepository = paymentRepository;
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


        // 2. 포트원에 결제층전 요청내역 처리(단건) 조회
        PortOneResponse resPayment = portoneRestClient.get()
                            .uri("/payments/{paymentId}?storeId={storeId}", request.getPaymentId(), request.getStoreId())
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .body(PortOneResponse.class);

        if (resPayment == null) {
            log.error("Could not retrieve PortOneResponse from PortOne");
            throw new NullPointerException();
        }
         // 3. Charge에 update
        Optional<Charge> charge = chargeRepository.findByChargeId(request.getChargeId());

        charge.ifPresent(c -> {
            c.setTransactionId(resPayment.getTransactionId());
            c.setStatus(PaymentStatus.valueOf(resPayment.getStatus()));

            // status가 지불일 때
            if (resPayment.getStatus().equals(PaymentStatus.PAID.name())) {
                c.setPaidAt(LocalDateTime.ofInstant(resPayment.getPaidPayment().getPaidAt(), ZoneId.of("Asia/Seoul")));
            }
            // status가 취소일 때
            else if (resPayment.getStatus().equals(PaymentStatus.CANCELLED.name())) {
                c.setCanceledAt(LocalDateTime.ofInstant(resPayment.getCancelledPayment().getCancelledAt(), ZoneId.of("Asia/Seoul")));
            }

            chargeRepository.save(c);
        });

        // 4. Payment에 update
        Optional<Payment> payment = paymentRepository.findByPaymentId(request.getPaymentId());
        payment.ifPresent( p -> {
            p.setTransactionId(resPayment.getTransactionId());
            p.setStatus(PaymentStatus.valueOf(resPayment.getStatus()));
            p.setBillingKey(resPayment.getBillingKey());
            p.setVersion(resPayment.getVersion().getValue());
            p.setChannel(SelectedChannelType.valueOf(resPayment.getChannel().getType().getValue()));
            p.setChannelGroup(resPayment.getChannelGroup().getId());
            paymentRepository.save(p);
        });

        return resPayment;
    }
}
