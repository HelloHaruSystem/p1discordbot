package com.haru_system.p1bot.bot_services.Joke;

import com.simtechdata.jokes.Jokes;
import com.simtechdata.jokes.enums.Category;
import com.simtechdata.jokes.enums.Flag;
import com.simtechdata.jokes.enums.Format;
import com.simtechdata.jokes.enums.Language;
import com.simtechdata.jokes.enums.Type;

// using https://sv443.net/jokeapi/v2/ 
public class JokeRepo {
    
    // calling https://jokeapi.dev/ for jokoes
    // remember to set filters with addBlackList()
    public static String getJoke() {
        Jokes jokes = new Jokes.Builder()
        //  can add back Category.PUN and Category.MISC later if needed
        .addCategory(Category.PROGRAMMING)
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
