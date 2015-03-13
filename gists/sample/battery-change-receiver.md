Usage:
private BatteryChangeReceiver mBatteryChangeReceiver;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mBatteryChangeReceiver = new BatteryChangeReceiver(getBaseContext());
    mBatteryChangeReceiver.register();
}
@Override
protected void onDestroy() {
    if(mBatteryChangeReceiver != null){
        mBatteryChangeReceiver.unRegister();
    }
    super.onDestroy();
}



**BatteryChangeReceiver.java**
```java
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;

public class BatteryChangeReceiver
        extends BroadcastReceiver {

    private Context mContext;

    public BatteryChangeReceiver(@NonNull Context context) {
        mContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int currentBatteryLevel = intent.getIntExtra("level", 0);
        if (currentBatteryLevel <= 50) {

        }
    }

    public void register(){
        mContext.registerReceiver(this, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public void unRegister(){
        mContext.unregisterReceiver(this);
    }
}
```
