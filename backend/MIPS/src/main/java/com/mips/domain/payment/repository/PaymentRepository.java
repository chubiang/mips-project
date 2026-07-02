package com.mips.domain.payment.repository;

import com.mips.domain.charge.entity.Charge;
import com.mips.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentId(String paymentId);

}
