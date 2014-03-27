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

```java
public class App extends Application {
}
```

```xml
//AndroidManifest.xml
<application
    android:name=".App"
    android:icon="@drawable/ic_launcher"
    android:label="@string/app_name"
    android:theme="@style/AppTheme">
```

>onCreate ()


  [1]: http://developer.android.com/reference/android/content/ContentProvider.html
  [2]: http://developer.android.com/reference/android/app/Application.html
  [3]: http://developer.android.com/reference/android/content/ComponentCallbacks.html
  [4]: http://developer.android.com/reference/android/content/ComponentCallbacks2.html
  [5]: http://developer.android.com/reference/android/app/Application.ActivityLifecycleCallbacks.html
  [6]: http://developer.android.com/reference/android/app/Application.OnProvideAssistDataListener.html
