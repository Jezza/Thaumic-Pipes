package me.jezza.thaumicpipes.common.core;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.relauncher.Side;

public class TPLogger {

    // Take on the appearance of FML's logger, because why not?
    public static TPLogger log = new TPLogger();
    static Side side;

    private Logger tpLogger;

    public TPLogger() {
    }

    public static void init() {
        log.tpLogger = LogManager.getLogger("TP");
    }

    public static void log(Level logLevel, Object object) {
        log.tpLogger.log(logLevel, String.valueOf(object));
    }

    public static void info(Object object) {
        log(Level.INFO, "[INFO] " + object);
    }

    public static void debug(Object object) {
        log(Level.WARN, "[DEBUG] " + object);
    }

    public static void severe(Object object) {
        log(Level.FATAL, object);
    }

    public static Logger getLogger() {
        return log.tpLogger;
    }
}
