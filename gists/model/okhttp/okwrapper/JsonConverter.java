import com.squareup.okhttp.ResponseBody;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * Created by Yakiv M. on 09.02.2015.
 */
public class JsonConverter<S> extends ConvertMethod<S> {

    private final JsonHandler<S> mHandler;

    public JsonConverter(@NonNull JsonHandler<S> handler) {
        mHandler = handler;
    }

    @Override
    @Nullable
    protected S convertResult(@NonNull ResponseBody body) {
        try {
            String js = body.string();
            if (js != null) {
                mHandler.parse(js);
            }
        } catch (IOException e) {
            L.e(e.getMessage());
        }
        return mHandler.getResult();
    }
}
