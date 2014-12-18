Usage

```java
TextView txt = (TextView) findViewById(R.id.custom_fonts);  
txt.setTextSize(30);

Typeface font = Typeface.createFromAsset(getAssets(), "Akshar.ttf");
Typeface font2 = Typeface.createFromAsset(getAssets(), "bangla.ttf");   
SpannableStringBuilder SS = new SpannableStringBuilder("???????????");
SS.setSpan (new CustomTypefaceSpan("", font2), 0, 4,Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
SS.setSpan (new CustomTypefaceSpan("", font), 4, 11,Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
txt.setText(SS);
```

CustomTypefaceSpan.java
```java
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

    public class CustomTypefaceSpan extends TypefaceSpan {
        private final Typeface newType;

        public CustomTypefaceSpan(String family, Typeface type) {
            super(family);
            newType = type;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            applyCustomTypeFace(ds, newType);
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            applyCustomTypeFace(paint, newType);
        }

        private static void applyCustomTypeFace(Paint paint, Typeface tf) {
            int oldStyle;
            Typeface old = paint.getTypeface();
            if (old == null) {
                oldStyle = 0;
            } else {
                oldStyle = old.getStyle();
            }

            int fake = oldStyle & ~tf.getStyle();
            if ((fake & Typeface.BOLD) != 0) {
                paint.setFakeBoldText(true);
            }

            if ((fake & Typeface.ITALIC) != 0) {
                paint.setTextSkewX(-0.25f);
            }

            paint.setTypeface(tf);
        }
    }
```