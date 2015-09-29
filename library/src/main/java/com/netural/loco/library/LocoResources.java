package com.netural.loco.library;

import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class LocoResources extends Resources {

    private ResourceIntegrator ri;

    public LocoResources(AssetManager assets, DisplayMetrics metrics, Configuration config, ResourceIntegrator ri) {
        super(assets, metrics, config);
        this.ri = ri;
    }

    @Override
    public CharSequence getText(int id)
            throws Resources.NotFoundException {
        return ri == null ? super.getText(id) : ri.getText(id);
    }

    @Override
    public CharSequence getText(int id, CharSequence def)
            throws Resources.NotFoundException {
        return ri == null ? super.getText(id, def) : ri.getText(id, def);
    }

    @Override
    public CharSequence[] getTextArray(int id)
            throws Resources.NotFoundException {
        return ri == null ? super.getTextArray(id) : ri.getTextArray(id);
    }
}
