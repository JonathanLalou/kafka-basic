package com.github.jonathanlalou.kafkabasic.batch;

import com.github.jonathanlalou.kafkabasic.domain.Book;
import com.github.jonathanlalou.kafkabasic.domain.BookDTO;
import com.github.jonathanlalou.kafkabasic.domain.Chapter;
import com.github.jonathanlalou.kafkabasic.domain.GhardaiaPersistenceMode;
import com.github.jonathanlalou.kafkabasic.domain.Letter;
import com.github.jonathanlalou.kafkabasic.domain.Verse;
import com.github.jonathanlalou.kafkabasic.repository.LetterRepository;
import com.github.jonathanlalou.kafkabasic.service.LetterKafkaProducer;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    @Value("${ghardaia.persistence.mode}")
    private GhardaiaPersistenceMode persistenceMode;
    @Value("${ghardaia.persistence.pageSize}")
    private Integer pageSize;

    @Autowired
    private GhardaiaHelper ghardaiaHelper;
    @Autowired
    private LetterRepository letterRepository;

    @Lazy
    @Autowired
    private LetterKafkaProducer letterKafkaProducer;

    private final Gson gson = new Gson();

    protected static final String INPUT_FOLDER = "./src/main/resources/text/";

    private List<Book> books = new ArrayList<>();
    private List<Letter> letters = new ArrayList<>();

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        // TODO extract in a Component
        int bookRank = 1;
        int letterAbsoluteRank = 1;
        int chapterRankInBook;
        int verseRankInChapter;

        for (String fileInput : fileInputs) {
            log.info("Handling file {}", fileInput);
            final String json = IOUtils.toString(new FileReader(INPUT_FOLDER + fileInput));

            final BookDTO bookDTO = gson.fromJson(json, BookDTO.class);
            final Book book = new Book();
            final Set<Letter> setOfLetters = new HashSet<>(pageSize);
            chapterRankInBook = 1;
            for (List<String> chapterDTOs : bookDTO.getText()) {
                final Chapter chapter = new Chapter();
                chapter.setChapter(chapterRankInBook);
                verseRankInChapter = 1;
                for (String verseDTO : chapterDTOs) {
                    chapter.getVerses().add(new Verse(verseRankInChapter, verseDTO.length(), verseDTO));
                    for (int letterRankInVerse = 0; letterRankInVerse < verseDTO.length(); ++letterRankInVerse) {
                        final Character hebrewCharacter = verseDTO.charAt(letterRankInVerse);
                        final Character latinCharacter = ghardaiaHelper.hebrew2Latin(hebrewCharacter);
                        if (null == latinCharacter) { // TODO clean
                            // TODO handle the theoretically needed letterRankInVerse--
                            continue;
                        }
                        final Boolean isFinal = ghardaiaHelper.isFinal(hebrewCharacter);
                        final Letter letter = Letter.builder()
                                .book(bookRank)
                                .chapter(chapterRankInBook)
                                .verse(verseRankInChapter)
                                .letterRank(letterRankInVerse + 1)
                                .absoluteRank(letterAbsoluteRank)
                                .character(latinCharacter)
                                .heCharacter(hebrewCharacter)
                                .finalLetter(isFinal)
                                .build();
                        if (persistenceMode == GhardaiaPersistenceMode.SYNCHRONOUS) {
                            setOfLetters.add(letter);
                            /* Regularly, save a set of entities. It is more efficient to save 1 time 100 entities, than to save 100 times 1 entity ;-).
                            We don't save all the entities at one time, in order to avoid an OutOfMemory error.
                            * */
                            if (0 == setOfLetters.size() % pageSize) {
                                letterRepository.saveAll(setOfLetters);
                                setOfLetters.clear();
                            }
                        } else {
                            letterKafkaProducer.sendOneLetterToKafka(letter);
                        }
                        letters.add(letter);
                        letterAbsoluteRank++;
                    }

                    verseRankInChapter++;
                }
                chapter.setSize(verseRankInChapter);
                chapterRankInBook++;
                book.getChapters().add(chapter);
            }
            book.setBook(bookRank);
            books.add(book);
            bookRank++;
            log.info("Book {} has {} chapters, {} verses ; total number of letters until now: {}."
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
