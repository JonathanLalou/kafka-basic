package com.github.jonathanlalou.kafkabasic.batch;

import com.github.jonathanlalou.kafkabasic.domain.Book;
import com.github.jonathanlalou.kafkabasic.domain.Letter;
import com.github.jonathanlalou.kafkabasic.dto.JsonBookLoadingResult;
import com.github.jonathanlalou.kafkabasic.repository.LetterRepository;
import com.github.jonathanlalou.kafkabasic.service.GhardaiaService;
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

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static com.github.jonathanlalou.kafkabasic.batch.GhardaiaHelper.INPUT_FOLDER;

@Component(JsonFileLoadTasklet.JSON_FILE_LOAD_TASKLET)
@Slf4j
@Getter
@Setter
@StepScope
public class JsonFileLoadTasklet implements Tasklet, StepExecutionListener {
    public static final String JSON_FILE_LOAD_TASKLET = "JsonFileLoadTasklet";
    public static final String BOOKS = "books";
    public static final String LETTERS = "letters";

    @Value("${file.inputs}")
    private String[] fileInputs;

    @Autowired
    private LetterRepository letterRepository;
    @Autowired
    private GhardaiaService ghardaiaService;

    private List<Book> books = new ArrayList<>();
    private List<Letter> letters = new ArrayList<>();

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        int letterAbsoluteRank = 1;
        JsonBookLoadingResult jsonBookLoadingResult = new JsonBookLoadingResult(letterAbsoluteRank, null, null);
        for (int bookRank = 1; bookRank < fileInputs.length; bookRank++) {
            final String fileInput = fileInputs[bookRank];
            log.info("Handling file {}", fileInput);
            final String json = IOUtils.toString(new FileReader(INPUT_FOLDER + fileInput));

            jsonBookLoadingResult = ghardaiaService.processOneJsonBook(json, bookRank, jsonBookLoadingResult.getLetterAbsoluteRank());
            books.add(jsonBookLoadingResult.getBook());
            letters.addAll(jsonBookLoadingResult.getLetters());
        }
        return RepeatStatus.FINISHED;
    }


    @Override
    public void beforeStep(StepExecution stepExecution) {
        // nothing to do on the first step of the job!
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        final ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();
        executionContext.put(BOOKS, this.books);
        executionContext.put(LETTERS, this.letters);
        log.debug("Step is completed and data was put into ExecutionContext");

        return ExitStatus.COMPLETED;
    }
}
