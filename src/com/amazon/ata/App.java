package com.amazon.ata;

import com.amazon.ata.advertising.service.future.ThreadState;
import com.amazon.ata.advertising.service.future.ThreadUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Arrays;

import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

public class App {

    public static final GsonBuilder builder = new GsonBuilder();
    static final Logger LOG = LogManager.getLogger(App.class);
    private static final String CYAN = "\u001B[36m";
    private static final String RESET = "\u001B[0m";
    private static final Gson gson = builder.setPrettyPrinting().create();
    public static final Consumer<String> print = System.out::println;

    public static final Consumer<Object> toJson = o -> {
        String[] lines = gson.toJson(o).split("\n");

        Arrays.stream(lines).map(line -> new StringBuilder().append(CYAN).append(line).append(CYAN).append(RESET))
                .forEach(System.out::println);
    };

    public static DateTime getCurrentTime() {
        return DateTime.now();
    }

}
