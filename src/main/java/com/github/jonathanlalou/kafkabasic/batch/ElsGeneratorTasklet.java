package com.github.jonathanlalou.kafkabasic.batch;

import com.github.jonathanlalou.kafkabasic.domain.Book;
import com.github.jonathanlalou.kafkabasic.domain.Els;
import com.github.jonathanlalou.kafkabasic.domain.Letter;
import com.github.jonathanlalou.kafkabasic.service.ElsSequenceGenerator;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
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

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.jonathanlalou.kafkabasic.batch.JsonFileLoadTasklet.BOOKS;
import static com.github.jonathanlalou.kafkabasic.batch.JsonFileLoadTasklet.LETTERS;

@Component(ElsGeneratorTasklet.ELS_GENERATOR_TASKLET)
@Slf4j
@Getter
@Setter
@StepScope
public class ElsGeneratorTasklet implements Tasklet, StepExecutionListener {
    public static final String ELS_GENERATOR_TASKLET = "elsGeneratorTasklet";
    public static final String EQUIDISTANT_LETTER_SEQUENCES = "equidistantLetterSequences";

    @Value("${equidistantLetterSequenceGenerator.minInterval}")
    private Integer minInterval;
    @Value("${equidistantLetterSequenceGenerator.maxInterval}")
    private Integer maxInterval;

    @Autowired
    private ElsSequenceGenerator elsSequenceGenerator;

    private List<Book> books = new ArrayList<>();
    private List<Letter> letters = new ArrayList<>();
    private String allLetters;

    private List<Els> equidistantLetterSequences = new ArrayList<>();

    @SuppressWarnings("unchecked")
    @Override
    public void beforeStep(StepExecution stepExecution) {
        final ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();
        this.books = (List<Book>) executionContext.get(BOOKS);
        this.letters = (List<Letter>) executionContext.get(LETTERS);
        log.debug("Step is starting and data was retrieved from ExecutionContext");

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        final ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();
        executionContext.put(BOOKS, this.books);
        executionContext.put(LETTERS, this.letters);
        executionContext.put(EQUIDISTANT_LETTER_SEQUENCES, this.equidistantLetterSequences);
        log.debug("Step is completed and data was put into ExecutionContext");

        return ExitStatus.COMPLETED;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        allLetters = letters.stream().map(it -> String.valueOf(it.getCharacter())).collect(Collectors.joining());
        log.info("Joined all letters: {}", allLetters.substring(0, 100));
        FileUtils.writeStringToFile(new File(JsonFileLoadTasklet.INPUT_FOLDER + "allLetters.txt"), allLetters, Charset.defaultCharset());

        equidistantLetterSequences = elsSequenceGenerator.generateEquidistantLetterSequences(minInterval, maxInterval, allLetters);

        return RepeatStatus.FINISHED;
    }

}
