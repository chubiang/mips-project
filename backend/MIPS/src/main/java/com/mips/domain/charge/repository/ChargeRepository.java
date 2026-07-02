package com.mips.domain.charge.repository;

import com.mips.domain.charge.entity.Charge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChargeRepository extends JpaRepository<Charge, Long> {

    Optional<Charge> findByChargeId(String chargeId);

}
