package com.ls.skiresort.model.http.okwrapper;

import android.support.annotation.NonNull;

/**
 * Created by Yakiv M. on 11.02.2015.
 */
public interface RequestCallback<S> {

    public void onResponse(@NonNull ServerResponse<S> response);
}
