## Best Practice: SharedPreferences
Android provides many ways of storing application data. One of those ways leads us to the **SharedPreferences** object which is used to store private primitive data in key-value pairs.

All logic are based only on three simple classes:

- [SharedPreferences][1]
- [SharedPreferences.Editor][3]
- [SharedPreferences.OnSharedPreferenceChangeListener][4]

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
preferences.get...("key", defaultValue)

// you can get all Map but be careful you must not modify the collection returned by this
// method, or alter any of its contents.
Map<String, ?> all = preferences.getAll();

// get Editor object
SharedPreferences.Editor edit = preferences.edit();

//add on Change Listener
preferences.registerOnSharedPreferenceChangeListener(mListener);

//remove on Change Listener
preferences.unregisterOnSharedPreferenceChangeListener(mListener);
```
### Editor

### Performance test

- `SharedPreferences` in an Singleton object so you can easily get as many references as you want, it opens file only when you call `getSharedPreferences` first time, or create only one reference for it.

```java
// There are 1000 String values in preferences

SharedPreferences first = context.getSharedPreferences("com.example.app", Context.MODE_PRIVATE);
// call time = 4 milliseconds

SharedPreferences second = context.getSharedPreferences("com.example.app", Context.MODE_PRIVATE);
// call time = 0 milliseconds

SharedPreferences third = context.getSharedPreferences("com.example.app", Context.MODE_PRIVATE);
// call time = 0 milliseconds
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

[Android guide.][2]


  [1]: http://developer.android.com/reference/android/content/SharedPreferences.html
  [2]: http://developer.android.com/guide/topics/data/data-storage.html#pref
  [3]: http://developer.android.com/reference/android/content/SharedPreferences.Editor.html
  [4]: http://developer.android.com/reference/android/content/SharedPreferences.OnSharedPreferenceChangeListener.html
