package com.haru_system.p1bot.bot_services.Joke;

import com.simtechdata.jokes.Jokes;
import com.simtechdata.jokes.enums.Category;
import com.simtechdata.jokes.enums.Flag;
import com.simtechdata.jokes.enums.Format;
import com.simtechdata.jokes.enums.Language;
import com.simtechdata.jokes.enums.Type;

public class JokeRepo {
    
    public static String getJoke() {
        Jokes jokes = new Jokes.Builder()
        .addCategory(Category.PUN, Category.PROGRAMMING, Category.MISC)
        .setLanguage(Language.ENGLISH)
        .addBlackList(Flag.EXPLICIT, Flag.NSFW, Flag.SEXIST, Flag.RELIGIOUS, Flag.RACIST)
        .setFormat(Format.JSON)
        .type(Type.SINGLE)
        .safeMode()
        .build();

        String joke = jokes.getOnePart();
        
        return joke;
    }
}
