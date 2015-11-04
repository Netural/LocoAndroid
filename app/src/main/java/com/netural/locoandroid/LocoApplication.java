package com.netural.locoandroid;

import android.app.Application;

import com.netural.loco.library.LocoManager;

import java.util.Locale;

public class LocoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        LocoManager locoManager = LocoManager.getInstance();
        locoManager.init(this, BuildConfig.LOCO_KEY, Locale.getDefault());
    }
}
