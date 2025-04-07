package com.haru_system.bot_services;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class ScheduleService {
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final JDA jda;
    private final String channelId;

    public ScheduleService(JDA jda, String channelId) {
        this.jda = jda;
        this.channelId = channelId;
        setupDailyScheduleAnnouncement();
    }

    private void setupDailyScheduleAnnouncement() {
        // calculate initial delay until 08:00
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.with(LocalTime.of(8, 0));
        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1);
        }

        long initialDelay = java.time.Duration.between(now, nextRun).getSeconds();

        // Schedule the task to run daily at 08:00
        scheduler.scheduleAtFixedRate(() -> {
            TextChannel channel = jda.getTextChannelById(channelId);
            if (channel != null) {
                String todaysSchedule = generateSchedule();
                channel.sendMessage("#Good morning! Here's todays schedule:\n" + todaysSchedule);
            }
        }, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);        
    }

    public String generateSchedule() {
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        int dayOfWeek = LocalDateTime.now().getDayOfWeek().getValue() % 5;

        StringBuilder schedule = new StringBuilder();

        switch (dayOfWeek) {
            case 1:
                schedule.append("08:00 - Daily Stand-up");
                schedule.append("12:00 - Website");
                break;
            default:
                schedule.append("No schedule today");
        }
        
        return schedule.toString();
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}
