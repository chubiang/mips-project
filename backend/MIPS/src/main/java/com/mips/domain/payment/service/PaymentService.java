package com.mips.domain.payment.service;

import com.mips.domain.payment.dto.PaymentRequest;
import com.mips.domain.payment.dto.PaymentResponse;
import com.mips.global.config.PortOneSecretProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

@Service
public class PaymentService {

    private final RestClient portoneRestClient;
    private String webhookVerifier;

    public PaymentService(@Autowired @Qualifier("portoneRestClient") RestClient portoneRestClient,
                          PortOneSecretProperties properties) {
        this.portoneRestClient = portoneRestClient;
        this.webhookVerifier = properties.getWebhook();
    }


    @Transactional
    public PaymentResponse processPaymentComplete(PaymentRequest request) {
        return portoneRestClient.get()
                .uri("/payments/{paymentId}?storeId={storeId}", request.getPaymentId(), request.getStoreId())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(PaymentResponse.class);
    }
}
