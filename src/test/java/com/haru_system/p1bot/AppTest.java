package com.haru_system.p1bot;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


public class AppTest {
    
    @Test
    void HelloWorkTest() {
        String result = App.helloWorld();
        String expectedOutput = "Hello World!";

        assertEquals(expectedOutput, result);
    }
}
