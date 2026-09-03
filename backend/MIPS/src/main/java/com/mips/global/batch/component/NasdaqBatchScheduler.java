package com.mips.global.batch.component;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class NasdaqBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job nasdaqSyncJob;

    public NasdaqBatchScheduler(
            JobLauncher jobLauncher,
            @Qualifier("nasdaqSyncJob") Job nasdaqSyncJob
    ) {
        this.jobLauncher = jobLauncher;
        this.nasdaqSyncJob = nasdaqSyncJob;
    }

    @Scheduled(
            cron = "${batch.nasdaq.cron}",
            zone = "Asia/Seoul"
    )
    public void runNasdaqSync() throws Exception {

        JobParameters parameters =
                new JobParametersBuilder()
                        .addString(
                                "syncDate",
                                LocalDate.now(
                                        ZoneId.of("Asia/Seoul")
                                ).toString()
                        )
                        .toJobParameters();

        jobLauncher.run(
                nasdaqSyncJob,
                parameters
        );
    }
}