**AndroidManifest.xml**
```xml
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>

<receiver android:name=".BootReceiver">
    <intent-filter>
        <action android:name="android.intent.action.BOOT_COMPLETED"/>
        <action android:name="android.intent.action.QUICKBOOT_POWERON"/>
    </intent-filter>
</receiver>

<receiver android:name=".ShutdownReceiver">
    <intent-filter>
        <action android:name="android.intent.action.ACTION_SHUTDOWN" />
        <action android:name="android.intent.action.QUICKBOOT_POWEROFF" />
    </intent-filter>
</receiver>
```

**BootReceiver.java**
```java
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //do your staf here, start service for example
        Intent startServiceIntent = new Intent(context, ApplicationService.class);
        context.startService(startServiceIntent);
    }
}
```

**ShutdownReceiver.java**
```java
public class ShutdownReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //do your staf here, stop location tracking for example
    }
}
```
