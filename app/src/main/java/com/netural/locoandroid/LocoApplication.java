package com.netural.locoandroid;

import android.app.Application;

import com.netural.loco.library.LocoConfig;
import com.netural.loco.library.LocoManager;

import java.util.Locale;

public class LocoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        LocoConfig.initDefault(
                new LocoConfig.Builder()
                        .setBaseUrl("https://localise.biz:443/api/") // https://localise.biz:443/api/ https://cacheproxy.netural.com/
                        .setLocalesUrl("locales")
                        .setRefreshTime(-1) // no delay
                        .setAdditionalParameters(null)
                        .setLocale(Locale.getDefault())
                        .setPath(getFilesDir().getAbsolutePath())
                        .setApiKey(BuildConfig.LOCO_KEY).build());
        LocoManager.getInstance().init();
    }
}
