Sample shows simple factory that creates [ActivityToast][1]'s

#### Usage
```java
ToastFactory.makeText(this, ActivityToast.LENGTH_LONG, mOnClickListener).show();
```

#### Sources

```java
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

public class ToastFactory {

    public static ActivityToast makeText(
            Activity activity, long length, View.OnClickListener listener) {

        final View view = activity.getLayoutInflater().inflate(
                R.layout.view_toast,
                (ViewGroup) activity.getWindow().getDecorView(),
                false
        );
        view.findViewById(R.id.btnAction).setOnClickListener(listener);

        final ActivityToast toast = new ActivityToast(activity, view);
        toast.setLength(length);
        return toast;
    }

}

```

#### Resources

**view_toast.xml** file
```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:minHeight="@dimen/min_lay_height"
                android:minWidth="@dimen/min_lay_width"
                android:layout_marginBottom="5dp"
                android:background="@drawable/draw_toast_bg">

    <TextView android:id="@+id/txtMessage"
              android:layout_width="match_parent"
              android:layout_height="wrap_content"
              android:paddingLeft="12dp"
              android:paddingRight="@dimen/spacing_small"
              android:drawableLeft="@android:drawable/ic_dialog_alert"
              android:drawableStart="@android:drawable/ic_dialog_alert"
              android:drawablePadding="@dimen/spacing_big"
              android:gravity="center_vertical"
              android:layout_centerVertical="true"
              android:layout_toStartOf="@+id/separator"
              android:layout_toLeftOf="@+id/separator"
              android:textColor="@android:color/white"
              android:textSize="@dimen/text_size_medium"
              android:text="No connection."/>

    <View android:id="@+id/separator"
          android:layout_width="0.5dp"
          android:layout_height="32dp"
          android:layout_toStartOf="@+id/btnAction"
          android:layout_toLeftOf="@+id/btnAction"
          android:layout_centerVertical="true"
          android:background="@android:color/darker_gray"
          android:textColor="@android:color/white"
          android:textSize="@dimen/text_size_medium"/>


    <Button android:id="@+id/btnAction"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_centerVertical="true"
            android:layout_alignParentRight="true"
            android:layout_alignParentEnd="true"
            android:background="@drawable/sel_btn_retry"
            android:textColor="@android:color/white"
            android:textSize="@dimen/text_size_medium"
            android:text="Retry"/>

</RelativeLayout>
```

**draw_black_rect.xml** file
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
       android:shape="rectangle">

    <solid android:color="#66000000"/>
    <corners android:radius="@dimen/rect_radius"/>

</shape>
```

**draw_toast_bg.xml** file
```xml
<?xml version="1.0" encoding="utf-8"?>
<inset xmlns:android="http://schemas.android.com/apk/res/android"
       android:drawable="@drawable/draw_black_rect"
       android:insetTop="@dimen/spacing_small"
       android:insetRight="@dimen/spacing_big"
       android:insetBottom="@dimen/spacing_small"
       android:insetLeft="@dimen/spacing_big" />
```

**sel_btn_retry.xml** file

```xml
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:state_pressed="true" android:drawable="@android:color/holo_blue_dark"/>
    <item android:drawable="@android:color/transparent"/>
</selector>
```

[1]: https://github.com/yakivmospan/android-codeview/blob/gh-pages/gists/View.%20ActivityToast.md
