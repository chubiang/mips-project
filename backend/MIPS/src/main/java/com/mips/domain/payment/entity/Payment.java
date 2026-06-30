package com.mips.domain.payment.entity;

import com.mips.domain.charge.entity.Charge;
import com.mips.domain.payment.enums.PaymentStatus;
import com.mips.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "payment", schema = "finance")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email", nullable = false)
    private User user;

    @Column(name = "payment_id", length = 64, unique = true, nullable = false)
    private String paymentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charge_db_id", nullable = false)
    private Charge charge;

    @Column(name = "transaction_id", length = 128, unique = true)
    private String transactionId;

    @Column(name = "store_id", length = 200, nullable = false)
    private String storeId;

    @Column(name = "bill_no", length = 200, unique = true, nullable = false)
    private String billNo;        // 결제 건 영수증번호

    @Column(name = "pay_method")
    private String payMethod;

    @Column(name = "channel")
    private String channel;

    @Column(name = "channel_group")
    private String channelGroup;

    @Column(name = "version")
    private String version;

    @Column(name = "billing_key")
    private String billingKey;

    @Column(name = "amount", nullable = false)
    private Integer amount;      // 결제 금액 정보 객체 - 테스트니까 작게 잡음

    @Column(name = "currency", nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private PaymentStatus status;      // READY, PAID, FAILED 등 결제 상태

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "status_changed_at")
    private LocalDateTime statusChangedAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 주문 생성 시간

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt; // 결제 상태 최종 변경 시간

    @Builder
    public Payment(User user, String billNo, Integer amount) {
        this.user = user;
        this.billNo = billNo;
        this.amount = amount;
        this.status = PaymentStatus.READY; // 처음 엔티티가 만들어질 땐 무조건 '대기' 상태
    }

    // 비즈니스 로직 헬퍼 메서드 - 성공
    public void changeStatusToPaid(String paymentId) {
        this.paymentId = paymentId;
        this.status = PaymentStatus.PAID;
    }
    // 비즈니스 로직 헬퍼 메서드 - 실패
    public void changeStatusToFailed() {
        this.status = PaymentStatus.FAILED;
    }

}
