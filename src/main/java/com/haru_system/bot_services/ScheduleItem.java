package com.haru_system.bot_services;

import java.io.Serializable;

public class ScheduleItem implements Serializable {
    @SuppressWarnings("unused")
    private static final Long serialVersionUID = 1L;

    private final String time;
    private final String description;
    
    public ScheduleItem(String time, String description) {
        this.time = time;
        this.description = description;
    }

    public String getTime() {
        return this.time;
    }

    public String getDescription() {
        return this.description;
    }
}
