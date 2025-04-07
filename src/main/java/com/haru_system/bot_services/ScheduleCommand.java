package com.haru_system.bot_services;

import java.awt.Color;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ScheduleCommand {
    private final ScheduleService scheduleService;

    public ScheduleCommand(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    public void handleCommand(MessageReceivedEvent event, String[] args) {
        if (args.length == 0) {
            // execute default command: show today's schedule
            event.getChannel().sendMessageEmbeds(scheduleService.createDailyScheduleEmbed().build()).queue();
            return;
        }

        switch (args[0].toLowerCase()) {
            case "today":
                event.getChannel().sendMessageEmbeds(scheduleService.createDailyScheduleEmbed().build()).queue();
                break;

            case "week":
            case "weekly":
                event.getChannel().sendMessageEmbeds(scheduleService.createWeeklyScheduleEmbed().build()).queue();
                break;
            
            case "add":
                handleAddCommand(event, args);
                break;
            
            case "remove":
                handleRemoveCommand(event, args);
                break;

            case "clear":
                handleClearCommand(event, args);
                break;

            case "help":
                handleHelpCommand(event, args);
                break;

            default:
                sendHelpEmbed(event);
                break;
        }
    }

    private void handleAddCommand(MessageReceivedEvent event, String[] args) {
        // in case of missing arguments
        if (args.length < 4) {
            event.getChannel().sendMessage("❌ Not enough arguments. Use: `!schedule add [day] [time] [description]`").queue();
            return;
        }

        String day = args[1];
        String time = args[2];

        // combine the rest of the arguments as the descriptions
        StringBuilder description = new StringBuilder();
        for (int i = 3; i < args.length; i++) {
            description.append(args[i]).append(" ");
        }

        int dayIndex = scheduleService.getDayIndex(day);
        if (dayIndex == -1) {
            event.getChannel().sendMessage("❌ Invalid day. Please use full day names (monday, tuesday, wednesday...).").queue();
            return;
        }

        if (scheduleService.addScheduleItem(dayIndex, time, description.toString().trim())) {
            EmbedBuilder embed = new EmbedBuilder()
                .setTitle("#✅ Event added the schedule")
                .setColor(Color.GREEN)
                .setDescription("Added to " + day + " at " + time + ":\n" + description.toString().trim());

            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        } else {
            event.getChannel().sendMessage("❌ Failed to add event to the schedule. Please check your the format is correct.");
        }
    }

    public void handleRemoveCommand(MessageReceivedEvent event, String[] args) {
        // in case of missing arguments
        if (args.length < 3) {
            event.getChannel().sendMessage("❌ Not enough arguments. Use: `!schedule remove [day] [index]`").queue();
            return;
        }

        String day = args[1];
        int dayIndex = scheduleService.getDayIndex(day);
        if (dayIndex == -1) {
            event.getChannel().sendMessage("❌ Invalid day. Please use full day names (monday, tuesday, wednesday...).").queue();
            return;
        }

        try {
            int itemIndex = Integer.parseInt(args[2]) - 1; // for 0 indexing :)

            List<ScheduleItem> dayItems = scheduleService.getDaySchedule(dayIndex);
            if (itemIndex < 0 || itemIndex >= dayItems.size()) {
                event.getChannel().sendMessage("❌ Invalid index. Please use `!schedule " + day + "` to see available items.").queue();
                return;
            }

            ScheduleItem removedItem = dayItems.get(itemIndex);

            if (scheduleService.removeScheduleItem(dayIndex, itemIndex)) {
                EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("#✅ Event Removed!")
                    .setColor(Color.RED)
                    .setDescription("Removed from " + day + ":\n" + removedItem.getTime() + " - " + removedItem.getDescription());
                
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            } else {
                event.getChannel().sendMessage("❌ Failed to remove event. Please check index.").queue();
            }
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage("❌ Invalid index number. Please use a number.").queue();
        }
    }


}
