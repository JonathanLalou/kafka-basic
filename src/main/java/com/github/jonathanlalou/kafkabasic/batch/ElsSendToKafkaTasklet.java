package com.github.jonathanlalou.kafkabasic.batch;

import com.github.jonathanlalou.kafkabasic.domain.Book;
import com.github.jonathanlalou.kafkabasic.domain.Els;
import com.github.jonathanlalou.kafkabasic.domain.Letter;
import com.github.jonathanlalou.kafkabasic.repository.ElsRepository;
import com.github.jonathanlalou.kafkabasic.service.ElsKafkaProducer;
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

import static com.github.jonathanlalou.kafkabasic.batch.ElsGeneratorTasklet.EQUIDISTANT_LETTER_SEQUENCES;
import static com.github.jonathanlalou.kafkabasic.batch.JsonFileLoadTasklet.BOOKS;
import static com.github.jonathanlalou.kafkabasic.batch.JsonFileLoadTasklet.LETTERS;

@Component(ElsSendToKafkaTasklet.ELS_SEND_TO_KAFKA_TASKLET)
@Slf4j
@Getter
@Setter
@StepScope
public class ElsSendToKafkaTasklet implements Tasklet, StepExecutionListener {
    public static final String ELS_SEND_TO_KAFKA_TASKLET = "ElsSendToKafkaTasklet";

    @Autowired
    private ElsKafkaProducer elsKafkaProducer;
    @Autowired
    private ElsRepository elsRepository;

    private List<Book> books = new ArrayList<>();
    private List<Letter> letters = new ArrayList<>();
    private List<Els> elses = new ArrayList<>();

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        log.warn("This step WON'T do anything");
/*
        log.info("About to send {} ELSes to Kafka", this.elses.size());
//        elsKakfaProducer.sendElsesToKafka(this.elses);
        elsRepository.saveAll(this.elses);
        log.info("There are currently {} ELSes in DB", elsRepository.count());
*/
        return RepeatStatus.FINISHED;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void beforeStep(StepExecution stepExecution) {
        final ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();
        this.books = (List<Book>) executionContext.get(BOOKS);
        this.letters = (List<Letter>) executionContext.get(LETTERS);
        this.elses = (List<Els>) executionContext.get(EQUIDISTANT_LETTER_SEQUENCES);
        log.debug("Step is starting and data was retrieved from ExecutionContext");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        final ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();
        executionContext.put(BOOKS, this.books);
        executionContext.put(LETTERS, this.letters);
        executionContext.put(EQUIDISTANT_LETTER_SEQUENCES, this.elses);
        log.debug("Step is completed and data was put into ExecutionContext");

        return ExitStatus.COMPLETED;
    }
}
