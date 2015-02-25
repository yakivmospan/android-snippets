import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Yakiv M. on 10.02.2015.
 */
public class Utils {

    public static
    @Nullable
    InputStream openStream(@NonNull Context context, String path) {
        try {
            AssetManager assetManager = context.getAssets();
            return assetManager.open(path);
        } catch (IOException e) {
            L.e(e.getMessage());
            return null;
        }
    }
}