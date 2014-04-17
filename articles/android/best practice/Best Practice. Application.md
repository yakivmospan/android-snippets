### Best Practice: Application

>Base class for those who need to maintain global application state. 

- **Application** is the start point of your program
- It's called when the application is starting, before any activity, service, or receiver objects have been created
- Only [Content Provider][1] is started before it

All logic are based in one [Application][2] class and few interfaces :

- [ComponentCallbacks][3]
- [ComponentCallbacks2][4]
- [Application.ActivityLifecycleCallbacks][5]
- [Application.OnProvideAssistDataListener][6]

### Application

`Application` is a [Singleton][7] that you can get from any [Activity][8] or [Service][9] with `getApplication()` method. Also you can cast your `getApplicationContext()` to Application.

```java
// get inside of Activity or Service
Application app = getApplication();

// get from Contex
Application app = (Application)view.getContext().getApplicationContext();
```

You can create your own custom application. To do this you need :

- Create `class` that extends `Application`

```java
public class App extends Application {
    // your logic goes here
}
```

- Initialize your custom `Application` in manifest (just add `name` tag that should matches your `Application` path)

```xml
<!--AndroidManifest.xml-->
<application
    android:name="yourpackage.App"
    android:icon="@drawable/ic_launcher"
    android:label="@string/app_name"
    android:theme="@style/AppTheme">
```

#### And what it can give to us ?

```java
// ...
Application app = getApplication();

// as Application extends ContextWrapper we can do everything the it can
// (get Assets as well)
AssetManager assets = app.getAssets();
```

And thats all, all we could do **before API 14** without overriding. So if you are supporting old OS versions and need to maintain global application state **you will need to** create Custom Application

```java
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // your application starts from here
    }
    
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // This is called when the overall system is running low on memory
        // and actively running processes should trim their memory usage
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Called by the system when the device configuration changes while your
        // component is running. Unlike activities Application doesn't restart when
        // a configuration changes
    }

    
    @Override
    public void onTerminate() {
        super.onTerminate();
        // This method is for use in emulated process environments only.
        // You can simply forget about it because it will never be called on real device
    }
}
```


**From API 14** android guys added few simple observers, so now you don't need to create Custom Application every time

- Added possibility to set `ComponentCallbacks` : `onConfigurationChanged` and `onLowMemory` methods
- Added new `ComponentCallbacks2` interface: implements `ComponentCallbacks` and has new `onTrimMemory` method. It provide us possibility to handle different memory levels change.
- **Note** that `onLowMemory` **is not called** from API 14. You should only use it as a fallback for older versions, which can be treated the same as `onTrimMemory` with the `ComponentCallbacks2.TRIM_MEMORY_COMPLETE` level.
- Added `registerActivityLifecycleCallbacks` which allows you to handle state change of each activity in your program

```java
// set ComponentCallbacks with out overriding
app.registerComponentCallbacks(new ComponentCallbacks() {
    @Override
    public void onConfigurationChanged(Configuration configuration) {
    // determinates Configuration Change
    }

    @Override
    public void onLowMemory() {
    // use it only for older API version 
    }
    
    @Override
    public void onTrimMemory(int level) {
    super.onTrimMemory(level);
    // Called when the operating system has determined that it is a good
    // time for a process to trim unneeded memory from its process
    }
});

// set ActivityLifecycleCallbacks
app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks(){
    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {}

    @Override
    public void onActivityStarted(Activity activity) {}

    @Override
    public void onActivityResumed(Activity activity) {}

    @Override
    public void onActivityPaused(Activity activity) {}

    @Override
    public void onActivityStopped(Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {}

    @Override
    public void onActivityDestroyed(Activity activity) {}
});

```
**From API 18** we have one more observer `Application.OnProvideAssistDataListener` that allows to place into the bundle anything you would like to appear in the `Intent.EXTRA_ASSIST_CONTEXT` part of the assist Intent

```java
app.registerOnProvideAssistDataListener(new Application.OnProvideAssistDataListener() {
    @Override
    public void onProvideAssistData(Activity activity, Bundle data) {
    // put your changes here
    }
});
```

### Performance & Tips

- `Applications` starts before any activity, service, or receiver objects have been created. Use this to initialize your model (http client, database, libraries etc.)

- There is normally no need to use Application as your static model(hold your objects as class fields). In most situation, static singletons can provide the same functionality in a more modular way. If your singleton needs a global context (for example to register broadcast receivers), the function to retrieve it can be given a Context which internally uses `Context.getApplicationContext()` when first constructing the singleton.

```java
public class App extends Application {

    @Override
    public void registerComponentCallbacks(ComponentCallbacks callback) {
        super.registerComponentCallbacks(callback);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // init your model before any activities starts
        // in this way you will always know where your application starts
        initModel();
    }

    private void initModel() {
        Context applicationContext = getApplicationContext();

        // initialize volley http client
        RequestManager.initializeWith(applicationContext);
        ImageManager.initializeWith(applicationContext);

        // initialize your singleton
        Model.INSTANCE.initialize(applicationContext);

        // initialize your preferences
        PreferencesManager.initializeInstance(applicationContext);
    }
}
```

```java
public enum Model {
    INSTANCE;
    public void initialize(Context context){
        // save your context as field
        // or do whatever you want here..
    }
}
```

- Always remember that `Application` runs on UI thread. Implementations of your `onCreate` method should be as quick as possible since the time spent in this method directly impacts the performance of starting the first activity, service, or receiver in a process

- If you override `onCreate` method, be sure to call `super.onCreate()`

- You can save your static fast changing global data right before your Application be killed. For example if you are collecting some GPS locations statistic is a bad practice for battery life to save it every time when new location came. Just save it when user want to stop collecting data is not enough because your Application can be killed in background when Android need some memory. To be sure that your data will be not lost you can use `onLowMemory` and `onTrimMemory` methods. Be sure that you need to do this in your `Application` class, every `Activity` and `Service` implements `ComponentCallbacks` interface. **Always remember** that even after this call application can be not killed or be killed with some delay. 

```java
// in Application, Activity or Service

ComponentCallbacks2 mComponentCallbacks = new ComponentCallbacks2() {
    @Override
    public void onTrimMemory(int level) {
        if(level == ComponentCallbacks2.TRIM_MEMORY_COMPLETE){
            saveGlobalData();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    }

    @Override
    public void onLowMemory() {
        // call it here to support old operating systems
        saveGlobalData();
    }

    private void saveGlobalData() {
        // save your stats to database
    }
};
```

- You can use `onTrimMemory` with `ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN` to handle when your application goes to background

```java
@Override
    public void onTrimMemory(int level) {
    if(level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN){
        // app in background
    }
}
```

- Save context in your `onCreate()` to have possibility to get it from any where in your project. But note that it will cause a lot of [Spaghetti code][10]. This is highly not recommended

```java
Context context = App.getContext();
```

```java
public class App extends Application {
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
    public static Context getContext() {
        return mContext;
    }
}    
```

- There are cases when we need to handle that all Activities are destroyed (App is off). If you need to stop `Service` that work with few screens when Application goes off. `ActivityLifecycleCallbacks` can help you to deal with it 

```java
// set ActivityLifecycleCallbacks
app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks(){
    
    private int mCounter;

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
      mCounter++;
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
       mCounter--;
       
       if(mScreensCounter == 0) {
          //... Application is Off
       }
       
       if(mScreensCounter < 0) {
           mCounter = 0;
       }
    }
    
    //... 
});

```


  [1]: http://developer.android.com/reference/android/content/ContentProvider.html
  [2]: http://developer.android.com/reference/android/app/Application.html
  [3]: http://developer.android.com/reference/android/content/ComponentCallbacks.html
  [4]: http://developer.android.com/reference/android/content/ComponentCallbacks2.html
  [5]: http://developer.android.com/reference/android/app/Application.ActivityLifecycleCallbacks.html
  [6]: http://developer.android.com/reference/android/app/Application.OnProvideAssistDataListener.html
  [7]: http://www.oodesign.com/singleton-pattern.html
  [8]: http://developer.android.com/reference/android/app/Activity.html
  [9]: http://developer.android.com/reference/android/app/Service.html
  [10]: http://en.wikipedia.org/wiki/Spaghetti_code
