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
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@Getter
@Setter
public class FeedTasklet implements Tasklet, StepExecutionListener {
    @Value("${file.inputs}")
    private String[] fileInputs;

    @Autowired
    private GhardaiaHelper ghardaiaHelper;

    private final Gson gson = new Gson();

    private final String INPUT_FOLDER = "./src/main/resources/text/";

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        int bookRank = 1;
        int letterAbsoluteRank = 1;
        int chapterRank;
        int verseRank;
        final List<Book> books = new ArrayList<>();
        final List<Letter> letters = new ArrayList<>();

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
                        if (StringUtils.isBlank("" + latinCharacter)) { // TODO clean
                            i--;
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
        // TODO
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }
}
