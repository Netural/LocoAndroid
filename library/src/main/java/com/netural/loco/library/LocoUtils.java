package com.netural.loco.library;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LocoUtils {

    public static void reloadTextViews(ViewGroup parent) {
        Context context = parent.getContext();
        if (!(context instanceof LocoContextWrapper)) {
            context = LocoContextWrapper.wrap(context);
        }
        reloadTextViews(parent, context);
    }

    private static void reloadTextViews(ViewGroup parent, Context context) {
        for (int i = parent.getChildCount() - 1; i >= 0; i--) {
            final View child = parent.getChildAt(i);
            if (child instanceof ViewGroup) {
                reloadTextViews((ViewGroup) child, context);
            } else {
                if (child != null) {
                    if (child instanceof TextView) {
                        Object resourceId = child.getTag(R.id.loco_text_id);
                        if (resourceId != null) {
                            ((TextView) child).setText(context.getText((Integer) resourceId));
                        }
                    }
                }
            }
        }
    }
}
