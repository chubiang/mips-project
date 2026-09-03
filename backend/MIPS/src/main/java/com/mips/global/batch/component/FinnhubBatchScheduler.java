package com.mips.global.batch.component;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Component
public class FinnhubBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job finnhubQuoteJob;

    public FinnhubBatchScheduler(
            JobLauncher jobLauncher,
            @Qualifier("finnhubQuoteJob") Job finnhubQuoteJob) {
        this.jobLauncher = jobLauncher;
        this.finnhubQuoteJob = finnhubQuoteJob;
    }

    @Scheduled(
            cron = "${batch.finnhub.cron}",
            zone = "Asia/Seoul"
    )
    public void runFinnhubQuote() throws Exception {
        String runDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                                            .truncatedTo(ChronoUnit.MINUTES)
                                            .format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));

        JobParameters parameters =
                new JobParametersBuilder()
                        .addString("runDateTime", runDateTime)
                        .toJobParameters();

        jobLauncher.run(finnhubQuoteJob, parameters);
    }


}
