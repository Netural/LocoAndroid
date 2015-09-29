package com.netural.loco.library;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LocoResourceContext extends ContextWrapper {

    private LocoResources locoResources;

    public LocoResourceContext(Context base) {
        super(base);

        ResourceIntegrator ri = null;
        try {
            ri = new ResourceIntegrator(base, Locale.getDefault().getLanguage());

            DisplayMetrics displayMetrics = base.getResources().getDisplayMetrics();

            locoResources = new LocoResources(
                    base.getAssets(), displayMetrics,
                    base.getResources().getConfiguration(), ri);

        } catch (PackageManager.NameNotFoundException e) {
            locoResources = null;
        }
    }

    @Override
    public Resources getResources() {
        return locoResources == null ? super.getResources() : locoResources;
    }
}
