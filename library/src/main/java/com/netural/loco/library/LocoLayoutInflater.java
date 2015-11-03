package com.netural.loco.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.xmlpull.v1.XmlPullParser;

public class LocoLayoutInflater extends LayoutInflater {

    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.webkit."
    };

    private final LocoFactory mLocoFactory;
    // Reflection Hax
    private boolean mSetPrivateFactory = false;
    private Field mConstructorArgs = null;

    protected LocoLayoutInflater(Context context) {
        super(context);
        mLocoFactory = new LocoFactory();
        setUpLayoutFactories(false);
    }

    protected LocoLayoutInflater(LayoutInflater original, Context newContext, boolean cloned) {
        super(original, newContext);
        mLocoFactory = new LocoFactory();
        setUpLayoutFactories(cloned);
    }

    @Override
    public LayoutInflater cloneInContext(Context newContext) {
        return new LocoLayoutInflater(this, newContext, true);
    }

    @Override
    protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
        View view = null;
        for (String prefix : sClassPrefixList) {
            try {
                view = createView(name, prefix, attrs);
            } catch (ClassNotFoundException ignored) {
            }
        }
        // In this case we want to let the base class take a crack
        // at it.
        if (view == null) {
            view = super.onCreateView(name, attrs);
        }

        return mLocoFactory.onViewCreated(view, view.getContext(), attrs);
    }

    @Override
    protected View onCreateView(View parent, String name, AttributeSet attrs) throws ClassNotFoundException {
        return mLocoFactory.onViewCreated(
                super.onCreateView(parent, name, attrs),
                getContext(), attrs);
    }

    // ===
    // Wrapping goodies
    // ===

    @Override
    public View inflate(XmlPullParser parser, ViewGroup root, boolean attachToRoot) {
        setPrivateFactoryInternal();
        return super.inflate(parser, root, attachToRoot);
    }

    /**
     * We don't want to unnecessary create/set our factories if there are none there. We try to be
     * as lazy as possible.
     */
    private void setUpLayoutFactories(boolean cloned) {
        if (cloned) {
            return;
        }
        // If we are HC+ we get and set Factory2 otherwise we just wrap Factory1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (getFactory2() != null && !(getFactory2() instanceof WrapperFactory2)) {
                // Sets both Factory/Factory2
                setFactory2(getFactory2());
            }
        }
        // We can do this as setFactory2 is used for both methods.
        if (getFactory() != null && !(getFactory() instanceof WrapperFactory)) {
            setFactory(getFactory());
        }
    }

    @Override
    public void setFactory(Factory factory) {
        // Only set our factory and wrap calls to the Factory trying to be set!
        if (!(factory instanceof WrapperFactory)) {
            super.setFactory(new WrapperFactory(factory, this, mLocoFactory));
        } else {
            super.setFactory(factory);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setFactory2(Factory2 factory2) {
        // Only set our factory and wrap calls to the Factory2 trying to be set!
        if (!(factory2 instanceof WrapperFactory2)) {
//            LayoutInflaterCompat.setFactory(this, new WrapperFactory2(factory2, mCalligraphyFactory));
            super.setFactory2(new WrapperFactory2(factory2, mLocoFactory));
        } else {
            super.setFactory2(factory2);
        }
    }

    private void setPrivateFactoryInternal() {
        // Already tried to set the factory.
        if (mSetPrivateFactory) {
            return;
        }
        // Reflection (Or Old Device) skip.
        boolean reflection = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
        if (!reflection) {
            return;
        }
        // Skip if not attached to an activity.
        if (!(getContext() instanceof Factory2)) {
            mSetPrivateFactory = true;
            return;
        }

        final Method setPrivateFactoryMethod = ReflectionUtils
                .getMethod(LayoutInflater.class, "setPrivateFactory");

        if (setPrivateFactoryMethod != null) {
            ReflectionUtils.invokeMethod(
                    this,
                    setPrivateFactoryMethod,
                    new PrivateWrapperFactory2((Factory2) getContext(), this, mLocoFactory));
        }
        mSetPrivateFactory = true;
    }

    private View createCustomViewInternal(View parent, View view, String name, Context viewContext, AttributeSet attrs) {
        boolean customViewCreation = true;
        if (!customViewCreation) {
            return view;
        }
        if (view == null && name.indexOf('.') > -1) {
            if (mConstructorArgs == null) {
                mConstructorArgs = ReflectionUtils.getField(LayoutInflater.class, "mConstructorArgs");
            }

            final Object[] mConstructorArgsArr = (Object[]) ReflectionUtils.getValue(mConstructorArgs, this);
            final Object lastContext = mConstructorArgsArr[0];
            // The LayoutInflater actually finds out the correct context to use. We just need to set
            // it on the mConstructor for the internal method.
            // Set the constructor ars up for the createView, not sure why we can't pass these in.
            mConstructorArgsArr[0] = viewContext;
            ReflectionUtils.setValue(mConstructorArgs, this, mConstructorArgsArr);
            try {
                view = createView(name, null, attrs);
            } catch (ClassNotFoundException ignored) {
            } finally {
                mConstructorArgsArr[0] = lastContext;
                ReflectionUtils.setValue(mConstructorArgs, this, mConstructorArgsArr);
            }
        }
        return view;
    }

    // ===
    // Wrapper Factories for Pre/Post HC
    // ===

    /**
     * Factory 1 is the first port of call for LayoutInflation
     */
    private static class WrapperFactory implements Factory {

        private final Factory mFactory;
        private final LocoLayoutInflater mInflater;
        private final LocoFactory mLocoFactory;

        public WrapperFactory(Factory factory, LocoLayoutInflater inflater, LocoFactory locoFactory) {
            mFactory = factory;
            mInflater = inflater;
            mLocoFactory = locoFactory;
        }

        @Override
        public View onCreateView(String name, Context context, AttributeSet attrs) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                return mLocoFactory.onViewCreated(
                        mInflater.createCustomViewInternal(
                                null, mFactory.onCreateView(name, context, attrs), name, context, attrs
                        ),
                        context, attrs
                );
            }
            return mLocoFactory.onViewCreated(
                    mFactory.onCreateView(name, context, attrs),
                    context, attrs
            );
        }
    }

    /**
     * Factory 2 is the second port of call for LayoutInflation
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static class WrapperFactory2 implements Factory2 {
        protected final Factory2 mFactory2;
        protected final LocoFactory mLocoFactory;

        public WrapperFactory2(Factory2 factory2, LocoFactory locoFactory) {
            mFactory2 = factory2;
            mLocoFactory = locoFactory;
        }

        @Override
        public View onCreateView(String name, Context context, AttributeSet attrs) {
            return mLocoFactory.onViewCreated(
                    mFactory2.onCreateView(name, context, attrs),
                    context, attrs);
        }

        @Override
        public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
            return mLocoFactory.onViewCreated(
                    mFactory2.onCreateView(parent, name, context, attrs),
                    context, attrs);
        }
    }

    /**
     * Private factory is step three for Activity Inflation, this is what is attached to the
     * Activity on HC+ devices.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static class PrivateWrapperFactory2 extends WrapperFactory2 {

        private final LocoLayoutInflater mInflater;

        public PrivateWrapperFactory2(Factory2 factory2, LocoLayoutInflater inflater, LocoFactory locoFactory) {
            super(factory2, locoFactory);
            mInflater = inflater;
        }

        @Override
        public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
            return mLocoFactory.onViewCreated(
                    mInflater.createCustomViewInternal(
                            parent,
                            mFactory2.onCreateView(parent, name, context, attrs),
                            name, context, attrs
                    ),
                    context, attrs
            );
        }
    }
}
