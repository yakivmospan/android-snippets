**Usage**:

```java
// Create darken selector color
int enabledColor = colors.parseColor(accent);
int pressedColor = DrawableUtil.darkenColor(enabledColor, 0.2f);
```

```java
// Creates 20% blacked color
int enabledColor = colors.parseColor(accent);
int pressedColor = DrawableUtil.blendColor(enabledColor, Color.BLACK, 0.2f);
```

```java
// Paint drawable in specific color
Drawable drawable = context.getResources().getDrawable(R.drawable.snowflake);
drawable.setColorFilter(DrawableUtil.getDirectColorFilter(color));
```




**DrawableUtil.java**
```java
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class DrawableUtil {

    private DrawableUtil() {
    }

    /**
     * @param context     context to retrieve resources
     * @param originResId id of drawable resource
     * @param color       color of resulting image
     * @param inverted    if true - alpha channel will be inverted (transparent regions will become
     *                    solid and otherwise)
     * @return new drawable object with specific colorfilter. Sometimes system may refuse drawable
     * cloning so method will return theOrigin drawable.
     */
    public static Drawable getStyledDrawable(Context context, int originResId, int color,
            boolean inverted) {
        final Resources res = context.getResources();
        final Drawable origin = res.getDrawable(originResId);
        return getStyledDrawable(origin, color, inverted);
    }

    /**
     * @param origin   the image which will be inflated.
     * @param color    color of resulting image
     * @param inverted if true - alpha channel will be inverted (transparent regions will become
     *                 solid and otherwise)
     * @return new drawable object with specific colorfilter. Sometimes system may refuse drawable
     * cloning so method will return origin drawable.
     */
    public static Drawable getStyledDrawable(Drawable origin, int color, boolean inverted) {
        final Drawable drawableToMutate = origin.getConstantState().newDrawable().mutate();
        if (inverted) {
            drawableToMutate.setColorFilter(getInvertedColorFilter(color));
        } else {
            drawableToMutate.setColorFilter(color, Mode.MULTIPLY);
        }

        return drawableToMutate;
    }

    /**
     * @return Colorfilter , which is inverting alpha and paints transparent regions with specified
     * color.
     */
    public static ColorFilter getInvertedColorFilter(int color) {
        final float alpha = (float) Color.alpha(color) / 255f;
        final float red = (float) Color.red(color) / 255f;
        final float green = (float) Color.green(color) / 255f;
        final float blue = (float) Color.blue(color) / 255f;
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(
                new float[]{
                        0, 0, 0, 0, red,
                        0, 0, 0, 0, green,
                        0, 0, 0, 0, blue,
                        0, 0, 0, -1, 255 * (alpha)
                }
        );
        return filter;
    }

    /**
     * @return Colorfilter , which is painting image with specified color
     */
    public static ColorFilter getDirectColorFilter(int color) {
        final float alpha = (float) Color.alpha(color) / 255f;
        final float red = (float) Color.red(color) / 255f;
        final float green = (float) Color.green(color) / 255f;
        final float blue = (float) Color.blue(color) / 255f;
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(
                new float[]{
                        red, 0, 0, 0, 0,
                        0, green, 0, 0, 0,
                        0, 0, blue, 0, 0,
                        0, 0, 0, alpha, 0}
        );
        return filter;
    }

    /**
     * Converts drawable to BitmapDrawable object. For bitmap drawables clonnig is performed.
     *
     * @param color drawable to convert
     * @return new BitmapDrawable object
     */
    public static Drawable toBitmapDrawable(Drawable color) {
        final Rect bounds = color.getBounds();
        int drawableWidth;
        int drawableHeight;

        if (bounds.isEmpty()) {
            final float dipFactor = Resources.getSystem().getDisplayMetrics().density;
            drawableWidth = (int) (color.getIntrinsicWidth() * dipFactor);
            drawableHeight = (int) (color.getIntrinsicHeight() * dipFactor);
            bounds.set(0, 0, drawableWidth, drawableHeight);
            color.setBounds(bounds);
        } else {
            drawableWidth = bounds.width();
            drawableHeight = bounds.height();
        }

        final Bitmap bmp = Bitmap.createBitmap(drawableWidth, drawableHeight, Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        color.draw(canvas);
        final BitmapDrawable rezult = new BitmapDrawable(bmp);
        return rezult;
    }

    /**
     * @param color color to inflate on
     * @param percent  float value, defining inflation (1 == 100%, 0 == 0%)
     * @return inflated color
     */
    public static int darkenColor(int color, float percent) {
        float hsv[] = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = hsv[2] * percent;
        int result = Color.HSVToColor(Color.alpha(color), hsv);
        return result;
    }

    public static int darkenColor(int theColor) {
        return darkenColor(theColor, 0.5f);
    }

    /**
     * @param color color to inflate on
     * @param percent  float value, defining inflation (1 == 100%, 0 == 0%)
     * @return inflated color
     */
    public static int lighterColor(int color, float percent) {
        float inverse = Math.abs(1.0f - percent);
        float hsbColor[] = new float[3];
        Color.colorToHSV(color, hsbColor);
        hsbColor[2] = (hsbColor[2] + 1.f) * inverse;
        return Color.HSVToColor(hsbColor);
    }

    /**
     * @param color color to inflate on
     * @param inverse color to blend
     * @param percent  float value, defining inflation of inverse color (1 == 100%, 0 == 0%)
     * @return inflated color
     */
    public static int blendColor(int color, int inverse, float percent) {
        float fInverse = 1.0f - percent;

        float hsbColor[] = new float[3];
        Color.colorToHSV(color, hsbColor);

        float hsbInverseColor[] = new float[3];
        Color.colorToHSV(inverse, hsbInverseColor);

        float hsbResult[] = new float[3];
        hsbResult[0] = hsbColor[0] * percent + hsbInverseColor[0] * fInverse;
        hsbResult[1] = hsbColor[1] * percent + hsbInverseColor[1] * fInverse;
        hsbResult[2] = hsbColor[2] * percent + hsbInverseColor[2] * fInverse;

        return Color.HSVToColor(hsbResult);
    }
}
```
