package com.github.jonathanlalou.kafkabasic.batch;

import com.github.jonathanlalou.kafkabasic.domain.Book;
import com.github.jonathanlalou.kafkabasic.domain.Letter;
import com.github.jonathanlalou.kafkabasic.repository.LetterRepository;
import com.github.jonathanlalou.kafkabasic.service.LetterKafkaProducer;
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

// DELETEME
@Component(LetterSendToKafkaTasklet.LETTER_SEND_TO_KAFKA_TASKLET)
@Slf4j
@Getter
@Setter
@StepScope
public class LetterSendToKafkaTasklet implements Tasklet, StepExecutionListener {
    public static final String LETTER_SEND_TO_KAFKA_TASKLET = "letterSendToKafkaTasklet";

    @Autowired
    private LetterKafkaProducer letterKafkaProducer;
    @Autowired
    private LetterRepository letterRepository;

    private List<Book> books = new ArrayList<>();
    private List<Letter> letters = new ArrayList<>();

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        log.warn("This step WON'T do anything");
/*
        log.info("About to send {} Letters to Kafka", this.letters.size());
//        letterRepository.saveAll(this.letters);
//        letterKafkaProducer.sendLettersToKafka(this.letters);
        log.info("There are currently {} Letters in DB", letterRepository.count());
*/
        return RepeatStatus.FINISHED;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void beforeStep(StepExecution stepExecution) {
        final ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();
        this.books = (List<Book>) executionContext.get(JsonFileLoadTasklet.BOOKS);
        this.letters = (List<Letter>) executionContext.get(JsonFileLoadTasklet.LETTERS);
        log.debug("Step is starting and data was retrieved from ExecutionContext");

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        final ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();
        executionContext.put(JsonFileLoadTasklet.BOOKS, this.books);
        executionContext.put(JsonFileLoadTasklet.LETTERS, this.letters);
        log.debug("Step is completed and data was put into ExecutionContext");

        return ExitStatus.COMPLETED;
    }
}
