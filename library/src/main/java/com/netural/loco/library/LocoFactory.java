package com.netural.loco.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

class LocoFactory {

    public View onViewCreated(View view, Context context, AttributeSet attrs) {
        if (view != null && view.getTag(R.id.loco_tag_id) != Boolean.TRUE) {
            onViewCreatedInternal(view, context, attrs);
            view.setTag(R.id.loco_tag_id, Boolean.TRUE);
        }
        return view;
    }

    void onViewCreatedInternal(View view, final Context context, AttributeSet attrs) {
        if (view instanceof TextView) {
            if (attrs == null) {
                return;
            }
            int[] set = {
                    android.R.attr.text
            };
            TypedArray a = context.obtainStyledAttributes(attrs, set);
            int resourceId = a.getResourceId(0, 0);
            a.recycle();
            if (resourceId == 0) {
                return;
            }
            ((TextView) view).setText(context.getResources().getString(resourceId));
        }
    }
}
