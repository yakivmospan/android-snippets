import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Yakiv M. on 10.02.2015.
 */
public interface ParseMethod<S, R> {

    @Nullable
    public abstract R parse(@NonNull S source);
}
