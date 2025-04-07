package com.haru_system.p1bot;

import com.haru_system.bot_services.PingPong;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class App 
{
    public static void main(String[] args) {
        // makes sure the app is starting
        System.out.println(helloWorld());

        // bot token
        Dotenv dotenv = Dotenv.load();
        String botToken = dotenv.get("BOT_TOKEN");

        try {
            // Initialize JDA and add the PingPong listener
            JDA api = JDABuilder.createDefault(botToken).build();
            api.addEventListener(new PingPong());

            // optional block main thread until bot is ready?
            api.awaitReady();
        } catch (Exception e) {
            System.out.println("Exception trying to build and run bot\nException: " + e.getMessage());
        }
    }

    public static String helloWorld() {
        return "Hello World!";
    }
}
