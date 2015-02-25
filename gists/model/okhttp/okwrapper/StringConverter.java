package com.ls.skiresort.model.http.okwrapper;

import com.ls.logger.L;
import com.ls.skiresort.model.util.JsonHandler;
import com.squareup.okhttp.ResponseBody;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Yakiv M. on 09.02.2015.
 */
public class StringConverter extends ConvertMethod<String> {
    @Override
    @Nullable
    protected String convertResult(@NonNull ResponseBody body) {
        try {
            return body.string();
        } catch (IOException e) {
            L.e(e.getMessage());
        }
        return null;
    }
}
