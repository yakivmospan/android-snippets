package com.ls.skiresort.model.http.okwrapper;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * Created by Yakiv M. on 09.02.2015.
 */

public abstract class ConvertMethod<S> {

    @Nullable
    protected abstract S convertResult(@NonNull ResponseBody body);

    protected final ServerResponse<S> convert(@Nullable Response response) {
        ServerResponse<S> result = new ServerResponse<>();
        if (response != null) {
            result.setCode(response.code());
            result.setSuccessufl(response.isSuccessful());
            result.setHeaders(response.headers());
            result.setSuccessResult(convertResult(response.body()));
            result.setWrapedResponse(response);
        }

        return result;
    }

    protected final ServerResponse<S> convert(@Nullable Request request, IOException e) {
        ServerResponse<S> result = new ServerResponse<>();
        if (request != null) {
            result.setSuccessufl(false);
            result.setHeaders(request.headers());
            result.setException(e);
            result.setWrapedRequest(request);
        }
        return result;
    }

}
