### Volley Request Manager - Lite

[Full source code on github][1]

In my [Volley Request Manager][2] article I've described model that covers a lot of stuff. But in practice you need only half of it or even less. So I've decided to create this version, that is :

- Reusable
- So simple as possible
- With possibility to include HTTP Client and Image Loader or only one of them
- Still with possibilities to use different Queues and Image Loaders

### Description

Library consists of two simple packages: **http** and **utils**. They are independent.

**http**  contains only two independent classes :

- `ImageManager` - for image loading
- `RequestManager` - for requests processing

**utils** contains classes that will help you to create your own `Requests`, `Queues` or `ImageLoaders`. You can use them if you wish

### Usage

Include [library][2] or just copy component that you need from **http** package in your project.

```java
// init component that for request processing
RequestManager.initializeWith(getApplicationContext());

// create request
Request request = new JsonObjectRequest(
        Request.Method.GET,
        "request url here",
        null,
        mListener,
        mErrorListener);
        
// process request with default queue      
RequestManager.queue().doRequest(request);

// process request with custom queue      
RequestManager.queue().doRequest(request, customQueue);
```

```java
// init component that for image loading
ImageManager.initializeWith(getApplicationContext());

// load image with defaul ImageLoader
ImageManager.loader().doLoad(
        "http://farm6.staticflickr.com/5475/10375875123_75ce3080c6_b.jpg",
        mImageListener);
        
// load image with cusmot ImageLoader
ImageManager.loader().doLoad(
        "http://farm6.staticflickr.com/5475/10375875123_75ce3080c6_b.jpg",
        mImageListener,
        customImageLoader);
        
// load image with NetworkImageView
NetworkImageView view = new NetworkImageView(context);

ImageManager.loader().doLoad(
        "http://farm6.staticflickr.com/5475/10375875123_75ce3080c6_b.jpg",
        view);        
  
 view.view.setImageUrl(
        "http://farm6.staticflickr.com/5475/10375875123_75ce3080c6_b.jpg",
        ImageManager.loader.instance()); // to use default ImageLoader       
```

  [1]: https://github.com/yakivmospan/volley-request-manager-lite
  [2]: https://github.com/yakivmospan/yakivmospan/blob/master/articles/android/http/Volley%20Request%20Manager.md
