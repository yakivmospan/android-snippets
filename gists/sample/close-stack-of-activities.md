```java

![Header](/assets/images/gists/sample-activity-stack.png)

public class ActivityC extends Activity{
  ...
  
  public void startHomeActivity(){
    Intent intent = new Intent(this, ActivityA.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(intent);
  }
  
}  
```
