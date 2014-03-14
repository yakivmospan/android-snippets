![enter image description here][6]

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

  [1]: http://developer.android.com/reference/android/content/SharedPreferences.html
  [2]: http://developer.android.com/reference/android/content/SharedPreferences.Editor.html
  [3]: http://developer.android.com/reference/android/content/SharedPreferences.OnSharedPreferenceChangeListener.html
  [4]: http://www.oodesign.com/singleton-pattern.html
  [5]: http://developer.android.com/guide/topics/data/data-storage.html#pref
  [6]: https://raw.github.com/yakivmospan/yakivmospan/master/images/Article%20Header.png
