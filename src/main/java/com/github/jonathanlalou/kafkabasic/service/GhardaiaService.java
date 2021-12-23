package com.github.jonathanlalou.kafkabasic.service;

import com.github.jonathanlalou.kafkabasic.batch.GhardaiaHelper;
import com.github.jonathanlalou.kafkabasic.domain.Book;
import com.github.jonathanlalou.kafkabasic.domain.Chapter;
import com.github.jonathanlalou.kafkabasic.domain.Els;
import com.github.jonathanlalou.kafkabasic.domain.GhardaiaPersistenceMode;
import com.github.jonathanlalou.kafkabasic.domain.Letter;
import com.github.jonathanlalou.kafkabasic.domain.Verse;
import com.github.jonathanlalou.kafkabasic.dto.BookDTO;
import com.github.jonathanlalou.kafkabasic.dto.JsonBookLoadingResult;
import com.github.jonathanlalou.kafkabasic.dto.WordSearchResult;
import com.github.jonathanlalou.kafkabasic.repository.ElsRepository;
import com.github.jonathanlalou.kafkabasic.repository.LetterRepository;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.jonathanlalou.kafkabasic.batch.GhardaiaHelper.INPUT_FOLDER;

@Service
@Slf4j
@Getter
@Setter
@Profile("web")
public class GhardaiaService {
//    private final int range = 20;
    @Autowired
    private ElsRepository elsRepository;
    @Autowired
    private LetterRepository letterRepository;

    @Lazy
    @Autowired
    private LetterKafkaProducer letterKafkaProducer;
    @Autowired
    private GhardaiaHelper ghardaiaHelper;
    @Value("${ghardaia.persistence.mode}")
    private GhardaiaPersistenceMode persistenceMode;
    @Value("${ghardaia.persistence.pageSize}")
    private Integer pageSize;
    private final Gson gson = new Gson();

    @PostConstruct
    public void postConstruct() {
        Assert.notNull(elsRepository, "elsRepository cannot be null");
        Assert.notNull(letterRepository, "letterRepository cannot be null");
    }

    public JsonBookLoadingResult processOneJsonBook(String json, int bookRank, int letterAbsoluteRank) {
        int chapterRankInBook = 1;
        int verseRankInChapter;
        final BookDTO bookDTO = gson.fromJson(json, BookDTO.class);
        final Book book = new Book();
        // All the letters from this book
        final List<Letter> letters = new ArrayList<>();
        // subset of letters that are saved at the same time
        final Set<Letter> setOfLetters = new HashSet<>(pageSize);

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
        log.info("Book {} has {} chapters, {} verses ; total number of letters until now: {}."
                , bookDTO.getTitle()
                , book.getChapters().size()
                , book.getChapters().stream().mapToInt(it -> it.getVerses().size()).sum()
                , letters.size()
        );
        return new JsonBookLoadingResult(letterAbsoluteRank, book, letters);
    }

    public List<WordSearchResult> search(String word, Integer max, Integer range) throws IOException {
        log.info("Searching {} with at most {} results", word, max);

        final List<WordSearchResult> wordSearchResults = new ArrayList<>();
        final List<Els> elses = elsRepository.findByContentContainsOrderByInterval(word, Pageable.ofSize(max));

        for (Els els : elses.subList(0, Math.min(max, elses.size()))) {
            final String content = els.getContent();
            final int index = StringUtils.indexOf(content, word);
            log.info("index of {} in ELS: {}", word, index);
            log.info("Substring (should be same as the searched word): {}", content.substring(index, index + word.length()));
            final List<Triple<String, Character, String>> enclosings = new ArrayList<>();
            final List<String> verses = new ArrayList<>();
            for (int i = 0; i < word.length(); i++) {
                final int currentLetterAbsoluteRank = els.getFirstLetter() + ((index + i) * els.getInterval());
                final Letter letter = letterRepository.findById(currentLetterAbsoluteRank).orElse(new Letter()); // TODO clean
                log.info("Letter: {}", letter);

                // TODO call an external API, such as Sefaria
                final ImmutableTriple<String, Character, String> enclosing = this.buildEnclosing(currentLetterAbsoluteRank, letter, range);
                enclosings.add(enclosing);

                final String readableVerse = buildReadableVerse(letter);
                verses.add(readableVerse);

            }
            wordSearchResults.add(WordSearchResult
                    .builder()
                    .firstLetter(els.getFirstLetter())
                    .interval(els.getInterval())
                    .firstLetterRank(index)
                    .enclosings(enclosings)
                    .verses(verses)
                    .build()
            );
        }
        return wordSearchResults;
    }

    protected String buildReadableVerse(Letter letter) throws IOException {
        final String json = IOUtils.toString(new FileReader(INPUT_FOLDER + String.format("%02d", letter.getBook()) + ".json"));
        final BookDTO bookDTO = gson.fromJson(json, BookDTO.class);
        final int chapter = letter.getChapter();
        final int verse = letter.getVerse();
        return bookDTO.getText().get(chapter - 1).get(verse - 1) + " (" + bookDTO.getTitle() + " " + chapter + ", " + verse + ")";
    }

    protected ImmutableTriple<String, Character, String> buildEnclosing(int currentLetterAbsoluteRank, Letter letter, int range) {
        final String left = letterRepository
                .findByAbsoluteRankInOrderByAbsoluteRank(IntStream.range(currentLetterAbsoluteRank - range, currentLetterAbsoluteRank).boxed().toList())
                .stream()
                .map(it -> String.valueOf(it.getHeCharacter()))
                .collect(Collectors.joining(""));
        final String right = letterRepository
                .findByAbsoluteRankInOrderByAbsoluteRank(IntStream.range(currentLetterAbsoluteRank + 1, currentLetterAbsoluteRank + range).boxed().toList())
                .stream()
                .map(it -> String.valueOf(it.getHeCharacter()))
                .collect(Collectors.joining(""));
        return new ImmutableTriple<>(left, letter.getHeCharacter(), right);
    }
}
