package com.mips.domain.account.entity;

import com.mips.domain.account.enums.AccountStatus;
import com.mips.domain.account.enums.AccountType;
import com.mips.domain.comm.entity.BaseTimeEntity;
import com.mips.domain.comm.enums.Currency;
import com.mips.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "account", schema = "finance")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Account extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Column(nullable = false, unique = true, length = 30)
    private String accountNumber;

    @NotNull
    @Column(length = 100)
    private String accountName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountStatus status;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false, length = 3)
    private Currency baseCurrency = Currency.KRW;

    private LocalDateTime openedAt;
    private LocalDateTime frozenAt;
    private LocalDateTime closedAt;

}