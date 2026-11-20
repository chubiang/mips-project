package com.mips.domain.order.entity;

import com.mips.domain.comm.entity.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.mips.domain.comm.enums.Currency;
import com.mips.domain.order.enums.*;

/** 실제 체결 한 건. 부분 체결마다 새 레코드를 생성한다. */
@Entity
@Table(name = "trade_execution", schema = "finance",
       indexes = @Index(name = "idx_trade_execution_order", columnList = "order_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class TradeExecution extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, updatable = false)
    @NotNull
    private TradeOrder order;

    // 체결 이벤트 재수신 시 동일 키 사용. 서비스에서 중복 결과를 반환한다.
    @NotBlank @Size(max = 100)
    @Column(name = "execution_key", nullable = false, unique = true, updatable = false, length = 100)
    private String executionKey;

    @NotNull @Positive
    @Column(nullable = false, updatable = false)
    private Long quantity;

    @NotNull @Positive @Digits(integer = 15, fraction = 4)
    @Column(nullable = false, updatable = false, precision = 19, scale = 4)
    private BigDecimal price;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, length = 3)
    private Currency currency;

    @NotNull @PositiveOrZero @Digits(integer = 15, fraction = 4)
    @Builder.Default
    @Column(nullable = false, updatable = false, precision = 19, scale = 4)
    private BigDecimal fee = BigDecimal.ZERO;

    @NotNull
    @Column(name = "executed_at", nullable = false, updatable = false)
    private LocalDateTime executedAt;
}
