package com.mips.global.batch.config;

import com.mips.domain.stock.batch.FinnhubQuoteProcessor;
import com.mips.domain.stock.batch.NasdaqSyncTasklet;
import com.mips.domain.stock.batch.RealtimeQuoteSaveTasklet;
import com.mips.domain.stock.batch.SecurityQuoteWriter;
import com.mips.domain.stock.entity.SecurityMaster;
import com.mips.domain.stock.entity.SecurityQuote;
import com.mips.domain.stock.enums.Exchange;
import com.mips.domain.stock.repository.SecurityMasterRepository;
import com.mips.global.exception.FinnhubRateLimitException;
import com.mips.global.exception.FinnhubTemporaryException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
public class MipsBatchConfig {

    @Bean
    public Step nasdaqSyncStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            NasdaqSyncTasklet nasdaqSyncTasklet
    ) {

        return new StepBuilder(
                "nasdaqSyncStep",
                jobRepository
        )
                .tasklet(
                        nasdaqSyncTasklet,
                        transactionManager
                )
                .build();
    }
    @Bean
    public Job nasdaqSyncJob(
            JobRepository jobRepository,
            @Qualifier("nasdaqSyncStep") Step nasdaqSyncStep
    ) {

        return new JobBuilder(
                "nasdaqSyncJob",
                jobRepository
        )
                .start(nasdaqSyncStep)
                .build();
    }

    /***********************************************/
    /* Finnhub 사의 API로 주식 Quote 조회 */
    /***********************************************/
    @Bean
    @StepScope
    public ListItemReader<SecurityMaster> securityMasterReader(
            SecurityMasterRepository securityMasterRepository
    ) {

        List<SecurityMaster> securities =
                securityMasterRepository.findAllByExchangeAndIsActive(
                        Exchange.NASDAQ,
                        true
                );

        return new ListItemReader<>(securities);
    }

    @Bean
    public Step finnhubQuoteStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        ListItemReader<SecurityMaster> securityMasterReader,
        FinnhubQuoteProcessor processor,
        SecurityQuoteWriter writer,
        ExponentialBackOffPolicy finnhubBackOffPolicy) {
        return new StepBuilder("finnhubQuoteStep", jobRepository)
                .<SecurityMaster, SecurityQuote>chunk(10, transactionManager)
                .faultTolerant()
                .retry(FinnhubRateLimitException.class)
                .retry(FinnhubTemporaryException.class)
                .retryLimit(3)
                .backOffPolicy(finnhubBackOffPolicy)
                .reader(securityMasterReader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job finnhubQuoteJob(
            JobRepository repository,
            @Qualifier("finnhubQuoteStep") Step step
    ) {
        return new JobBuilder("finnhubQuoteJob", repository)
                .start(step)
                .build();
    }

    /***********************************************/
    /* 장마감 시에 마지막 값 저장 배치 */
    /***********************************************/
    @Bean
    public Step finnhubClosedStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            RealtimeQuoteSaveTasklet tasklet
    ) {
        return new StepBuilder(
                "finnhubClosedStep",
                jobRepository
        )
                .tasklet(tasklet, transactionManager)
                .build();
    }

    @Bean
    public Job finnhubClosedQuoteJob(
            JobRepository jobRepository,
            @Qualifier("finnhubClosedStep") Step step
    ) {
        return new JobBuilder(
                "finnhubClosedQuoteJob",
                jobRepository
        )
                .start(step)
                .build();
    }

}