package com.ls.skiresort.model.http.okwrapper;

import com.ls.logger.L;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RequestManager {

    private static RequestManager mInstance;
    private static OkHttpClient mClient;

    private RequestManager(@NonNull Context context) {
        mClient = new OkHttpClient();
        mClient.setReadTimeout(1, TimeUnit.SECONDS);
        mClient.setWriteTimeout(1, TimeUnit.SECONDS);
        mClient.setConnectTimeout(1, TimeUnit.SECONDS);

        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        try {
            Cache cache = new Cache(context.getCacheDir(), cacheSize);
            mClient.setCache(cache);
        } catch (IOException e) {
            L.e(e.toString());
        }
    }

    public static synchronized void initializeWith(@NonNull Context context) {
        if (mInstance == null) {
            mInstance = new RequestManager(context);
        }
    }

    public static synchronized OkHttpClient client() {
        if (mInstance == null) {
            throw new IllegalStateException(RequestManager.class.getSimpleName() +
                    " is not initialized, call initialize() method first.");
        }
        return mInstance.getClient();
    }

    private OkHttpClient getClient() {
        return mClient;
    }
}
