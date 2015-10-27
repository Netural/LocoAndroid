package com.netural.loco.library;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;

public class ResourceIntegrator {

    private Context rContext;
    private Resources rBase;
    private String rLanguage;

    public ResourceIntegrator(Context context, String language)
            throws PackageManager.NameNotFoundException {
        super();
        rContext = context;
        rBase = context.getResources();
        rLanguage = language;
    }

    public CharSequence getText(int id, CharSequence def) {
        String text = LocoManager.getText(rContext.getResources().getResourceEntryName(id));
        return text == null ? rBase.getText(id) : text;
    }

    public CharSequence getText(int id)
            throws Resources.NotFoundException {
        String text = LocoManager.getText(rContext.getResources().getResourceEntryName(id));
        return text == null ? rBase.getText(id) : text;
    }

    public CharSequence[] getTextArray(int id)
            throws Resources.NotFoundException {
        String[] textArray = LocoManager.getInstance().getTextArray(rContext.getResources().getResourceEntryName(id), rLanguage);
        return textArray == null ? rBase.getTextArray(id) : textArray;
    }
}
