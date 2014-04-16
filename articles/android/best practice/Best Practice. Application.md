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


**From API 14** android gays added few simple observers, so now you don't need to create Custom Application every time

- Added possibility to set `ComponentCallbacks`: `onConfigurationChanged` and `onLowMemory`
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

```
**From API 18** we have one more observer `Application.OnProvideAssistDataListener` that allows to  to place into the bundle anything you would like to appear in the `Intent.EXTRA_ASSIST_CONTEXT` part of the assist Intent

```java
app.registerOnProvideAssistDataListener(new Application.OnProvideAssistDataListener() {
    @Override
    public void onProvideAssistData(Activity activity, Bundle data) {
    // put your changes here
    }
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
