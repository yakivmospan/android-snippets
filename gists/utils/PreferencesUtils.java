import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.util.Set;

/**
 * Helper class for quick access Shared Preferences of application.
 * <p/>
 * Note. You should call {@link #initialize(Context)} before using this class.
 */
public class PreferencesUtils {

    private static Context sContext;
    private String mFile;

    /**
     * @param file Preferences file name, that will be used to retrieve and save data.
     */
    public PreferencesUtils(@NonNull String file) {
        mFile = file;
    }

    /**
     * Must be called before any other usages of this class.
     *
     * @param context The context to open preferences with.
     */
    public static void initialize(@NonNull Context context) {
        sContext = context;
    }

    public String getString(String key, String defaultValue) {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(mFile, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }

    public void setString(String key, String value) {
        setString(mFile, key, value);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(mFile, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public void setBoolean(String key, boolean value) {
        setBoolean(mFile, key, value);
    }

    public int getInt(String key, int defaultValue) {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(mFile, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultValue);
    }

    public void setInt(String key, int value) {
        setInt(mFile, key, value);
    }

    public long getLong(String key, long defaultValue) {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(mFile, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, defaultValue);
    }

    public void setLong(String key, long value) {
        setLong(mFile, key, value);
    }

    public Set<String> getStringSet(String key, Set<String> defaultValue) {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(mFile, Context.MODE_PRIVATE);
        return sharedPreferences.getStringSet(key, defaultValue);
    }

    public void setStringSet(String key, Set<String> value) {
        setStringSet(mFile, key, value);
    }

    /**
     * Clear all in default preferences file.
     */
    public void clear() {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(mFile, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    /**
     * Remove keys in default preferences file.
     */
    public void remove(String... keys) {
        removeFrom(mFile, keys);
    }

    /**
     * @return {@link SharedPreferences.Editor} of default preferences file.
     */
    public SharedPreferences.Editor getEditor() {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(mFile, Context.MODE_PRIVATE);
        return sharedPreferences.edit();
    }

    public static String getString(String file, String key, String defaultValue) {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(file, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }

    public static void setString(String file, String key, String value) {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(file, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        edit.apply();
    }

    public static boolean getBoolean(String file, String key, boolean defaultValue) {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(file, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public static void setBoolean(String file, String key, boolean value) {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(file, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(key, value);
        edit.apply();
    }

    public static int getInt(String file, String key, int defaultValue) {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(file, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static void setInt(String file, String key, int value) {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(file, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt(key, value);
        edit.apply();
    }

    public static long getLong(String file, String key, long defaultValue) {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(file, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, defaultValue);
    }

    public static void setLong(String file, String key, long value) {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(file, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putLong(key, value);
        edit.apply();
    }

    public static Set<String> getStringSet(String file, String key, Set<String> defaultValue) {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(file, Context.MODE_PRIVATE);
        return sharedPreferences.getStringSet(key, defaultValue);
    }

    public static void setStringSet(String file, String key, Set<String> value) {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(file, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putStringSet(key, value);
        edit.apply();
    }

    /**
     * Clear all in passed preferences file.
     */
    public static void clear(String file) {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(file, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    /**
     * Remove keys in passed preferences file.
     */
    public static void removeFrom(String file, String... keys) {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(file, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        for (String key : keys) {
            edit.remove(key);
        }
        edit.apply();
    }

    /**
     * @return {@link SharedPreferences.Editor} of passed preferences file.
     */
    public static SharedPreferences.Editor getEditor(String file) {
        SharedPreferences sharedPreferences = sContext.getSharedPreferences(file, Context.MODE_PRIVATE);
        return sharedPreferences.edit();
    }
}
