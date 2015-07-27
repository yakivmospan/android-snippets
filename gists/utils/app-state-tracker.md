```java
import java.util.ArrayList;

/**
 * Tracks whenever application goes into background and foreground state.
 *
 * <br/> <br/> Add {@link OnStateChangeListener} and call {@code onActivityStart(),
 * onActivityStop()} and {@code onActivityCreate()} in each of your Activities. Best practice for
 * API < 14 is to create BaseActivity and do it there. In API >= 14 you can register {@link
 * android.app.Application.ActivityLifecycleCallbacks} in {@link android.app.Application} and match
 * lifecycle call there.
 */
public class AppStateTracker {

    private static AppStateTracker sInstance;

    private ArrayList<OnStateChangeListener> mListeners = new ArrayList<>();

    private boolean mIsInForeground;
    private int mForegroundScreensCount;

    private AppStateTracker() {
    }

    public static AppStateTracker getInstance() {
        if (sInstance == null) {
            synchronized (AppStateTracker.class) {
                if (sInstance == null) {
                    sInstance = new AppStateTracker();
                }
            }
        }
        return sInstance;
    }

    public void onActivityStart() {
        mForegroundScreensCount++;
        checkStateChange();
    }

    public void onActivityStop() {
        mForegroundScreensCount--;
        checkStateChange();
    }

    private void checkStateChange() {
        if (mForegroundScreensCount > 0 && !mIsInForeground) {
            // app goes into foreground
            for (OnStateChangeListener listener : mListeners) {
                mIsInForeground = true;
                listener.onForeground();
            }
        } else if (mForegroundScreensCount == 0 && mIsInForeground) {
            // app goes into background
            for (OnStateChangeListener listener : mListeners) {
                mIsInForeground = false;
                listener.onBackground();
            }
        }
    }

    public void addOnStateChangeListener(OnStateChangeListener listener) {
        mListeners.add(listener);
    }

    public void removeOnStateChangeListener(OnStateChangeListener listener) {
        mListeners.remove(listener);
    }

    public void clearOnStateChangeListeners() {
        mListeners.clear();
    }

    public interface OnStateChangeListener {
        void onForeground();
        void onBackground();
    }

}
```
