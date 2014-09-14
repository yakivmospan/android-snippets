###Utils: Custom Toast

Snippet shows simple implementation of custom `Toast` that can be easily used in hole application.

![Header](/assets/images/gists/utilst-custom-toast-1.png)

####Key notes 

- Use `Toast` to show simple informative messages only
- Create custom `Toast` when you need to change for example text font, view color or add some image to make message more informative
- You can not change `Toast` animation even if you create custom one
- You have only two duration options `Toast.LENGTH_SHORT` and `Toast.LENGTH_LONG`
- You can not handle click on `Toast` even on custom one
- Try to reuse `Toast` instance instead of creating new one

####Usage

```java
// use it like native Toast object
ErrorToast.makeToast(activity, "Message", Toast.LENGTH_LONG).show();

ErrorToast toast = new ErrorToast(activity);
toast .setText("Message");
toast .setGravity(Toast.LENGTH_SHORT);
toast .show();

// cancel toast if it is showing at the moment
if(toast .isShowing()){
	toast .cancel();
}
```

####Sources

`ErrorToast`  class
```java
import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ErrorToast {

    private Activity mActivity;
    private Toast mToast;

    public ErrorToast(Activity activity) {
        mActivity = activity;
        View view = mActivity.getLayoutInflater().inflate(
                R.layout.view_toast,
                (ViewGroup) activity.getWindow().getDecorView(),
                false
        );

        mToast = new Toast(mActivity.getApplicationContext());
        mToast.setView(view);
    }

    public static ErrorToast makeToast(Activity activity, String message, int duration) {
        ErrorToast result = new ErrorToast(activity);

        result.mToast.setDuration(duration);
        result.mToast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        result.setText(message);

        return result;
    }

    public void show() {
        mToast.show();
    }

    public void cancel() {
        mToast.cancel();
    }

    public void setText(String message) {
        TextView txtMessage = (TextView) mToast.getView().findViewById(R.id.txtMessage);
        txtMessage.setText(message);
    }

    public void setText(int messageId) {
        setText(mActivity.getText(messageId).toString());
    }

    public boolean isShowing() {
        return mToast.getView().isShown();
    }

    public void setDuration(int duration) {
        mToast.setDuration(duration);
    }

    public void setGravity(int gravity, int xOffset, int yOffset) {
        mToast.setGravity(gravity, xOffset, yOffset);
    }
}
```

####Resources

**view_toast.xml** file
```xml
<?xml version="1.0" encoding="utf-8"?>

<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
                xmlns:tools="http://schemas.android.com/tools"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:minHeight="@dimen/min_lay_height"
                android:minWidth="@dimen/min_lay_width"
                android:background="@drawable/toast_bg">

    <TextView android:id="@+id/txtMessage"
              android:layout_width="wrap_content"
              android:layout_height="wrap_content"
              android:paddingLeft="@dimen/spacing_big"
              android:paddingRight="16dp"
              android:drawableLeft="@android:drawable/ic_dialog_alert"
              android:drawableStart="@android:drawable/ic_dialog_alert"
              android:drawablePadding="@dimen/spacing_big"
              android:gravity="center_vertical"
              android:layout_centerVertical="true"
              android:textColor="@android:color/white"
              android:textSize="@dimen/text_size_medium"
              tools:text="No connection."/>
</RelativeLayout>
```

**black_rect.xml** file
```xml
<?xml version="1.0" encoding="utf-8"?>

<shape xmlns:android="http://schemas.android.com/apk/res/android"
       android:shape="rectangle">

    <solid android:color="#66000000"/>
    <corners android:radius="@dimen/rect_radius"/>

</shape>
```

**toast_bg.xml** file

```xml
<?xml version="1.0" encoding="utf-8"?>

<inset xmlns:android="http://schemas.android.com/apk/res/android"
       android:drawable="@drawable/black_rect"
       android:insetTop="@dimen/spacing_small"
       android:insetRight="@dimen/spacing_big"
       android:insetBottom="@dimen/spacing_small"
       android:insetLeft="@dimen/spacing_big" />
```

**dimens.xml** file

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <dimen name="rect_radius">2dp</dimen>
    <dimen name="ac_padding">16dp</dimen>

    <dimen name="spacing_small">4dp</dimen>
    <dimen name="spacing_big">8dp</dimen>

    <dimen name="text_size_micro">12sp</dimen>
    <dimen name="text_size_small">14sp</dimen>
    <dimen name="text_size_medium">18sp</dimen>
    <dimen name="text_size_large">22sp</dimen>

    <dimen name="min_lay_width">48dp</dimen>
    <dimen name="min_lay_height">48dp</dimen>
</resources>
```
