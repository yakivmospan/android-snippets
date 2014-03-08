## Best Practice: SharedPreferences
Android provides many ways of storing application data. One of those ways leads us to the **SharedPreferences** object which is used to store private primitive data in key-value pairs.

All logic are based only on three simple classes:

- [SharedPreferences][1]
- [SharedPreferences.Editor][2]
- [SharedPreferences.OnSharedPreferenceChangeListener][3]

### SharedPreferences

`SharedPreferences` is main of them. It's responsible for getting (parsing) stored data, provides interface for getting `Editor` object and interfaces for adding and removing `OnSharedPreferenceChangeListener`

- To create `SharedPreferences` you will need `Context` object (can be an application `Context`)
- `getSharedPreferences` method parses Preference file and creates `Map` object for it
- You can create it in few modes provided by Context, it's strongly recommended to use MODE_PRIVATE because creating world-readable/writable files is very dangerous, and likely to cause security holes in applications

```java
// parse Preference file
SharedPreferences preferences = context.getSharedPreferences("com.example.app", Context.MODE_PRIVATE);

// get values from Map
preferences.getBoolean("key", defaultValue)
preferences.get..("key", defaultValue)

// you can get all Map but be careful you must not modify the collection returned by this
// method, or alter any of its contents.
Map<String, ?> all = preferences.getAll();

// get Editor object
SharedPreferences.Editor editor = preferences.edit();

//add on Change Listener
preferences.registerOnSharedPreferenceChangeListener(mListener);

//remove on Change Listener
preferences.unregisterOnSharedPreferenceChangeListener(mListener);

// listener example
SharedPreferences.OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener
        = new SharedPreferences.OnSharedPreferenceChangeListener() {
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }
};
```
### Editor

`SharedPreferences.Editor` is an Interface used for modifying values in a `SharedPreferences` object. All changes you make in an editor are batched, and not copied back to the original `SharedPreferences` until you call commit() or apply()

- Use simple interface to put values in `Editor`
- Save values synchronous with `commit()` or asynchronous with `apply` which is faster. In fact of using different threads using `commit()` is safer. Thats why I prefer to use **`commit()`**.
- Remove single value with `remove()` or clear all values with `clear()`

```java
// get Editor object
SharedPreferences.Editor editor = preferences.edit();

// put values in editor
editor.putBoolean("key", value);
editor.put..("key", value);

// remove single value by key
editor.remove("key");

// remove all values
editor.clear();

// commit your putted values to the SharedPreferences object synchronously
// returns true if successe
boolean result = editor.commit();

// do the same as commit() but asynchronously (faster but not safely)
// returns nothing
editor.apply();
```

### Performance & Tips

- `SharedPreferences` is a [Singleton][4] object so you can easily get as many references as you want, it opens file only when you call `getSharedPreferences` first time, or create only one reference for it.

```java
// There are 1000 String values in preferences

SharedPreferences first = context.getSharedPreferences("com.example.app", Context.MODE_PRIVATE);
// call time = 4 milliseconds

SharedPreferences second = context.getSharedPreferences("com.example.app", Context.MODE_PRIVATE);
// call time = 0 milliseconds

SharedPreferences third = context.getSharedPreferences("com.example.app", Context.MODE_PRIVATE);
// call time = 0 milliseconds
```

- As `SharedPreferences` is a [Singleton][4] object you can change any of It's instances and not be scared that their data will be different

```java
first.edit().putInt("key",15).commit();

int firstValue = first.getInt("key",0)); // firstValue is 15
int secondValue = second.getInt("key",0)); // secondValue is also 15

```

- When you call `get` method first time it parses value by key and adds this value to the map. So for second call it just gets it from map, without parsing.

```java
first.getString("key", null)
// call time = 147 milliseconds

first.getString("key", null)
// call time = 0 milliseconds

second.getString("key", null)
// call time = 0 milliseconds

third.getString("key", null)
// call time = 0 milliseconds

```

- Remember the larger the Preference object is the longer `get`, `commit`, `apply`, `remove` and `clear` operations will be. So it's highly recommended to separate your data in different small objects.

- Your Preferences **will not be removed** after Application update. So there are cases when you need to create some migration scheme. For example you have Application that parse local JSON in start of application, to do this only after first start you decided to save boolean flag `wasLocalDataLoaded`. After some time you updated that JSON and released new application version. Users will update their applications but they will not load new JSON because you already done it in first application version. 

```java
public class MigrationManager {
    private final static String KEY_PREFERENCES_VERSION = "key_preferences_version";
    private final static int PREFERENCES_VERSION = 2;

    public static void migrate(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        checkPreferences(preferences);
    }

    private static void checkPreferences(SharedPreferences thePreferences) {
        final double oldVersion = thePreferences.getInt(KEY_PREFERENCES_VERSION, 1);

        if (oldVersion < PREFERENCES_VERSION) {
            final SharedPreferences.Editor edit = thePreferences.edit();
            edit.clear();
            edit.putInt(KEY_PREFERENCES_VERSION, currentVersion);
            edit.commit();
        }
    }
}
```


[Android guide.][5]


  [1]: http://developer.android.com/reference/android/content/SharedPreferences.html
  [2]: http://developer.android.com/reference/android/content/SharedPreferences.Editor.html
  [3]: http://developer.android.com/reference/android/content/SharedPreferences.OnSharedPreferenceChangeListener.html
  [4]: http://www.oodesign.com/singleton-pattern.html
  [5]: http://developer.android.com/guide/topics/data/data-storage.html#pref
