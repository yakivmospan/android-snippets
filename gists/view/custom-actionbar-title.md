### Important:

- Use R.layout.ben_toolbar_simple_title if you have menu items in layout, or doesn't have any buttons at all
- Use R.layout.ben_toolbar_margin_title if you have only one button in left corner (it could be back button for e.g)

**Activity.java**
```java
public void initActionBar() {
    ActionBar supportActionBar = getSupportActionBar();
    supportActionBar.setDisplayShowTitleEnabled(false);
    supportActionBar.setDisplayHomeAsUpEnabled(true);
    supportActionBar.setDisplayShowCustomEnabled(true);

    supportActionBar.setCustomView(R.layout.toolbar_simple_title);//have no margin
    supportActionBar.setCustomView(R.layout.toolbar_margin_title);//have righth margin
    
    TextView toolbarTitle = (TextView) supportActionBar.getCustomView().findViewById(
            R.id.toolbarTitle
    );
    toolbarTitle.setText(R.string.share);

    //supportActionBar.setBackgroundDrawable();
    //...
}
```

**toolbar_margin_title.xml**
```xml
<TextView xmlns:android="http://schemas.android.com/apk/res/android"
          android:id="@+id/toolbarTitle"
          android:layout_width="match_parent"
          android:layout_height="match_parent"
          android:gravity="center"
          android:layout_marginRight="@dimen/abc_action_button_min_width_material"
          android:textSize="20dp"
          android:textColor="@android:color/white"
          android:singleLine="true"
          android:ellipsize="end">
</TextView>
```

**toolbar_simple_title.xml**
```xml
<TextView xmlns:android="http://schemas.android.com/apk/res/android"
          android:id="@+id/toolbarTitle"
          android:layout_width="match_parent"
          android:layout_height="match_parent"
          android:gravity="center"
          android:textSize="20dp"
          android:textColor="@android:color/white"
          android:singleLine="true"
          android:ellipsize="end">
</TextView>
```
