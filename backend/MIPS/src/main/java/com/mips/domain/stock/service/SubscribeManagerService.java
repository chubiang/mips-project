package com.mips.domain.stock.service;

import com.mips.domain.stock.entity.SecurityMaster;
import com.mips.domain.stock.enums.Exchange;
import com.mips.domain.stock.repository.SecurityMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SubscribeManagerService {

    private final SecurityMasterRepository securityMasterRepository;

    @Transactional(readOnly = true)
    public List<SecurityMaster> getTickerList(Exchange exchange) {
        return securityMasterRepository.findAllByExchangeAndIsActive(exchange, true);
    }
}
