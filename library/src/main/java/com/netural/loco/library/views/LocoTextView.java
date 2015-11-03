package com.netural.loco.library.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import com.netural.loco.library.LocoContextWrapper;

public class LocoTextView extends TextView {

    public LocoTextView(Context context) {
        super(LocoContextWrapper.wrap(context));
        init(getContext(), null);
    }

    public LocoTextView(Context context, AttributeSet attrs) {
        super(LocoContextWrapper.wrap(context), attrs);
        init(getContext(), attrs);
    }

    public LocoTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(LocoContextWrapper.wrap(context), attrs, defStyleAttr);
        init(getContext(), attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LocoTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(LocoContextWrapper.wrap(context), attrs, defStyleAttr, defStyleRes);
        init(getContext(), attrs);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
    }

    private void init(Context context, AttributeSet attrs) {
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
        setText(context.getResources().getString(resourceId));
    }
}
