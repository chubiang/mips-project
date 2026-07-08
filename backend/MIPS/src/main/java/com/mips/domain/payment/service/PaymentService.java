package com.mips.domain.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mips.domain.account.entity.AccountBalance;
import com.mips.domain.account.repository.AccountBalanceRepository;
import com.mips.domain.charge.entity.Charge;
import com.mips.domain.charge.repository.ChargeRepository;
import com.mips.domain.comm.utils.DateTimeUtils;
import com.mips.domain.payment.dto.PaymentRequest;
import com.mips.domain.payment.dto.PortOneResponse;
import com.mips.domain.payment.entity.Payment;
import com.mips.domain.payment.entity.PaymentRawLog;
import com.mips.domain.payment.enums.PaymentStatus;
import com.mips.domain.payment.enums.SelectedChannelType;
import com.mips.domain.payment.repository.PaymentRawLogRepository;
import com.mips.domain.payment.repository.PaymentRepository;
import com.mips.global.config.PortOneSecretProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    private final PaymentRawLogService paymentRawLogService;
    private final ChargeRepository chargeRepository;
    private final PaymentRepository paymentRepository;
    private final AccountBalanceRepository accountBalanceRepository;
    private final ObjectMapper objectMapper;


    public PaymentService(@Autowired @Qualifier("portoneRestClient") RestClient portoneRestClient,
                          PortOneSecretProperties properties,
                          PaymentRawLogService paymentRawLogService,
                          ChargeRepository chargeRepository,
                          PaymentRepository paymentRepository,
                          AccountBalanceRepository accountBalanceRepository,
                          ObjectMapper objectMapper
    ) {
        this.portoneRestClient = portoneRestClient;
        this.webhookVerifier = properties.getWebhook();
        this.paymentRawLogService = paymentRawLogService;
        this.chargeRepository = chargeRepository;
        this.paymentRepository = paymentRepository;
        this.accountBalanceRepository = accountBalanceRepository;
        this.objectMapper = objectMapper;
    }





    @Transactional
    public PortOneResponse processPaymentComplete(PaymentRequest request) {
        // 0. 포트원 결제 단건 조회 요청
        String rawJson = portoneRestClient.get()
                .uri("/payments/{paymentId}?storeId={storeId}", request.getPaymentId(), request.getStoreId())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);

        // 1. RAW DATA 저장
        paymentRawLogService.saveRawLog(rawJson);

        // 2. 포트원에 결제층전 요청내역 처리(단건) 조회 JSON 파싱
        PortOneResponse resPayment = null;
        try {
            resPayment = objectMapper.readValue(rawJson, PortOneResponse.class);

            if (resPayment == null) {
                log.error("Could not retrieve PortOneResponse from PortOne");
                throw new NullPointerException();
            }
            PortOneResponse finalResPayment = resPayment;

             // 3. Charge에 update
            Charge c = chargeRepository.findByChargeId(request.getChargeId())
                    .orElseThrow(() -> new IllegalArgumentException("충전 정보를 찾을 수 없습니다."));

            c.setTransactionId(finalResPayment.transactionId());
            c.setStatus(PaymentStatus.valueOf(finalResPayment.status()));

            // status가 지불일 때
            boolean isPaid = finalResPayment.status().equals(PaymentStatus.PAID.name());
            if (isPaid) {
                c.setPaidAt(DateTimeUtils.toKstLocalDateTime(finalResPayment.paidAt()));
            }
            // status가 취소일 때
            else if (finalResPayment.status().equals(PaymentStatus.CANCELLED.name())) {
                c.setCanceledAt(DateTimeUtils.toKstLocalDateTime(finalResPayment.cancelledAt()));
            }

            chargeRepository.save(c);

            // 4. Payment에 update
            Payment p = paymentRepository.findByPaymentId(request.getPaymentId())
                        .orElseThrow(() -> new IllegalArgumentException("결제요청 정보를 찾을 수 없습니다."));

            p.setTransactionId(finalResPayment.transactionId());
            p.setStatus(PaymentStatus.valueOf(finalResPayment.status()));
            p.setBillingKey(finalResPayment.billingKey());
            p.setVersion(finalResPayment.version());
            p.setChannel(SelectedChannelType.valueOf(String.valueOf(finalResPayment.channel().type())));
            if (finalResPayment.channelGroup() != null) {
                p.setChannelGroup(finalResPayment.channelGroup().id());
            }
            paymentRepository.save(p);

            // 5. 결제 성공 시에, 고객 계좌에 UPDATE
            if (isPaid) {
                AccountBalance acc = accountBalanceRepository.findByUserId(c.getUser().getId())
                        .orElseThrow(() -> new IllegalArgumentException("잔고 정보를 찾을 수 없습니다."));

                acc.availableCashCharge(finalResPayment.amount().paid());
                accountBalanceRepository.save(acc);
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return resPayment;
    }
}
