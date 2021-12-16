package com.github.jonathanlalou.kafkabasic.service;

import com.github.jonathanlalou.kafkabasic.domain.Els;
import com.github.jonathanlalou.kafkabasic.domain.GhardaiaPersistenceMode;
import com.github.jonathanlalou.kafkabasic.repository.ElsRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ElsSequenceGenerator {

    @Autowired
    private ElsRepository elsRepository;

    @Lazy
    @Autowired
    private ElsKafkaProducer elsKafkaProducer;

    @Value("${ghardaia.persistence.mode}")
    private GhardaiaPersistenceMode persistenceMode;
    @Value("${ghardaia.persistence.pageSize}")
    private Integer pageSize;

    private Boolean persistElses = false;

    @PostConstruct
    public void postConstruct() {
        Assert.notNull(elsRepository, "elsRepository cannot be null");
        Assert.notNull(elsKafkaProducer, "elsKafkaProducer cannot be null");
    }

    public List<Els> generateEquidistantLetterSequences(Integer _minInterval, Integer _maxInterval, String _allLetters) {
        final List<Els> answer = new ArrayList<>();
        Integer counter = 0;
        final Set<Els> setOfElses = new HashSet<>(pageSize);
        for (int interval = _minInterval; interval <= _maxInterval; interval++) {
            for (int firstLetter = 0; firstLetter < interval; firstLetter++) {
                final StringBuilder stringBuilder = new StringBuilder(_allLetters.length() / interval);
                for (int j = firstLetter; (j + interval) <= _allLetters.length(); j += interval) {
                    stringBuilder.append(_allLetters.charAt(j));
                }
                final Els equidistantLetterSequence = Els
                        .builder()
                        .content(stringBuilder.toString())
                        .interval(interval)
                        .firstLetter(firstLetter + 1)
                        .id(String.format("%04d", interval) + "-" + String.format("%04d", firstLetter + 1))
                        .build();
                // TODO improve
                if (equidistantLetterSequence.toString().length() > 1024 * 1024) {
                    elsRepository.save(equidistantLetterSequence);
                } else if (persistenceMode == GhardaiaPersistenceMode.SYNCHRONOUS) {
                    setOfElses.add(equidistantLetterSequence);
                            /* Regularly, save a set of entities. It is more efficient to save 1 time 100 entities, than to save 100 times 1 entity ;-).
                            We don't save all the entities at one time, in order to avoid an OutOfMemory error.
                            * */
                    if (0 == setOfElses.size() % pageSize) {
                        elsRepository.saveAll(setOfElses);
                        setOfElses.clear();
                    }
                } else {
                    elsKafkaProducer.sendOneElsToKafka(equidistantLetterSequence);
                }
                log.debug("Added equidistantLetterSequence" + equidistantLetterSequence.toString().substring(0, 100));
                // for unit tests only
                // TODO clean and improve
                if (persistElses) {
                    answer.add(equidistantLetterSequence);
                }
                counter++;
            }
        }
        log.info("{} ELSes were generated", counter);
        return answer;
    }

}
