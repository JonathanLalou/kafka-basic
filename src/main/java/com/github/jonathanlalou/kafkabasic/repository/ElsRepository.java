package com.github.jonathanlalou.kafkabasic.repository;

import com.github.jonathanlalou.kafkabasic.domain.Els;
import com.github.jonathanlalou.kafkabasic.domain.Letter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElsRepository extends MongoRepository<Els, Integer> {
    List<Els> findTop3ByContentContainsOrderByInterval(String word);

    List<Els> findByContentContainsOrderByInterval(String word, Pageable pageable);
}
