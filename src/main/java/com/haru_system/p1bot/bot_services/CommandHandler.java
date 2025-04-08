package com.haru_system.p1bot.bot_services;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.haru_system.p1bot.bot_services.Joke.JokeRepo;
import com.haru_system.p1bot.bot_services.Schedule.ScheduleCommand;
import com.haru_system.p1bot.bot_services.Schedule.ScheduleService;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandHandler extends ListenerAdapter {
    
    private final Map<String, Command> commands = new HashMap<>();
    private final String prefix = "!";
    private final ScheduleService scheduleService;
    private final ScheduleCommand scheduleCommand;

    public CommandHandler(JDA jda, String channelId) {

        // initialize the schedule service with the channel id
        this.scheduleService = new ScheduleService(jda, channelId);
        this.scheduleCommand = new ScheduleCommand(scheduleService);

        // register commands here
        registerCommand("ping", event -> {
            event.getChannel().sendMessage("Pong").queue();
        });

        registerCommand("web", event -> {
            event.getChannel().sendMessage("Visit P1's webpage at: http://10.0.1.211:5000/").queue();
        });

        registerCommand("schedule", event -> {
            // Parse arguments
            String[] args = event.getMessage().getContentRaw()
                .substring(prefix.length() + "schedule".length()).trim().split("\\s+");
            
            // handle the schedule commands
            scheduleCommand.handleCommand(event, args);
        });

        registerCommand("joke", event -> {
            // get joke
            String joke = JokeRepo.getJoke();
            EmbedBuilder embed = new EmbedBuilder()
                .setTitle("🤭 Here is a joke 🤭")
                .setColor(Color.MAGENTA)
                .setDescription(joke);

            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        });

        // more commands goes here
    }

    public void registerCommand(String name, Command command) {
        commands.put(name.toLowerCase(), command);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        // getContentRaw is an atomic getter
        // alternatively there is getContentDisplay() which is a lazy getter which modifies the content e.g. console view (strip discord formatting)
        String content = event.getMessage().getContentRaw();
        String channel = event.getChannel().getName();

        // Print debug statement to verify that the event is received
        System.out.println("Message received: " + content);
        System.out.println("In channel: " + channel);

        if (!content.startsWith(prefix)) {
            return;
        }

        // get command name and arguments
        String[] parts = content.substring(prefix.length()).trim().split("\\s", 2);
        String commandName = parts[0].toLowerCase();

        // execute command if it exists
        Command command = commands.get(commandName);
        if (command != null) {
            command.execute(event);
        }
    }

    public void shutdown() {
        scheduleService.shutdown();
    }
}
