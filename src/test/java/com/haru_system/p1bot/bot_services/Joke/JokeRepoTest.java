package com.haru_system.p1bot.bot_services.Joke;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class JokeRepoTest {
    
    @Test
    void getJokeTest() {
        String joke = JokeRepo.getJoke();

        System.out.println(joke);
        assertTrue(joke instanceof String, "Joke should be a String");
    }

}
