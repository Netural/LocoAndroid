package com.netural.loco.library;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.netural.loco.base.LocoInfo;
import com.netural.loco.base.LocoLibrary;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

public class LocoManager {
    private static final String TAG = LocoManager.class.getSimpleName();

    private static LocoManager ourInstance = new LocoManager();
    private Context context;
    private String path;
    private LocoLibrary locoLibrary;

    private HashMap<String, String> language = new HashMap<>();

    private LocoManager() {
    }

    public static LocoManager getInstance() {
        return ourInstance;
    }

    public static String getText(int id) {
        return ourInstance.getTextIntern(id);
    }

    public static String getText(String id) {
        return ourInstance.getTextIntern(id);
    }

    public void init(final Context context, String key, final Locale locale) {
        init(context, key, locale.getLanguage());
    }

    public void init(final Context context, String key, final String locale) {
        this.locoLibrary = new LocoLibrary(key);
        this.context = context;
        this.path = context.getFilesDir().getAbsolutePath();

        File languageFile = locoLibrary.getLanguageFile(path, locale);
        boolean setLanguage = true;
        if (languageFile != null) {
            setLanguage = false;
        }

        language = locoLibrary.getLanguage(path, locale);

        new InitLocoTask(path, locale, setLanguage, null).execute();
    }

    public void reload(Locale locale, OnLanguageLoadedListener listener) {
        reload(locale.getLanguage(), listener);
    }

    public void reload(String locale, OnLanguageLoadedListener listener) {
        new InitLocoTask(path, locale, true, listener).execute();
    }

    public LocoInfo getInfo() {
        return locoLibrary.getInfo(path);
    }

    private String getTextIntern(String id) {
        Log.v(TAG, "get String for id " + id);
        return language.containsKey(id) ? language.get(id) : id;
    }

    private String getTextIntern(int id) {
        String textId = context.getResources().getResourceEntryName(id);
        Log.v(TAG, "get String for id " + textId);
        return language.containsKey(textId) ? language.get(textId) : textId;
    }

    public String[] getTextArray(String id, String language) {
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

            try {
                locoLibrary.loadUnzipAndSaveAll(path);
            } catch (IOException e) {
                Log.e(TAG, "could not load language zip file", e);
            }

            return locoLibrary.getLanguage(path, locale);
        }

        @Override
        protected void onPostExecute(HashMap<String, String> result) {
            if (setLanguage) {
                language = result;
            }
            if (listener != null) {
                listener.onLanguageLoaded();
            }
        }
    }
}
