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
        return mRequestQueue.add(request);
    }
}
```

  [1]: https://developers.google.com/events/io/sessions/325304728
  [2]: http://dmytrodanylyk.github.io/dmytrodanylyk
  [3]: https://github.com/dmytrodanylyk/dmytrodanylyk/blob/gh-pages/articles/volley-part-2.md
  [4]: http://www.oodesign.com/singleton-pattern.html
