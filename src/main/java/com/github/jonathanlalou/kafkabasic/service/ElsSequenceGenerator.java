package com.github.jonathanlalou.kafkabasic.service;

import com.github.jonathanlalou.kafkabasic.domain.Els;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Getter
@Setter
public class ElsSequenceGenerator {
    public List<Els> generateEquidistantLetterSequences(Integer _minInterval, Integer _maxInterval, String _allLetters) {
        final List<Els> answer = new ArrayList<>();
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
                        .id(interval + "-" + (firstLetter + 1))
                        .build();
                log.debug("Added equidistantLetterSequence" + equidistantLetterSequence.toString().substring(0, 100));
                answer.add(equidistantLetterSequence);
            }
        }
        return answer;
    }

}
