package com.haru_system.bot_services;

import com.haru_system.bot_services.Command;

import java.util.HashMap;
import java.util.Map;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandHandler extends ListenerAdapter {
    
    private final Map<String, Command> commands = new HashMap<>();
    private final String prefix = "!";

    public CommandHandler() {
        // register commands here
        registerCommand("ping", event -> {
            event.getChannel().sendMessage("Pong").queue();
        });

        registerCommand("web", event -> {
            event.getChannel.sendMessage("P1 webpage: http://10.0.1.211:5000/").queue();
        });
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
}
