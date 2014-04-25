### Utils: DialogHelper

Class that helps to **show/dismiss** `DialogFraments` allowing state loss

```java
public class DialogHelper {

    public static void showAllowStateLoss(FragmentActivity activity, String tag, DialogFragment dialog) {
        if (activity != null && !activity.isFinishing()) {
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            ft.add(dialog,tag);
            ft.commitAllowingStateLoss();
        }
    }


    public static void dismissAllowStateLoss(FragmentActivity activity, DialogFragment dialog) {
        if (activity != null && !activity.isFinishing()) {
            dialog.dismissAllowingStateLoss();
        }
    }
}
```
