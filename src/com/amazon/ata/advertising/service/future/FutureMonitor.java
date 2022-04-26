package com.amazon.ata.advertising.service.future;

import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class FutureMonitor {
    public static final Logger LOG = LogManager.getLogger(FutureMonitor.class);
    private static final String DEFAULT = "\u001B[0m";
    private static final Consumer<String> magenta = s -> System.out.println("\u001b[35m" + s + DEFAULT);
    private static final Consumer<String> yellow = s -> System.out.println("\u001b[33m" + s + DEFAULT);
    private static final Consumer<String> green = s -> System.out.println("\u001b[32m" + s + DEFAULT);
    private static final Consumer<String> cyan = s -> System.out.println("\u001B[36m" + s + DEFAULT);
    private static final Consumer<String> red = s -> System.out.println("\u001b[31m" + s + DEFAULT);

    private FutureMonitor() {}

    public enum ConsoleLogger {
        MAGENTA(magenta), YELLOW(yellow), GREEN(green), CYAN(cyan), RED(red);
        private final Consumer<String> color;
        ConsoleLogger(Consumer<String> color) {
            this.color = color;
        }
        public Consumer<String> getColor() {
            return color;
        }
        public void log(String message) {
            color.accept(message);
        }
    }
}
