### Model: Async data loader

Snippet shows simple implementation of download manager that :

- Load data asynchronously
- Cancel all request if some error occurs 
- Wait till all data will be downloaded
- Save data if it was downloaded successfully

#### When to use it? 
If you need to download some set of data that can be loaded asynchronously. And save it if all requests was successful

#### Sources
```java
public class DownloadManager {

    private AtomicInteger mQueuedRequests = new AtomicInteger();

    private Context mContext;
    private Callback mCallback;

    private Object mFirstResult;
    private List<Objects> mSecondResult;
    private List<Objects> mThirdResult;

    public void startLoading(@NotNull Context context, @NotNull Callback callback) {
        mContext = context;
        mCallback = callback;

        loadData();
    }

    private void loadData() {
        loadFirstData();
        loadSecondData();
        //.. load all rest of data
    }

    private void loadFirstData() {
        mQueuedRequests.incrementAndGet();
        HttpFactory.createFirstRequest(
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        onFirstDataLoaded(jsonObject);
                        checkLoadedData();
                    }
                }, errorListener
        );
    }

    private void onFirstDataLoaded(JSONObject jsonObject) {
        mFirstResult = parseFirstData(jsonObject);
    }

    private void loadSecondData() {
        mQueuedRequests.incrementAndGet();
        HttpFactory.createSecondRequest(
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        onSecondDataLoaded(jsonObject);
                        checkLoadedData();
                    }
                }, errorListener
        );
    }

    private void onSecondDataLoaded(JSONObject jsonObject) {
        mSecondResult = parseSecondData(jsonObject);
    }

    private void checkLoadedData() {
        if (mQueuedRequests.decrementAndGet() == 0) {
            saveData();
        }
    }

    private void saveData() {
        if (mContext == null) {
            onUnexpectedBehavior("Context is null in saveData()");
        } else if (isValidData()) {

            //save data here

            onDownloadSuccess();
        } else {
            onDownloadError();
        }
    }

    private boolean isValidData() {
        return mFirstResult != null
                && mSecondResult != null
                && mThirdResult != null;
    }

    private void onDownloadSuccess() {
        if (mCallback != null) {
            mCallback.onDownloadSuccess();
        }
    }

    private void onDownloadError() {
        if (mCallback != null) {
            mCallback.onDownloadError();
        }
    }

    private void onUnexpectedBehavior(String reason) {
        if (mCallback != null) {
            mCallback.onUnexpectedBehavior(reason);
        }
    }

    private boolean mIsCanceled;
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            L.e(volleyError.getMessage());
            if (!mIsCanceled) {
                mIsCanceled = true;
                onDownloadError();
                RequestManager.queue().cancelAll(new RequestQueue.RequestFilter() {
                    @Override
                    public boolean apply(Request<?> request) {
                        return true;
                    }
                });
            }
        }
    };

    public static abstract class Callback {

        abstract public void onDownloadSuccess();

        abstract public void onDownloadError();

        public void onUnexpectedBehavior(String reason) {
            L.e(reason);
        }
    }
}
```
