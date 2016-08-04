package com.netural.loco.library;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.netural.loco.base.LocoInfo;
import com.netural.loco.base.LocoLibrary;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class LocoManager {
    private static final String TAG = LocoManager.class.getSimpleName();

    private static LocoManager ourInstance = new LocoManager();
    private LocoConfig locoConfig;
    private LocoLibrary locoLibrary;
    private LocoInfo locoInfo;

    private HashMap<String, String> language = new HashMap<>();

    private LocoManager() {
    }

    public static LocoManager getInstance() {
        return ourInstance;
    }

    public static String getText(Context context, int id) {
        return ourInstance.getTextIntern(context, id);
    }

    public static String getText(String id) {
        return ourInstance.getTextIntern(id);
    }

    public void init() {
        init(LocoConfig.get());
    }

    public void init(LocoConfig locoConfig) {
        this.locoConfig = locoConfig;

        this.locoLibrary = new LocoLibrary(locoConfig.getApiKey(), locoConfig.getBaseUrl(), locoConfig.getLocalesUrl());

        File languageFile = locoLibrary.getLanguageFile(locoConfig.getPath(), locoConfig.getLanguage());
        boolean setLanguage = true;
        if (languageFile != null) {
            setLanguage = false;
        }

        language = locoLibrary.getLanguageFromFile(locoConfig.getPath(), locoConfig.getLanguage());

        new InitLocoTask(locoConfig.getPath(), locoConfig.getLanguage(), setLanguage, null).execute();
    }

    public void reload(OnLanguageLoadedListener listener) {
        new InitLocoTask(locoConfig.getPath(), locoConfig.getLanguage(), true, listener).execute();
    }

    public LocoInfo getInfo() {
        if (locoInfo == null) {
            locoInfo = locoLibrary.getInfo(locoConfig.getPath());
        }
        return locoInfo;
    }

    private String getTextIntern(String id) {
        Log.v(TAG, "get String for id " + id);
        return language.containsKey(id) ? language.get(id) : null;
    }

    private String getTextIntern(Context context, int id) {
        String textId = context.getResources().getResourceEntryName(id);
        Log.v(TAG, "get String for id " + textId);
        return language.containsKey(textId) ? language.get(textId) : null;
    }

    public String[] getTextArray(String id, String languageStr) {
        // TODO: not supported in loco yet
        Log.v(TAG, "not supported - get StringArray for id " + id);
        return null;
    }

    public interface OnLanguageLoadedListener {
        void onLanguageLoaded();
    }

    private class InitLocoTask extends AsyncTask<Void, Integer, HashMap<String, String>> {

        private String path;
        private String locale;
        private boolean setLanguage;
        private OnLanguageLoadedListener listener;

        public InitLocoTask(String path, String locale, boolean setLanguage, OnLanguageLoadedListener listener) {
            this.path = path;
            this.locale = locale;
            this.setLanguage = setLanguage;
            this.listener = listener;
        }

        protected HashMap<String, String> doInBackground(Void... voids) {

            LocoInfo info = getInfo();
            if (info != null && ((info.lastUpdate.getTime() + locoConfig.getRefreshTime()) > System.currentTimeMillis())) {
                Log.i(TAG, "language file is up to date");
                return null;
            }
            try {
                locoLibrary.loadUnzipAndSaveAll(path);
            } catch (IOException e) {
                Log.e(TAG, "could not load language zip file", e);
                return null;
            }

            return locoLibrary.getLanguageFromFile(path, locale);
        }

        @Override
        protected void onPostExecute(HashMap<String, String> result) {
            if (setLanguage && result != null) {
                language = result;
                locoInfo = null;
            }
            if (listener != null && result != null) {
                listener.onLanguageLoaded();
            }
        }
    }
}
