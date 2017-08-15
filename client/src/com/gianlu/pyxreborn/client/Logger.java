package com.gianlu.pyxreborn.client;

import com.gianlu.pyxreborn.Utils;
import org.fusesource.jansi.Ansi;

import static org.fusesource.jansi.Ansi.ansi;

public class Logger {
    private static boolean enabled = true;

    public static void info(String str) {
        if (!enabled) return;
        System.out.println(ansi().fg(Ansi.Color.GREEN).bg(Ansi.Color.WHITE).a(str));
    }

    public static void severe(Throwable ex) {
        System.out.println(ansi().bg(Ansi.Color.RED).a(Utils.printStackTrace(ex)));
    }

    /**
     * Log level severe will be always enabled
     */
    public static void setEnabled(boolean enabled) {
        Logger.enabled = enabled;
    }
}
