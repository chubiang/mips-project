package com.mips.domain.order.entity;

import com.mips.domain.comm.entity.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.mips.domain.comm.enums.Currency;
import com.mips.domain.order.enums.*;

/** 상태 변경 이력. 기존 행을 수정하지 않고 이벤트마다 새 행을 추가한다. */
@Entity
@Table(name = "order_history", schema = "finance",
       indexes = @Index(name = "idx_order_history_order", columnList = "order_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class OrderHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, updatable = false)
    @NotNull
    private TradeOrder order;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 20, updatable = false)
    private OrderStatus previousStatus;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 20, updatable = false)
    private OrderStatus newStatus;

    // 부분 체결이 반복되면 이전/다음 상태가 같아도 별도 이력을 기록한다.
    @Size(max = 50)
    @Column(name = "reason_code", length = 50, updatable = false)
    private String reasonCode;

    @Size(max = 500)
    @Column(length = 500, updatable = false)
    private String description;

    @NotNull
    @Column(name = "occurred_at", nullable = false, updatable = false)
    private LocalDateTime occurredAt;
}
