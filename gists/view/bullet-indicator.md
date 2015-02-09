### Usage:

Just include `BulletIndicatorView` in layout

```java
<BulletIndicatorView
  android:layout_width="wrap_content"
  android:layout_height="wrap_content"/>
```

```java
BulletIndicatorView indicator = (BulletIndicatorView) view.findViewById(
        R.id.indicator
);

ViewPager pager = (ViewPager) view.findViewById(R.id.pager);

PagerAdapter adapter = new PagerAdapter(getActivity(), getFragmentManager());
pager.setAdapter(adapter);

indicator.setBulletNormal(R.drawable.bg_bullet);
indicator.setBulletSelected(R.drawable.bg_bullet_selected);;
indicator.setViewPager(pager);
indicator.setCurrentItem(Random.nextInt(0, adapter.getCount() - 1));
```

**BulletIndicatorView.java**
```java
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class BulletIndicatorView extends LinearLayout {

    private static final int DEFAULT_BULLET_PADDING = (int)
            (4 * Resources.getSystem().getDisplayMetrics().density); //dp

    private ViewPager mPager;

    private Drawable mBulletNormal;
    private Drawable mBulletSelected;

    private int mBulletPadding = DEFAULT_BULLET_PADDING;

    private boolean mIsBulletClickable = true;

    private ViewPager.OnPageChangeListener mUserListener;

    public BulletIndicatorView(Context context) {
        super(context);
    }

    public BulletIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BulletIndicatorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setViewPager(@NonNull ViewPager pager) {
        mPager = pager;
        mPager.setOnPageChangeListener(mDefaultListener);
        createBullets();
    }

    private void createBullets() {
        if (mPager != null) {
            PagerAdapter adapter = mPager.getAdapter();
            if (adapter != null) {
                removeAllViews();
                int count = adapter.getCount();
                for (int i = 0; i < count; i++) {
                    addView(createBulletView(i));
                }

                if (count > 0) {
                    setSelectedBullet(0);
                }
            }
        }
    }

    private View createBulletView(int id) {
        ImageView bullet = new ImageView(getContext());
        bullet.setId(id);
        bullet.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        bullet.setPadding(mBulletPadding, mBulletPadding, mBulletPadding, mBulletPadding);
        bullet.setOnClickListener(mOnClickListener);
        return bullet;
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if(mIsBulletClickable) {
                setCurrentItem(view.getId());
            }
        }
    };

    private ViewPager.OnPageChangeListener mDefaultListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i2) {
            if (mUserListener != null) {
                mUserListener.onPageScrolled(i, v, i2);
            }
        }

        @Override
        public void onPageSelected(int i) {
            setSelectedBullet(i);
            if (mUserListener != null) {
                mUserListener.onPageSelected(i);
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {
            if (mUserListener != null) {
                mUserListener.onPageSelected(i);
            }
        }
    };

    public void setCurrentItem(int position) {
        if(mPager!=null) {
            mPager.setCurrentItem(position);
        }
    }

    private void setSelectedBullet(int position) {
        if (position > getChildCount() || position < 0) {
            return;
        }

        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);

            if (!(view instanceof ImageView)) {
                return;
            }

            ImageView bullet = (ImageView) view;

            if (i != position) {
                bullet.setImageDrawable(mBulletNormal);
                bullet.setSelected(false);
            } else {
                bullet.setImageDrawable(mBulletSelected);
                bullet.setSelected(true);
            }
        }
    }

    public ViewPager getViewPager() {
        return mPager;
    }

    public void setAdapter(@Nullable PagerAdapter adapter) {
        if (mPager != null) {
            mPager.setAdapter(adapter);
            createBullets();
        }
    }

    public ViewPager.OnPageChangeListener getUserListener() {
        return mUserListener;
    }

    public void setUserListener(ViewPager.OnPageChangeListener userListener) {
        mUserListener = userListener;
    }

    public void setBulletPadding(int padding) {
        mBulletPadding = padding;
    }

    public void setBulletNormal(@Nullable Drawable drawable) {
        mBulletNormal = drawable;
    }

    public void setBulletNormal(int drawable) {
        setBulletNormal(getContext().getResources().getDrawable(drawable));
    }

    public void setBulletSelected(@Nullable Drawable drawable) {
        mBulletSelected = drawable;
    }

    public void setBulletSelected(int drawable) {
        setBulletSelected(getContext().getResources().getDrawable(drawable));
    }
}
```
