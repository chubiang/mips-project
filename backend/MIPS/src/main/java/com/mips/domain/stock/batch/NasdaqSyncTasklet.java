package com.mips.domain.stock.batch;

import com.mips.domain.stock.service.NasdaqSyncService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NasdaqSyncTasklet implements Tasklet {

    private final NasdaqSyncService nasdaqSyncService;


    @Nullable
    @Override
    public RepeatStatus execute(StepContribution contribution,
                                ChunkContext chunkContext) throws Exception {
        nasdaqSyncService.syncNasdaq100();
        return RepeatStatus.FINISHED;

    }
}
