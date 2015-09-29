package com.netural.loco.library;

public class LocoManager {
    private static LocoManager ourInstance = new LocoManager();

    private LocoManager() {
    }

    public static LocoManager getInstance() {
        return ourInstance;
    }

    public String getText(String id, String language) {
        return "set string for " + id + " and language " + language;
//        return null;
    }

    public String[] getTextArray(String id, String language) {
        return null;
    }
}
