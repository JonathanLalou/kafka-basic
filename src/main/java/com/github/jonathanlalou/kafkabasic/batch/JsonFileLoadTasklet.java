package com.github.jonathanlalou.kafkabasic.batch;

import com.github.jonathanlalou.kafkabasic.domain.Book;
import com.github.jonathanlalou.kafkabasic.domain.BookDTO;
import com.github.jonathanlalou.kafkabasic.domain.Chapter;
import com.github.jonathanlalou.kafkabasic.domain.Letter;
import com.github.jonathanlalou.kafkabasic.domain.Verse;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
    private GhardaiaHelper ghardaiaHelper;

    private final Gson gson = new Gson();

    private final String INPUT_FOLDER = "./src/main/resources/text/";

    private List<Book> books = new ArrayList<>();
    private List<Letter> letters = new ArrayList<>();

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        int bookRank = 1;
        int letterAbsoluteRank = 1;
        int chapterRank;
        int verseRank;

        for (String fileInput : fileInputs) {
            log.info("Handling file {}", fileInput);
            final String json = IOUtils.toString(new FileReader(INPUT_FOLDER + fileInput));
            final BookDTO bookDTO = gson.fromJson(json, BookDTO.class);

            final Book book = new Book();
            chapterRank = 1;
            for (List<String> chapterDTOs : bookDTO.getText()) {
                final Chapter chapter = new Chapter();
                chapter.setChapter(chapterRank);
                verseRank = 1;
                for (String verseDTO : chapterDTOs) {
                    chapter.getVerses().add(new Verse(verseRank, verseDTO.length(), verseDTO));
                    for (int i = 0; i < verseDTO.length(); ++i) {
                        final Character hebrewCharacter = verseDTO.charAt(i);
                        final Character latinCharacter = ghardaiaHelper.hebrew2Latin(hebrewCharacter);
                        if (null == latinCharacter) { // TODO clean
                            // TODO handle the theoretically needed i--
                            continue;
                        }
                        final Boolean isFinal = ghardaiaHelper.isFinal(hebrewCharacter);
                        letters.add(Letter.builder()
                                .book(bookRank)
                                .chapter(chapterRank)
                                .verse(verseRank)
                                .absoluteRank(letterAbsoluteRank)
                                .character(latinCharacter)
                                .heCharacter(hebrewCharacter)
                                .finalLetter(isFinal)
                                .build()
                        );
                        letterAbsoluteRank++;
                    }

                    verseRank++;
                }
                chapter.setSize(verseRank);
                chapterRank++;
                book.getChapters().add(chapter);
            }
            book.setBook(bookRank);
            books.add(book);
            bookRank++;
            log.warn("Book {} has {} chapters, {} verses ; total number of letters until now: {}."
                    , bookDTO.getTitle()
                    , book.getChapters().size()
                    , book.getChapters().stream().mapToInt(it -> it.getVerses().size()).sum()
                    , letters.size()
            );
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
