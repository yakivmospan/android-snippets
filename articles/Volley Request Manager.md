##Volley Request Manager

On mine 3 years of developing practice every second project had feature like Http Client or Image Loader and can easily done with [Volley][1]. Thats why I decided to develop some sort of model that will provide:
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
And it will be great, great until you need to use different queues in your application. You will tell that there is no problems, just add new `RequestQueue` field and initialize it
```java
    private RequestQueue mSecondRequestQueue;
    
    private RequestManager(Context context) {
        //...
        mSecondRequestQueue = //create your custom Queue here
    }
```
And what to do when there is more than two or three of them?
More fields?

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

Another ugly problem is Requests creation. You will definitely meet it in your new tasty project while reusing Volley model from old one.

```java
public class RequestManager {
    //...
    //your old Request Manager with old requests.
    public void doRequest() {
        final Request request = //your request initialization here
        mRequestQueue.add(request);
    }
    
    //to reuse this manager on another project you will need to remove or change your old
    //requests from here
}
```
To create new\remove old\change current requests you will need to change your `RequestManager`. This is not the best practice and just not comfortable.

  [1]: https://developers.google.com/events/io/sessions/325304728
  [2]: http://dmytrodanylyk.github.io/dmytrodanylyk
  [3]: https://github.com/dmytrodanylyk/dmytrodanylyk/blob/gh-pages/articles/volley-part-2.md
  [4]: http://www.oodesign.com/singleton-pattern.html
