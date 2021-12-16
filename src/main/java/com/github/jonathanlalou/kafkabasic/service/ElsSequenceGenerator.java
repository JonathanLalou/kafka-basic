package com.github.jonathanlalou.kafkabasic.service;

import com.github.jonathanlalou.kafkabasic.batch.ElsSendToKafkaTasklet;
import com.github.jonathanlalou.kafkabasic.domain.Els;
import com.github.jonathanlalou.kafkabasic.repository.ElsRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.text.ChoiceFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ElsSequenceGenerator {

    // DELETEME
    @Autowired
    private ElsRepository elsRepository;
    @Autowired
    private ElsKafkaProducer elsKafkaProducer;

    private Boolean persistElses = false;

    @PostConstruct
    public void postConstruct() {
        Assert.notNull(elsRepository, "elsRepository cannot be null");
        Assert.notNull(elsKafkaProducer, "elsKafkaProducer cannot be null");
    }

    public List<Els> generateEquidistantLetterSequences(Integer _minInterval, Integer _maxInterval, String _allLetters) {
        final List<Els> answer = new ArrayList<>();
        Integer counter = 0;
        for (int interval = _minInterval; interval <= _maxInterval; interval++) {
            for (int firstLetter = 0; firstLetter < interval; firstLetter++) {
                final StringBuilder stringBuilder = new StringBuilder(_allLetters.length() / interval);
                for (int j = firstLetter; /*j < allLetters.length() &&*/ (j + interval) <= _allLetters.length(); j += interval) {
                    stringBuilder.append(_allLetters.charAt(j));
                }
                final Els equidistantLetterSequence = Els
                        .builder()
                        .content(stringBuilder.toString())
                        .interval(interval)
                        .firstLetter(firstLetter + 1)
                        .id(String.format("%04d", interval) + "-" + String.format("%02d", firstLetter + 1))
                        .build();
                log.debug("Added equidistantLetterSequence" + equidistantLetterSequence.toString().substring(0, 100));
//                answer.add(equidistantLetterSequence);         // DELETEME
//                elsRepository.save(equidistantLetterSequence); // DELETEME
                elsKafkaProducer.sendOneElsToKafka(equidistantLetterSequence);
                counter++;
            }
        }
        log.info("{} ELSes were generated", counter);
        return answer;
    }

}
