package com.haru_system.p1bot.bot_services;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@FunctionalInterface
public interface Command {
    void execute(MessageReceivedEvent event);
}
