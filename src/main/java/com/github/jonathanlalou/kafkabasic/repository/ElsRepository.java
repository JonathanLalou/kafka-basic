package com.github.jonathanlalou.kafkabasic.repository;

import com.github.jonathanlalou.kafkabasic.domain.Els;
import com.github.jonathanlalou.kafkabasic.domain.Letter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElsRepository extends MongoRepository<Els, Integer> {
}
