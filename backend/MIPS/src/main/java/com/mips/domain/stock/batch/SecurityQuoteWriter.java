package com.mips.domain.stock.batch;

import com.mips.domain.stock.entity.SecurityMaster;
import com.mips.domain.stock.entity.SecurityPriceHistory;
import com.mips.domain.stock.entity.SecurityQuote;
import com.mips.domain.stock.repository.SecurityPriceHistoryRepository;
import com.mips.domain.stock.repository.SecurityQuoteRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityQuoteWriter implements ItemWriter<SecurityQuote> {

    private final EntityManager entityManager;
    private final SecurityQuoteRepository securityQuoteRepository;
    private final SecurityPriceHistoryRepository securityPriceHistoryRepository;

    @Override
    public void write(@NotNull Chunk<? extends SecurityQuote> chunk) {
        for (SecurityQuote newQuote : chunk) {
            Long securityId = newQuote.getSecurity().getId();

            Optional<SecurityQuote> existing =
                    securityQuoteRepository.findById(securityId);

            if (existing.isPresent()) {
                SecurityQuote quote = existing.get();
                quote.update(
                        newQuote.getCurrentPrice(),
                        newQuote.getChangePrice(),
                        newQuote.getPercentageChange(),
                        newQuote.getHighPrice(),
                        newQuote.getLowPrice(),
                        newQuote.getOpenPrice(),
                        newQuote.getPrevClose(),
                        newQuote.getVolume(),
                        newQuote.getQuotedAt()
                );
            }
            else {
                // 영속성 컨텍스트 엇박자 방지
                SecurityMaster managedSecurity = entityManager.getReference(
                        SecurityMaster.class,
                        securityId
                );

                newQuote.changeSecurity(managedSecurity);

                securityQuoteRepository.save(newQuote);
            }

            // 시가이력 테이블
            SecurityPriceHistory history = new SecurityPriceHistory(newQuote.getSecurity(),
                                                                    newQuote.getCurrentPrice(),
                                                                    newQuote.getChangePrice(),
                                                                    newQuote.getPercentageChange(),
                                                                    newQuote.getVolume(),
                                                                    newQuote.getQuotedAt());
            securityPriceHistoryRepository.save(history);
        }
    }
}
