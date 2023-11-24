package ru.hbb.Console.Logger;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleLogger {

    public static final Logger logger = Logger.getLogger(SimpleLogger.class.getName());

    public static void log(String message) {
        logger.log(Level.INFO, message);
    }

    public static void error(String message) {
        logger.warning(message);
    }

}
