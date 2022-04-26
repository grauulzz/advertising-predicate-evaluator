package com.amazon.ata;

import java.util.function.Consumer;

public enum ConsoleColors {
    CYAN("\u001B[36m"),
    GREEN("\u001b[32m"),
    RED("\u001b[31m"),
    YELLOW("\u001b[33m"),
    BLUE("\u001b[34m"),
    MAGENTA("\u001b[35m"),
    WHITE("\u001b[37m"),
    BLACK("\u001b[30m"),
    RESET("\u001B[0m");
    ConsoleColors(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
    private final String value;
    public static final Consumer<String> pY = s -> System.out.println(YELLOW.getValue() + s + RESET.getValue());
    public static final Consumer<String> pG = s -> System.out.println(GREEN.getValue() + s + RESET.getValue());
    public static final Consumer<String> pB = s -> System.out.println(BLUE.getValue() + s + RESET.getValue());
    public static final Consumer<String> pR = s -> System.out.println(RED.getValue() + s + RESET.getValue());
    public static final Consumer<String> pC = s -> System.out.println(CYAN.getValue() + s + RESET.getValue());
    public static final Consumer<String> pM = s -> System.out.println(MAGENTA.getValue() + s + RESET.getValue());


}
