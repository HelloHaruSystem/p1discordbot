package com.haru_system.bot_services;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class ScheduleService {
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final JDA jda;
    private final String channelId;
    // Map for storing schedules for each day
    private final Map<Integer, List<ScheduleItem>> weeklySchedule;
    // file to store schedule data
    private final String scheduleFilePath = "schedule_data.ser";

    // constructor
    public ScheduleService(JDA jda, String channelId) {
        this.jda = jda;
        this.channelId = channelId;

        // try to load existing schedule from file or create new empty one
        Map<Integer, List<ScheduleItem>> loadedSchedule = loadScheduleFromFile();
        if (loadedSchedule != null) {
            this.weeklySchedule = loadedSchedule;
        } else {
            this.weeklySchedule = new HashMap<>();
            // initialize empty schedule for each day
            for (int i = 0; i < 5; i++) {
                weeklySchedule.put(i, new ArrayList<>());
            }
        }

        setupDailyScheduleAnnouncement();
    }

    // handle warning properly
    @SuppressWarnings("unchecked")
    private Map<Integer, List<ScheduleItem>> loadScheduleFromFile() {
        try (ObjectInputStream objInStream = new ObjectInputStream(new FileInputStream(scheduleFilePath))) {
            return (Map<Integer, List<ScheduleItem>>) objInStream.readObject();
        } catch (Exception e) {
            System.out.println("could not load schedule from file: ");
            e.getStackTrace();
            return null;
        }
    }

    private void saveScheduleToFile() {
        try (ObjectOutputStream objOutStream = new ObjectOutputStream(new FileOutputStream(scheduleFilePath))) {
            objOutStream.writeObject(weeklySchedule);
        } catch (Exception e) {
            System.out.println("Could not save schedule to file: ");
            e.getStackTrace();
        }
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
            case 2:
                schedule.append("08:00 - Daily Stand-up");
                schedule.append("12:00 - personal project");
                break;
            case 3:
                schedule.append("08:00 - Daily Stand-up");
                schedule.append("10:30 - activity");
                break;
            case 4:
                schedule.append("08:00 - Daily Stand-up");
                schedule.append("12:00 - ???");
                break;
            case 5:
                schedule.append("08:00 - Daily Stand-up");
                schedule.append("12:00 - retro");
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
