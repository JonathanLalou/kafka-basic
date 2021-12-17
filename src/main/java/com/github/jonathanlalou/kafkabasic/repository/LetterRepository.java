package com.github.jonathanlalou.kafkabasic.repository;

import com.github.jonathanlalou.kafkabasic.domain.Letter;
import org.springframework.data.mongodb.core.aggregation.VariableOperators;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface LetterRepository extends MongoRepository<Letter, Integer> {
    List<Letter> findByAbsoluteRankInOrderByAbsoluteRank(Collection<Integer> absoluteRanks);

}
