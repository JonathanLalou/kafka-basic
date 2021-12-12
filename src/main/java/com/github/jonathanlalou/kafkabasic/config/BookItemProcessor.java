package com.github.jonathanlalou.kafkabasic.config;

import com.github.jonathanlalou.kafkabasic.domain.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@StepScope
@Slf4j
public class BookItemProcessor implements ItemProcessor<Book, Book> {
    @Override
    public Book process(Book book) throws Exception {
        log.info("In processor: " + book.toString().substring(0, 100));
        log.info("Book has {} chapters", book.getText().size());
        for (int k = 0; k < book.getText().size(); k++) {
            log.info("Chapter {} has {} verses", k + 1, book.getText().get(k).size());
        }
        final List<String> chapters = book.getText().get(0);
        for (int j = 0; j < 3; j++) {
            final String chapter = chapters.get(j);
            log.info("Chapter {} has {} verses", chapter, chapter.length());

//            for (String verse : strings) {
            for (int i = 0; i < chapter.length(); i++) {
                System.out.println(chapter.charAt(i));
            }
        }
        System.out.println("book.getText().size(): " + chapters);
        return book;

    }
}
