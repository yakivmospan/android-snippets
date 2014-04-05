### Utils: Keyboard

Some times we need to hide/show keyboard after some actions. This class will help to do this :

```java
public class KeyboardUtils {

    public static void showKeyboard(View theView) {
        Context context = theView.getContext();
        Object service = context.getSystemService(Context.INPUT_METHOD_SERVICE);

        InputMethodManager imm = (InputMethodManager) service;
        if (imm != null) {
            imm.toggleSoftInput(
                    InputMethodManager.SHOW_FORCED,
                    InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    public static void hideKeyboard(View theView) {
        Context context = theView.getContext();
        Object service = context.getSystemService(Context.INPUT_METHOD_SERVICE);

        InputMethodManager imm = (InputMethodManager) service;
        if (imm != null) {
            imm.hideSoftInputFromWindow(theView.getWindowToken(), 0);
        }
    }
}
```
