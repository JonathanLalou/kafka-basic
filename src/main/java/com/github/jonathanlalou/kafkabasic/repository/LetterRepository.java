package com.github.jonathanlalou.kafkabasic.repository;

import com.github.jonathanlalou.kafkabasic.Letter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LetterRepository extends MongoRepository<Letter, Integer> {
}
