package com.netural.loco.library;

import java.util.List;
import java.util.Locale;

public class LocoConfig {

    private static LocoConfig sInstance;

    private String mPath;
    private String mLanguage;
    private Locale mLocale;
    private long mRefreshTime;
    private String mApiKey;
    private String mBaseUrl;
    private String mLocalesUrl;
    private List<String> mParams;

    protected LocoConfig(Builder builder) {
        mLanguage = builder.language;
        mPath = builder.path;
        mLocale = builder.locale;
        mRefreshTime = builder.refreshTime;
        mApiKey = builder.apiKey;
        mBaseUrl = builder.baseUrl;
        mLocalesUrl = builder.localesUrl;
        mParams = builder.params;
    }

    public static void initDefault(LocoConfig locoConfig) {
        sInstance = locoConfig;
    }

    public static LocoConfig get() {
        if (sInstance == null) {
            sInstance = new LocoConfig(new Builder());
        }
        return sInstance;
    }

    public Locale getLocale() {
        return mLocale;
    }

    public long getRefreshTime() {
        return mRefreshTime;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public String getPath() {
        return mPath;
    }

    public String getApiKey() {
        return mApiKey;
    }

    public String getBaseUrl() {
        return mBaseUrl;
    }

    public String getLocalesUrl() {
        return mLocalesUrl;
    }



    public static class Builder {

        private String path = null;
        private String language = Locale.getDefault().getLanguage();
        private Locale locale = Locale.getDefault();
        private long refreshTime = 3600000; // 1 hour
        private String apiKey = null;
        private String baseUrl = null;
        private String localesUrl = null;
        private List<String> params = null;

        /**
         * The use of this method is not recommended, use @setLocale instead
         *
         * @param language
         * @return
         */
        public Builder setLanguage(String language) {
            this.language = language;
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                this.locale = Locale.forLanguageTag(language);
            }
            return this;
        }

        public Builder setLocale(Locale locale) {
            this.locale = locale;
            this.language = locale.getLanguage();
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public Builder setRefreshTime(long refreshTimeInMillis) {
            this.refreshTime = refreshTimeInMillis;
            return this;
        }

        public Builder setApiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder setAdditionalParameters(List<String> params) {
            this.params = params;
            return this;
        }

        public Builder setLocalesUrl(String localesUrl) {
            this.localesUrl = localesUrl;
            return this;
        }

        public LocoConfig build() {
            return new LocoConfig(this);
        }


    }
}
