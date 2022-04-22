package com.amazon.ata;

import java.util.Arrays;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum ConsoleColors {
    CYAN("\u001B[36m"), GREEN("\u001b[32m"), RED("\u001b[31m"), YELLOW("\u001b[33m"), BLUE("\u001b[34m"), MAGENTA("\u001b[35m"), WHITE("\u001b[37m"), BLACK("\u001b[30m"), RESET("\u001B[0m");
    private final String value;

    public String getValue() {
        return value;
    }

    ConsoleColors(String value) {
        this.value = value;
    }

    public static final Consumer<String> pY = s -> System.out.println(YELLOW.getValue() + s + RESET.getValue());
    public static final Consumer<String> pG = s -> System.out.println(GREEN.getValue() + s + RESET.getValue());
    public static final Consumer<String> pB = s -> System.out.println(BLUE.getValue() + s + RESET.getValue());
    public static final Consumer<String> pR = s -> System.out.println(RED.getValue() + s + RESET.getValue());
    public static final Consumer<String> pW = s -> System.out.println(WHITE.getValue() + s + RESET.getValue());
    public static final Consumer<String> pC = s -> System.out.println(CYAN.getValue() + s + RESET.getValue());
    public static final Consumer<String> pM = s -> System.out.println(MAGENTA.getValue() + s + RESET.getValue());
    public static final BiConsumer<String, String> biPYB = (s, s2) -> System.out.println(YELLOW.getValue() + s + " " + BLUE.getValue() + s2 + RESET.getValue());
    public static final Function<String, Consumer<String>> pYBFunc = s1 -> s2 -> {
        pY.accept(s1);
        pB.accept(s2);
    };

    public static final BiFunction<String, String, Consumer<String>> biPYF = (s1, s2) -> {
        pY.accept(s1);
        pB.accept(s2);
        return pY;
    };

    public static final BiFunction<String, String, Consumer<String>> triPYF = (s1, s2) -> {
        pY.accept(s1);
        pB.accept(s2);
        return pY;
    };
}