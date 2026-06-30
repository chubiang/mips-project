package com.mips.domain.charge.entity;

import com.mips.domain.payment.entity.Payment;
import com.mips.domain.payment.enums.PaymentStatus;
import com.mips.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "charge", schema = "finance")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Charge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "charge_id", length = 64, unique = true, nullable = false)
    private String chargeId;

    // 고유 주문번호
    @OneToOne(mappedBy = "charge", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;

    // 포트원 서버가 발급하는 거래 고유 식별자 (결제 성공/실패 후 업데이트 됨)
    @Column(name = "transaction_id", length = 128, unique = true)
    private String transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email", nullable = false)
    private User user;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private PaymentStatus status;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // 💡 [비즈니스 로직] 1. 충전 사전 요청 (PENDING 상태로 최초 생성 시 사용)
    public static Charge createPending(User user, String chargeId, Integer amount, String currency) {
        Charge charge = new Charge();
        charge.user = user;
        charge.chargeId = chargeId;
        charge.amount = amount;
        charge.currency = currency;
        charge.status = PaymentStatus.PENDING;
        // payment 필드는 null 임.
        return charge;
    }

    // 💡 [비즈니스 로직] 2. 결제 완료 처리 (웹훅/검증 성공 시 상태 변경)
    public void markAsPaid(Payment payment, String transactionId) {
        if (this.status == PaymentStatus.PAID) {
            return; // 멱등성 보장
        }
        this.payment = payment;
        this.status = PaymentStatus.PAID;
        this.transactionId = transactionId;
        this.paidAt = LocalDateTime.now();
    }

    // 💡 [비즈니스 로직] 3. 결제 실패 처리
    public void markAsFailed(Payment payment, String transactionId) {
        this.payment = payment;
        this.status = PaymentStatus.FAILED;
        this.transactionId = transactionId;
    }
}
