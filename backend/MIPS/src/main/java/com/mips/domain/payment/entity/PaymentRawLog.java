package com.mips.domain.payment.entity;

import com.mips.domain.payment.enums.PaymentLogType;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "payment_raw_log")
public class PaymentRawLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentId;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private PaymentLogType eventType;
    // COMPLETE, WEBHOOK, CANCEL 등

    @Column(columnDefinition = "TEXT", nullable = false)
    private String rawResponse;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    public static PaymentRawLog setCompletePaymentRawLog(String rawResponse)
    {
        PaymentRawLog prl = new PaymentRawLog();
        prl.rawResponse = rawResponse;
        prl.eventType = PaymentLogType.COMPLETE;
        return prl;
    }

    public static PaymentRawLog setCancelPaymentRawLog(String rawResponse)
    {
        PaymentRawLog prl = new PaymentRawLog();
        prl.rawResponse = rawResponse;
        prl.eventType = PaymentLogType.CANCEL;
        return prl;
    }

    public static PaymentRawLog setWebhookPaymentRawLog(String rawResponse)
    {
        PaymentRawLog prl = new PaymentRawLog();
        prl.rawResponse = rawResponse;
        prl.eventType = PaymentLogType.WEBHOOK;
        return prl;
    }
}