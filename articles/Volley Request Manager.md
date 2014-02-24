## Volley Request Manager

On mine 3 years of developing practice every second project had feature, like Http Client or Image Loader, that can be easily done with [Volley][1]. Thats why I decided to develop some sort of model that will provide:

 - Easy and reusable interface
 - Possibility to use different queues
 - Background and volley default queues implementations
 - Possibility to create your own queues
 - Factory that will help to create your own queues
 - Callback that handle result in background and deliver result in UI thread
 - Possibility to use default Volley Listeners
 - Load Images with different Image Loaders
 - Factory that will help to create your own Image Loader
 - Possibility to clear Image Loader memory cache

### Queue

Look on this great and easy to understand article from [Dmytro Danylyk][2] - [Volley Part 2 - Application Model][3]. As he said if you want to control your request and queues from different parts of application, you need to use [Singleton][4].

You can create it like this :

```java
public class RequestManager {

    private static RequestManager instance;
    private RequestQueue mRequestQueue;

    private RequestManager(Context context) {
        mRequestQueue = new Volley().newRequestQueue(context.getApplicationContext());
    }

    public static synchronized RequestQueueBuilder getInstance(Context context) {
        if (instance == null) {
            instance = new RequestManager(context);
        }

        return instance.getRequestBuilder().mQueueBuilder;
    }

    public void doRequest() {
        final Request request = //your request initialization here
        mRequestQueue.add(request);
    }
    
    //... other requests goes here
}
```
And it will be great, great until you need to use different queues in your application. You might say that there are no problems, just add new `RequestQueue` field and initialize it
```java
    private RequestQueue mSecondRequestQueue;
    
    private RequestManager(Context context) {
        //...
        mSecondRequestQueue = //create your custom Queue here
    }
```
And what to do when there is more than two or three of them?
More fields that will migrate from one project to another?

There is more radical solution - `Map`.
```java
public class QueueBuilder {

    private Context mContext;
    
    private Map<String, RequestQueue> mRequestQueue = new HashMap<String, RequestQueue>();
    
    private String mCurQueue;    
    
    public QueueBuilder(Context context) {
        mContext = context;
    }
    
    public RequestController use(String queueName) {
        validateQueue(queueName);
        mCurQueue = queueName;
        return mRequestController;
    }
    
    private void validateQueue(String queueName) {
        if (!mRequestQueue.containsKey(queueName)) {
            final RequestQueue queue = RequestQueueFactory.getQueue(mContext, queueName);
            if (queue != null) {
                mRequestQueue.put(queueName, queue);
            } else {
                throw new IllegalArgumentException(
                        "RequestQueue - \"" + queueName + "\" doesn't exists!");
            }
        }
    }
    
}    
```
And to collect all yours Queues use simple Factory class :
```java
public class RequestQueueFactory {

    public static RequestQueue getQueue(Context context, String name) {
        RequestQueue result = null;

        if (RequestOptions.DEFAULT_QUEUE.equals(name)) {
            result = getDefault(context);
        }
        if (RequestOptions.BACKGROUND_QUEUE.equals(name)) {
            result = newBackgroundQueue(context);
        }

        return result;
    }

    public static RequestQueue getDefault(Context context) {
        return Volley.newRequestQueue(context.getApplicationContext());
    }
    
    //... all your queue realizations and helpers
}
```
You can define simple method to use your favorite queue
```java
public class QueueBuilder {
    //...
    public RequestController useBackgroundQueue() {
        return use(RequestOptions.BACKGROUND_QUEUE);
    }    
}
```
### Request

Another ugly problem is Requests creation. If you want to reuse Volley from old project than you will definitely meet it in your new one.

```java
public class RequestManager {
    //...
    //your old Request Manager with old requests.
    public void doRequest() {
        final Request request = //your request initialization here
        mRequestQueue.add(request);
    }
    
    //to reuse this manager in another project you will need to remove or change your old
    //requests from here
}
```
To create new\remove old\change current requests you will need to change your `RequestManager`. This is not the best practice and just not comfortable.

Thats why I decided to encapsulate methods and make their behavior as objects ([Strategy][5] design pattern).

```java
public abstract class RequestInterface {
    public abstract Request create();
}
```
Now we can create controller that will work with Queue and that simple Interface
```java
public class RequestController {

    private QueueBuilder mQueueBuilder;

    public RequestController(Context context) {
        mQueueBuilder = new QueueBuilder(context);
    }

    public RequestController addRequest(RequestInterface volleyRequest) {
        mQueueBuilder.getRequestQueue().add(volleyRequest.create());
        return this;
    }

    public void start() {
        mQueueBuilder.getRequestQueue().start();
    }

    public void stop() {
        mQueueBuilder.getRequestQueue().stop();
    }

    public void cancelAll(Object tag) {
        mQueueBuilder.getRequestQueue().cancelAll(tag);
    }

    public void cancelAll(RequestQueue.RequestFilter requestFilter) {
        mQueueBuilder.getRequestQueue().cancelAll(requestFilter);
    }
}
```
Simple Request example:
```java
public class TestJsonRequest extends RequestInterface {
    
    private Response.Listener<JSONObject> mResponseListener;
    private Response.ErrorListener errorListener = mErrorListener;
    
    public TestJsonRequest(Response.Listener<JSONObject> responseListener,
            Response.ErrorListener errorListener) {
        mResponseListener = responseListener;
        mErrorListener = errorListener;
    }

    @Override
    public Request create() {
        Uri.Builder uri = new Uri.Builder();
        uri.scheme("http");
        uri.authority("httpbin.org");
        uri.path("get");
        uri.appendQueryParameter("name", "Jon Doe");
        uri.appendQueryParameter("age", "21");
        String url = uri.build().toString();

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                mResponseListener,
                mErrorListener);

        return request;
    }
}
```
### Background

Volley deliver result from Requests into Callbacks that are handled in UI thread(more about this you can find [here][6]), this is not good especially when you need to parse and/or save Request result. 

Thats why I've added default Queue that always handle result in background thread. But then I've meet another problem - updating UI after background process. To do this we need to trigger `runOnUiThread()` or use `Handler`

```java
private Response.Listener mListener = new Response.Listener<JSONObject>() {
    @Override
    public void onResponse(JSONObject o) {
        //parse and save response data
        
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //update UI here
            }
        });
    }
};

private Response.ErrorListener mErrorListener = new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError volleyError) {
        //handle errors here (UI thread)    
    }
};
```
I don't want to do this manually, every time when I add new Callback. And that inner `Runnable` object in inner `Listener` object looks terrible. So I've created Callback that handle result in background and deliver result in UI thread like `AsyncTask` does

```java
private RequestCallback mRequestCallback = new RequestCallback<JSONObject, ResultType>() {
    @Override
    public ResultType doInBackground(JSONObject response) {
        //parse and save response data
        return new ResultType();
    }

    @Override
    public void onPostExecute(ResultType result) {
        //update UI here
        Toast.makeText(getApplicationContext(), "Toast from UI", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(VolleyError error) {
        //handle errors here (UI thread) 
        L.e(error.toString());
    }
};
```
and updated `RequestInterface`
```java
public abstract class RequestInterface<ResponseType, ResultType> {

    protected Handler mHandler;
    private RequestCallback<ResponseType, ResultType> mRequestCallback;
    private Response.Listener<ResponseType> mResponseListener;
    private Response.ErrorListener mErrorListener;

    public RequestInterface() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public abstract Request create();

    private Response.Listener<ResponseType> mInterfaceListener
            = new Response.Listener<ResponseType>() {
        @Override
        public void onResponse(ResponseType response) {
            if (mResponseListener != null) {
                mResponseListener.onResponse(response);
            } else if (mRequestCallback != null) {
                final ResultType resultType = mRequestCallback.doInBackground(response);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mRequestCallback.onPostExecute(resultType);
                    }
                });
            }
        }
    };

    private Response.ErrorListener mInterfaceErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (mErrorListener != null) {
                mErrorListener.onErrorResponse(error);
            } else if (mRequestCallback != null) {
                mRequestCallback.onError(error);
            }
        }
    };

    public final Response.Listener<ResponseType> useInterfaceListener() {
        return mInterfaceListener;
    }

    public final Response.ErrorListener useInterfaceErrorListener() {
        return mInterfaceErrorListener;
    }

    final void setRequestCallback(RequestCallback<ResponseType, ResultType> requestCallback) {
        mRequestCallback = requestCallback;
    }

    final void setResponseListener(Response.Listener<ResponseType> responseListener) {
        mResponseListener = responseListener;
    }

    final void setErrorListener(Response.ErrorListener errorListener) {
        mErrorListener = errorListener;
    }
}
```
new `Request` creation will look like this

```java
public class TestJsonRequest extends RequestInterface<JSONObject, Void> {

    @Override
    public Request create() {
        Uri.Builder uri = new Uri.Builder();
        uri.scheme("http");
        uri.authority("httpbin.org");
        uri.path("get");
        uri.appendQueryParameter("name", "Jon Doe");
        uri.appendQueryParameter("age", "21");
        String url = uri.build().toString();

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                
                //if you want to use Callbacks provided
                //via Request Manager interface
                //use useInterfaceListener() and useInterfaceErrorListener()
                //instead of creating new listenets here
                
                useInterfaceListener(),
                useInterfaceErrorListener());
        return request;
    }
}
```
To avoid Callbacks initialization via `RequestInterface` we need to provide a little update for `RequestController`
```java
public class RequestController {
    //...

    public RequestController addRequest(RequestInterface volleyRequest,
            RequestCallback requestCallback) {
        volleyRequest.setRequestCallback(requestCallback);
        mQueueBuilder.getRequestQueue().add(volleyRequest.create());
        return this;
    }

    public RequestController addRequest(RequestInterface volleyRequest,
            Response.Listener responseListener, Response.ErrorListener errorListener) {
        volleyRequest.setResponseListener(responseListener);
        volleyRequest.setErrorListener(errorListener);
        mQueueBuilder.getRequestQueue().add(volleyRequest.create());
        return this;
    }
    
    //...
}
```
Now when all puzzle parts are ready we can put them together. Lets look on it
```java
RequestManager.initializeWith(getApplicationContext());

//Queue using custom listener
RequestManager.queue()
        .useBackgroundQueue()
        .addRequest(new TestJsonRequest(), mRequestCallback)
        .start();
        
//Queue using default volley Response and Error listener
RequestManager
        .queue()
        .useBackgroundQueue()
        .addRequest(new TestJsonRequest(), mListener, mErrorListener)
        .start();
```

### Image Loader
Like in Volley Request creation, queues problems are present in Image Loader. So I've added `ImageQueueBuilder` and `ImageLoaderController` like I did for Requests. There is only one different between them - `BitmapLruCache` interface .
```java
public class ImageLoaderController {

    private ImageQueueBuilder mImageQueueBuilder;

    public ImageLoaderController(Context context) {
        mImageQueueBuilder = new ImageQueueBuilder(context);
    }

    public ImageLoader obtain() {
        return mImageQueueBuilder.getLoader();
    }

    public void clearCache() {
        final BitmapLruCache cache = mImageQueueBuilder.getCache();
        if (cache != null) {
            cache.evictAll();
        }
    }
}
```
`BitmapLruCache` is responsible for memory caching. It is pretty useful to be able to release memory and furthermore from large `Bitmap`.  

Here is an example of how you can deal with it 
```java

//load image
 RequestManager
            .loader()
            .useDefaultLoader()
            .obtain()
            .get(
                    "http://farm6.staticflickr.com/5475/10375875123_75ce3080c6_b.jpg",
                    mImageListener
            );
            
//clear chache
 RequestManager
            .loader()
            .useDefaultLoader()
            .clearCache();
```

[Full source code on github][7]


  [1]: https://developers.google.com/events/io/sessions/325304728
  [2]: http://dmytrodanylyk.github.io/dmytrodanylyk
  [3]: https://github.com/dmytrodanylyk/dmytrodanylyk/blob/gh-pages/articles/volley-part-2.md
  [4]: http://www.oodesign.com/singleton-pattern.html
  [5]: http://www.oodesign.com/strategy-pattern.html
  [6]: http://goo.gl/dScAvf
  [7]: https://github.com/yakivmospan/volley-request-manager
