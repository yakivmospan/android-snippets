import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Yakiv M. on 09.02.2015.
 */
public class JsonParser<R> implements ParseMethod<InputStream, R> {

    private final JsonHandler<R> mHandler;

    public JsonParser(@NonNull JsonHandler<R> handler) {
        mHandler = handler;
    }

    @Nullable
    @Override
    public R parse(@Nullable InputStream source) {
        if(source == null){
            return null;
        }

        String js = convertStreamToString(source);
        if (js != null) {
            mHandler.parse(js);
        }

        return mHandler.getResult();
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder(256);

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
