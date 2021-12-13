package com.github.jonathanlalou.kafkabasic.batch;

import com.github.jonathanlalou.kafkabasic.domain.Book;
import com.github.jonathanlalou.kafkabasic.domain.Letter;
import com.github.jonathanlalou.kafkabasic.service.KafkaBasicEmitter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component(GrossDataSendToKafkaTasklet.GROSS_DATA_SEND_TO_KAFKA_TASKLET)
@Slf4j
@Getter
@Setter
@StepScope
public class GrossDataSendToKafkaTasklet implements Tasklet, StepExecutionListener {
    public static final String GROSS_DATA_SEND_TO_KAFKA_TASKLET = "GrossDataSendToKafkaTasklet";

    @Autowired
    private KafkaBasicEmitter kafkaBasicEmitter;

    private List<Book> books = new ArrayList<>();
    private List<Letter> letters = new ArrayList<>();


    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        kafkaBasicEmitter.sendMessagesToKafka(this.letters);
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void beforeStep(StepExecution stepExecution) {
        final ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();
        this.books = (List<Book>) executionContext.get("books");
        this.letters = (List<Letter>) executionContext.get("letters");
        log.debug("Step is starting and data was retrieved from ExecutionContext");

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        final ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();
        executionContext.put("books", this.books);
        executionContext.put("letters", this.letters);
        log.debug("Step is completed and data was put into ExecutionContext");

        return ExitStatus.COMPLETED;
    }
}
