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
        // call helper method
        return getResourceResult(id, def);
    }

    public CharSequence getText(int id) {
        return getText(id, null);
    }

    public CharSequence[] getTextArray(int id)
            throws Resources.NotFoundException {

        String resourceEntryName = rContext.getResources().getResourceEntryName(id);
        // not supported yet in loco manager, always null
        String[] textArray = LocoManager.getInstance().getTextArray(resourceEntryName, rLanguage);

        if (textArray != null) {
            return textArray;
        } else {
            // try to find in local file only
            return rBase.getTextArray(id);
        }
    }

    /**
     * Handles the right result of a resource id.
     * First searches for loco text. If not found, returns local text (in DEBUG mode returns resourceEntryName).
     * If no text is available, returns resourceEntryName or an alternative text.
     *
     * @param id resourceId
     * @param def alternative text
     * @return normally loco text
     */
    private CharSequence getResourceResult(int id, CharSequence def) {
        String resourceEntryName = rContext.getResources().getResourceEntryName(id);
        String text = LocoManager.getText(resourceEntryName);

        if (text != null && !text.trim().isEmpty()) {
            // text or not empty text
            return text;
        } else {
            if (BuildConfig.DEBUG) {
                // no return resourceEntryName immediately
                return resourceEntryName;
            } else {
                try {
                    // try to find text in local resources, e.g. res/values/strings.xml
                    return rBase.getText(id);
                } catch (Resources.NotFoundException e) {
                    // no resource found

                    // check if alternative text is available
                    return def != null ? def : resourceEntryName;
                }
            }
        }
    }
}
