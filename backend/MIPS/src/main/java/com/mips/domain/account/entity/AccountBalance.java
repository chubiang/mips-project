package com.mips.domain.account.entity;

import com.mips.domain.comm.entity.BaseTimeEntity;
import com.mips.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "account_balance", schema = "finance")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountBalance extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) // ★ 지연 로딩 필수
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "available_cash", nullable = false, precision = 19, scale = 4)
    private BigDecimal availableCash = BigDecimal.ZERO;

    @Column(name = "locked_cash", nullable = false, precision = 19, scale = 4)
    private BigDecimal lockedCash = BigDecimal.ZERO;

    @Version // JPA 결제 버그 방어
    @Column(nullable = false)
    private Long version;

    // ★ 객체지향적 비즈니스 로직: "주문할 때 내 가용 현금을 묶는다" (Setter 대신 사용)
    public void lockCashForOrder(BigDecimal orderAmount) {
        if (this.availableCash.compareTo(orderAmount) < 0) {
            throw new IllegalArgumentException("가용 잔고가 부족합니다.");
        }
        this.availableCash = this.availableCash.subtract(orderAmount);
        this.lockedCash = this.lockedCash.add(orderAmount);
    }
    // 충전용
    public void availableCashCharge(BigDecimal charge) {
        if (charge == null || charge.signum() <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }

        this.availableCash = this.availableCash.add(charge);
    }

}