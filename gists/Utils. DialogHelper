### Utils: DialogHelper

Class that helps to **show/dismiss** `DialogFraments`

```java
public class DialogHelper {

    public static void showAllowStateLoss(FragmentActivity activity, DialogFragment dialog) {
        if (!activity.isFinishing()) {
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            ft.add(dialog, dialog.getTag());
            ft.commitAllowingStateLoss();
        }
    }


    public static void dismissAllowStateLoss(FragmentActivity activity, DialogFragment dialog) {
        if (!activity.isFinishing()) {
            dialog.dismissAllowingStateLoss();
        }
    }
}
```
