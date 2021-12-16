package com.github.jonathanlalou.kafkabasic.batch;

import com.github.jonathanlalou.kafkabasic.domain.Book;
import com.github.jonathanlalou.kafkabasic.domain.BookDTO;
import com.github.jonathanlalou.kafkabasic.domain.Chapter;
import com.github.jonathanlalou.kafkabasic.domain.Letter;
import com.github.jonathanlalou.kafkabasic.domain.Verse;
import com.github.jonathanlalou.kafkabasic.repository.ElsRepository;
import com.github.jonathanlalou.kafkabasic.repository.LetterRepository;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Component(GhardaiaCleaningTasklet.GHARDAIA_CLEANING_TASKLET)
@Slf4j
@Getter
@Setter
@StepScope
public class GhardaiaCleaningTasklet implements Tasklet, StepExecutionListener {
    public static final String GHARDAIA_CLEANING_TASKLET = "ghardaiaCleaningTasklet";

    @Value("${ghardaia.cleaning}")
    private Boolean cleaning;

    @Autowired
    private LetterRepository letterRepository;
    @Autowired
    private ElsRepository elsRepository;

    protected static final String INPUT_FOLDER = "./src/main/resources/text/";

    @PostConstruct
    public void postConstruct() {
        Assert.notNull(letterRepository, "letterRepository cannot be null");
        Assert.notNull(elsRepository, "elsRepository cannot be null");
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        if (cleaning) {
            log.info("Cleaning all...");
            letterRepository.deleteAll();
            elsRepository.deleteAll();
            if (letterRepository.count() == 0L) {
                log.info("Letters were purged ✅");
            } else {
                log.warn("Letters were NOT purged ❌");
            }
            if (elsRepository.count() == 0L) {
                log.info("ELSes were purged ✅");
            } else {
                log.warn("ELSes were NOT purged ❌");
            }
            // TODO purge Kafka topics
        } else {
            log.info("Won't clean...");
        }

        return RepeatStatus.FINISHED;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        // nothing to do on the first step of the job!
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }
}
