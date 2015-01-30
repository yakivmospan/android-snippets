**Usage**

Extend `SlideAnimationActivity` and simply start/finish it. To change start animation of 3d party APPS

```java
IntentUtils.openBrowser(activity, "github.com");
//start animation for 3d party app, we can just set in to on pause of fragment/activity,
//but then this animation will start even after home press
activity.overridePendingTransition(R.anim.pull_in_from_right, R.anim.hold);
```

To change close animation of 3d party APPS

```java
@Override
public void onStart() {
    super.onStart();
    FragmentActivity activity = getActivity();
    if(activity != null) {
        // start slide anim if fragment is resuming from browser or any 3d party app
        activity.overridePendingTransition(R.anim.pull_in_from_left, R.anim.pull_out_to_right);
     }
}
```

**SlideAnimationActivity.class**

```java
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

public class SlideAnimationActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_in_from_right, R.anim.hold);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Pressing back button in action bar doesn't call finish,
                //so we need to override Pending Transition here as well.
                //We can just set in to on pause, but then this animation will
                //start even after home press
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder
                            .create(this)
                            .addNextIntentWithParentStack(upIntent)
                            .startActivities()
                    ;
                } else {
                    NavUtils.navigateUpTo(this, upIntent);
                }
                overridePendingTransition(R.anim.pull_in_from_left, R.anim.pull_out_to_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.pull_in_from_left, R.anim.pull_out_to_right);
    }
}
```

==============================

**res/anim/hold.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<translate xmlns:android="http://schemas.android.com/apk/res/android"
           android:duration="350"
           android:fromXDelta="0"
           android:toXDelta="0" />
```

**res/anim/pull_in_from_left.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<translate xmlns:android="http://schemas.android.com/apk/res/android"
           android:duration="350"
           android:fromXDelta="-50%"
           android:interpolator="@android:anim/decelerate_interpolator"
           android:toXDelta="0%" />
```

**res/anim/pull_in_from_right.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<translate xmlns:android="http://schemas.android.com/apk/res/android"
           android:duration="350"
           android:fromXDelta="100%"
           android:interpolator="@android:anim/decelerate_interpolator"
           android:toXDelta="0%" />
```

**res/anim/pull_out_to_right.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<translate xmlns:android="http://schemas.android.com/apk/res/android"
           android:duration="350"
           android:fromXDelta="0%"
           android:interpolator="@android:anim/decelerate_interpolator"
           android:toXDelta="100%" />
```
