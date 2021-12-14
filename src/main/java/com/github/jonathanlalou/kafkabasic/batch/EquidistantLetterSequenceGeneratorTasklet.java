package com.github.jonathanlalou.kafkabasic.batch;

import com.github.jonathanlalou.kafkabasic.domain.Book;
import com.github.jonathanlalou.kafkabasic.domain.EquidistantLetterSequence;
import com.github.jonathanlalou.kafkabasic.domain.Letter;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component(EquidistantLetterSequenceGeneratorTasklet.EQUIDISTANT_LETTER_SEQUENCE_GENERATOR_TASKLET)
@Slf4j
@Getter
@Setter
@StepScope // TODO rename as tasklet and extract the generator in a different component
public class EquidistantLetterSequenceGeneratorTasklet implements Tasklet, StepExecutionListener {
    public static final String EQUIDISTANT_LETTER_SEQUENCE_GENERATOR_TASKLET = "equidistantLetterSequenceGeneratorTasklet";

    @Value("${equidistantLetterSequenceGenerator.minInterval}")
    private Integer minInterval;
    @Value("${equidistantLetterSequenceGenerator.maxInterval}")
    private Integer maxInterval;

    private List<Book> books = new ArrayList<>();
    private List<Letter> letters = new ArrayList<>();
    private String allLetters;

    private List<EquidistantLetterSequence> equidistantLetterSequences = new ArrayList<>();

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
        executionContext.put("equidistantLetterSequences", this.equidistantLetterSequences);
        log.debug("Step is completed and data was put into ExecutionContext");

        return ExitStatus.COMPLETED;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        allLetters = letters.stream().map(it -> String.valueOf(it.getCharacter())).collect(Collectors.joining());
        log.info("Joined all letters: {}", allLetters.substring(0, 100));

        equidistantLetterSequences = generateEquidistantLetterSequences(minInterval, maxInterval, allLetters);

        return RepeatStatus.FINISHED;
    }

    protected List<EquidistantLetterSequence> generateEquidistantLetterSequences(Integer _minInterval, Integer _maxInterval, String _allLetters) {
        final List<EquidistantLetterSequence> answer = new ArrayList<>();
        for (int interval = _minInterval; interval <= _maxInterval; interval++) {
            for (int firstLetter = 0; firstLetter < interval; firstLetter++) {
                final StringBuilder stringBuilder = new StringBuilder(_allLetters.length() / interval);
                for (int j = firstLetter; /*j < allLetters.length() &&*/ (j + interval) < _allLetters.length(); j += interval) {
                    stringBuilder.append(_allLetters.charAt(j));
                }
                final EquidistantLetterSequence equidistantLetterSequence = EquidistantLetterSequence
                        .builder()
                        .content(stringBuilder.toString())
                        .interval(interval)
                        .firstLetter(firstLetter + 1)
                        .build();
                log.debug("Added equidistantLetterSequence" + equidistantLetterSequence.toString().substring(0, 100));
                answer.add(equidistantLetterSequence);
            }
        }
        return answer;
    }
}
