### Widget: Simple Implementation

Snippent shows simple implementation of android widget that can recieve custom intents. Also shows how to update widget

**Send custom intent to widget:**
```java
//Note. Don't use LocalBroadcastManager to send intents to widget, it won't work
context.sendBroadcast(new Intent(Widget.CUSTOM_ACTION));
```

**Widget**:
```java
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class Widget extends AppWidgetProvider {

    public static final String CUSTOM_ACTION = "com.your.package.CUSTOM_ACTION";

    @Override
    public void onUpdate(final Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {
            appWidgetManager.updateAppWidget(appWidgetIds[i], getRemoteViews(context));
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();

        if (CUSTOM_ACTION.equals(action)) {
            AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
            ComponentName component = new ComponentName(
                    context.getPackageName(), Widget.class.getName()
            );

            RemoteViews views = getRemoteViews(context);
            widgetManager.updateAppWidget(component, views);
        }
    }

    private RemoteViews getRemoteViews(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        views.setOnClickPendingIntent(R.id.viewId, pendingIntent);
        views.setTextViewText(R.id.viewId, "Some text");
        return views;
    }
}

```

**AndroidManifest:**
```xml
<receiver
   android:name=".Widget"
   android:icon="@drawable/ic_launcher"
   android:label="@string/app_name" >
   <intent-filter>
       <action android:name="android.appwidget.action.APPWIDGET_UPDATE"/>
       <action android:name="com.your.package.CUSTOM_ACTION"/>

   </intent-filter>

   <meta-data
       android:name="android.appwidget.provider"
       android:resource="@xml/widget_metadata" >
   </meta-data>
</receiver>
```
