package com.haru_system.p1bot;

import com.haru_system.p1bot.bot_services.CommandHandler;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class App 
{
    public static void main(String[] args) {
        // makes sure the app is starting
        System.out.println(helloWorld());

        // bot token and channel id
        Dotenv dotenv = Dotenv.load();
        String botToken = dotenv.get("BOT_TOKEN");
        String channelId = dotenv.get("DISCORD_TEST_CHANNEL");

        // Initialize JDA
        JDA jda = JDABuilder.createDefault(botToken)
            .enableIntents(GatewayIntent.MESSAGE_CONTENT) // enabling the message_content
            .build();
            
        try {
            jda.awaitReady();
            CommandHandler commandHandler = new CommandHandler(jda, channelId);
            // adding listeners
            jda.addEventListener(commandHandler);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                commandHandler.shutdown();
                jda.shutdown();
            }));
            
        } catch (InterruptedException e) {
            System.out.println("Exception trying to build and run bot\nException: ");
            e.printStackTrace();
        }
    }

    public static String helloWorld() {
        return "Hello World!";
    }
}
