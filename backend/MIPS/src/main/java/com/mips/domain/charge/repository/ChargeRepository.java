package com.mips.domain.charge.repository;

import com.mips.domain.charge.entity.Charge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChargeRepository extends JpaRepository<Charge, Long> {


}
