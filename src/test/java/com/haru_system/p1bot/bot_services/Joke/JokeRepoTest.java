package com.haru_system.p1bot.bot_services.Joke;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JokeRepoTest {
    
    private String joke;

    @BeforeEach
    void setUp() {
        joke = JokeRepo.getJoke();
    }

    @Test
    void getJokeTest() {
        assertTrue(joke instanceof String, "Joke should be a String");
    }

    @Test
    void GetJokeTestNotEmpty() {
        assertFalse(joke.isEmpty());
    }

}
