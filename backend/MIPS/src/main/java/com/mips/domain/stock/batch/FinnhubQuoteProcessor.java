package com.mips.domain.stock.batch;

import com.mips.domain.stock.entity.SecurityMaster;
import com.mips.domain.stock.entity.SecurityQuote;
import com.mips.domain.stock.service.NasdaqSyncService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FinnhubQuoteProcessor implements ItemProcessor<SecurityMaster, SecurityQuote> {

    private final NasdaqSyncService nasdaqSyncService;

    @Nullable
    @Override
    public SecurityQuote process(@NotNull SecurityMaster security) throws Exception
    {
        return nasdaqSyncService.getStockPrice(security);
    }
}
