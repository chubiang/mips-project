package com.mips.domain.stock.batch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mips.domain.stock.service.RealtimeQuotePersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RealtimeQuoteSaveTasklet implements Tasklet {

    private final RealtimeQuotePersistenceService persistenceService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        persistenceService.saveLatestQuotes();
        return RepeatStatus.FINISHED;

    }
}
