package com.haru_system.bot_services;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PingPong extends ListenerAdapter {
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        // Print debug statement to verify that the event is received
        System.out.println("Message received: " + event.getMessage().getContentRaw());
        System.out.println("In channel: " + event.getChannel().getName());

        Message message = event.getMessage();
        
        // getContentRaw is an atomic getter
        // alternatively there is getContentDisplay() which is a lazy getter which modifies the content e.g. console view (strip discord formatting)
        String content = message.getContentRaw();

        if (content.equals("!ping")) {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("Pong").queue();
        }
    }
}
