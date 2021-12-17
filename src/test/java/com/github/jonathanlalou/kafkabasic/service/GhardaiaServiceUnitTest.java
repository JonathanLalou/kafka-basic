package com.github.jonathanlalou.kafkabasic.service;

import com.github.jonathanlalou.kafkabasic.batch.GhardaiaHelper;
import com.github.jonathanlalou.kafkabasic.domain.GhardaiaPersistenceMode;
import com.github.jonathanlalou.kafkabasic.dto.JsonBookLoadingResult;
import com.github.jonathanlalou.kafkabasic.repository.ElsRepository;
import com.github.jonathanlalou.kafkabasic.repository.LetterRepository;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class GhardaiaServiceUnitTest {

    private GhardaiaService ghardaiaService = new GhardaiaService();

    @Mock
    private ElsRepository elsRepository;
    @Mock
    private LetterRepository letterRepository;

    @Mock
    private LetterKafkaProducer letterKafkaProducer;

    private GhardaiaHelper ghardaiaHelper = new GhardaiaHelper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ghardaiaService.setElsRepository(elsRepository);
        ghardaiaService.setLetterRepository(letterRepository);
        ghardaiaService.setLetterKafkaProducer(letterKafkaProducer);
        ghardaiaService.setGhardaiaHelper(ghardaiaHelper);

        ghardaiaService.setPageSize(100);
        ghardaiaService.setPersistenceMode(GhardaiaPersistenceMode.ASYNCHRONOUS);
    }

    @Test
    public void processOneJsonBook() throws IOException {
        final String INPUT_FOLDER = "./src/main/resources/text/";

        final String json = IOUtils.toString(new FileReader(INPUT_FOLDER + "01.json"));
        final JsonBookLoadingResult actual = ghardaiaService.processOneJsonBook(json, 1, 1);
//        System.out.println(actual);
        assertEquals(78144, actual.getLetterAbsoluteRank());
        assertEquals(78144, actual.getLetters().size());
        assertEquals(50, actual.getBook().getChapters().size());
        assertEquals(1, actual.getBook().getBook());
    }
}