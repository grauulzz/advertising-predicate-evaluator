package com.amazon.ata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Arrays;

import java.util.function.Consumer;
import org.joda.time.DateTime;

public class App {

    public static final GsonBuilder builder = new GsonBuilder();
    private static final Gson gson = builder.setPrettyPrinting().create();
    public static final Consumer<String> print = System.out::println;

    public static final Consumer<Object> toJson = o -> {
        String[] lines = gson.toJson(o).split("\n");

        Arrays.stream(lines).map(line -> new StringBuilder().append(ConsoleColors.CYAN.getValue()).append(line).append(ConsoleColors.CYAN.getValue()).append(ConsoleColors.RESET.getValue()))
                .forEach(System.out::println);
    };

    public static final Consumer<Object> toJsonNoColor = o -> {
        print.accept(gson.toJson(o));
    };


    public static DateTime getCurrentTime() {
        return DateTime.now();
    }

}
