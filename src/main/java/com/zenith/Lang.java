package com.zenith;

import java.io.File;
import java.nio.file.Files;

public class Lang {
    private static volatile String lang = "en";

    static {
        reload();
    }

    public static void reload() {
        // 1. JVM system property set by launcher (-Dgangs.lang=es)
        String sysProp = System.getProperty("gangs.lang");
        if ("es".equals(sysProp) || "en".equals(sysProp)) {
            lang = sysProp;
            return;
        }
        // 2. Fallback: lang.json file
        try {
            File f = new File("lang.json");
            if (f.exists()) {
                String content = Files.readString(f.toPath());
                if (content.contains("\"lang\":\"es\"")) {
                    lang = "es";
                } else {
                    lang = "en";
                }
            }
        } catch (Exception ignored) {}
    }

    public static String t(String es, String en) {
        return "es".equals(lang) ? es : en;
    }

    public static boolean isEs() {
        return "es".equals(lang);
    }

    public static String get() {
        return lang;
    }
}
