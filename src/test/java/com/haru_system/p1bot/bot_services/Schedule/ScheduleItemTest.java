package com.haru_system.p1bot.bot_services.Schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ScheduleItemTest {
    
    @Test
    void testConstructorAndGetters() {
        // Arrange
        String expectedTime = "10:00";
        String expectedDescription = "Team Meeting";

        // Act
        ScheduleItem scheduleItem = new ScheduleItem(expectedTime, expectedDescription);

        // Assert
        assertEquals(expectedTime, scheduleItem.getTime());
        assertEquals(expectedDescription, scheduleItem.getDescription());
    }
}