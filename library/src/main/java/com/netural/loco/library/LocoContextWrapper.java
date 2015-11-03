package com.netural.loco.library;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;

import java.util.Locale;

public class LocoContextWrapper extends ContextWrapper {

    private LocoResources locoResources;
    private LayoutInflater mInflater;

    private LocoContextWrapper(Context base) {
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

    public static ContextWrapper wrap(Context base) {
        return new LocoContextWrapper(base);
    }

    @Override
    public Resources getResources() {
        return locoResources == null ? super.getResources() : locoResources;
    }

    @Override
    public Object getSystemService(String name) {
        if (LAYOUT_INFLATER_SERVICE.equals(name)) {
            if (mInflater == null) {
                mInflater = new LocoLayoutInflater(LayoutInflater.from(getBaseContext()), this, false);
            }
            return mInflater;
        }
        return super.getSystemService(name);
    }
}
