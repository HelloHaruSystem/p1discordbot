package com.haru_system.p1bot;

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

        System.out.println(botToken);
    }

    public static String helloWorld() {
        return "Hello World!";
    }
}
