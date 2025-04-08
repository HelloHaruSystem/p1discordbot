package com.haru_system.p1bot.bot_services;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
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
            e.printStackTrace();
            return null;
        }
    }

    private void saveScheduleToFile() {
        try (ObjectOutputStream objOutStream = new ObjectOutputStream(new FileOutputStream(scheduleFilePath))) {
            objOutStream.writeObject(weeklySchedule);
        } catch (Exception e) {
            System.out.println("Could not save schedule to file: ");
            e.printStackTrace();
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
                channel.sendMessageEmbeds(createDailyScheduleEmbed().build()).queue();
            }
        }, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);        
    }

    // get today's schedule as an Embed
    public EmbedBuilder createDailyScheduleEmbed() {
        int dayOfWeek = LocalDateTime.now().getDayOfWeek().getValue() - 1;
        String dayName = getDayName(dayOfWeek);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("📅 Schedule for " + dayName);
        embed.setColor(Color.PINK);
        embed.setDescription("🔥 Good Morning! Here's what's planned for today: "+ formattedDate() + "🔥");

        List<ScheduleItem> todaysItems = weeklySchedule.get(dayOfWeek);

        if (todaysItems.isEmpty()) {
            embed.addField("No Events", "Nothing scheduled for today. :(", false);
        } else {
            todaysItems.sort((a, b) -> a.getTime().compareTo(b.getTime()));
            
            for (ScheduleItem item : todaysItems) {
                embed.addField(
                    item.getTime(),
                    item.getDescription(),
                    false
                );
            }
        }

        embed.setFooter("Use !schedule help for more commands! :)");
        return embed;
    }

    // get this weeks schedule as an Embed
    public EmbedBuilder createWeeklyScheduleEmbed() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("📅 Schedule for this week!");
        embed.setColor(Color.GREEN);

        for (int i = 0; i < 5; i++) {
            List<ScheduleItem> dayITems = weeklySchedule.get(i);
            StringBuilder sb = new StringBuilder();

            if (dayITems.isEmpty()) {
                sb.append("No events scheduled.\n");
            } else {
                dayITems.sort((a, b) -> a.getTime().compareTo(b.getTime()));
                for (ScheduleItem item : dayITems) {
                    sb.append("`").append(item.getTime()).append("` - ");
                    sb.append(item.getDescription()).append("\n");
                }
            }

            embed.addField(getDayName(i), sb.toString(), false);
        }

        return embed;
    }

    // add item to schedule
    public boolean addScheduleItem(int dayOfWeek, String time, String description) {
        if (dayOfWeek < 0 || dayOfWeek > 5) {
            return false;
        }

        ScheduleItem newItem = new ScheduleItem(time, description);
        weeklySchedule.get(dayOfWeek).add(newItem);
        saveScheduleToFile();
        return true;
    }

    // remove item from schedule by index
    public boolean removeScheduleItem(int dayOfWeek, int index) {
        if (dayOfWeek < 0 || dayOfWeek > 5) {
            return false;
        }

        List<ScheduleItem> dayItems = weeklySchedule.get(dayOfWeek);
        if (index < 0 || index >= dayItems.size()) {
            return false;
        }

        dayItems.remove(index);
        saveScheduleToFile();
        return true;
    }

    // clear all items for a specific day of the week
    public void clearDay(int dayOfWeek) {
        if (dayOfWeek >= 0 && dayOfWeek <= 5) {
            weeklySchedule.get(dayOfWeek).clear();
            saveScheduleToFile();
        }
    }

    // clear the whole schedule
    public void clearAllSchedules() {
        for (int i = 0; i < 5; i++) {
            weeklySchedule.get(i).clear();
        }
        saveScheduleToFile();
    }

    // get the day name of the week by number (monday=0, 1=tuesday and so on)
    private String getDayName(int day) {
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        return days[day];
    }

    // get day index from name
    public int getDayIndex(String dayName) {
        switch (dayName.toLowerCase()) {
            case "monday": case "mon": return 0;
            case "tuesday": case "tue": return 1;
            case "wednesday": case "wed": return 2;
            case "thursday": case "thu": return 3;
            case "friday": case "fri": return 4;
            default: return -1;
        }
    }

    // get a specific fay's schedule items
    public List<ScheduleItem> getDaySchedule(int dayOfWeek) {
        if (dayOfWeek >= 0 && dayOfWeek <= 6) {
            return new ArrayList<>(weeklySchedule.get(dayOfWeek));
        }
        return new ArrayList<>();
    }

    // get today in dd/mm/yyyy format
    private String formattedDate() {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formatted = dateTime.format(formatter); 
        
        return formatted;
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}
