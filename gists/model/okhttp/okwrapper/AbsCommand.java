import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import android.support.annotation.NonNull;

/**
 * Created by Yakiv M. on 11.02.2015.
 */
public interface AbsCommand<S> {
    public
    @NonNull
    OkHttpClient createClient();

    public
    @NonNull
    Request createRequest();

    public
    @NonNull
    ConvertMethod<S> createConvertMethod();

    public
    @NonNull
    void onResponse(ServerResponse<S> response);
}
