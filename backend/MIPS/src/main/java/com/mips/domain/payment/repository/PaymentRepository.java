package com.mips.domain.payment.repository;

import com.mips.domain.charge.entity.Charge;
import com.mips.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
