package com.mips.domain.payment.repository;

import com.mips.domain.payment.entity.PaymentRawLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRawLogRepository extends JpaRepository<PaymentRawLog, Long>  {
}
