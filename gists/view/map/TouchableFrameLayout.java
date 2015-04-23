package fr.go_detect.godetect.view.map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yakiv M. on 23.04.2015.
 */
public class TouchableFrameLayout  extends FrameLayout {

    private List<OnTouchListener> mTouchListeners = new ArrayList<>();

    public TouchableFrameLayout(Context context) {
        super(context);
    }
    public TouchableFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public TouchableFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public TouchableFrameLayout(Context context, AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (OnTouchListener listener : mTouchListeners) {
            listener.onTouch(this, ev);
        }

        return super.dispatchTouchEvent(ev);
    }

    public void addOnTouchListener(OnTouchListener listener) {
        if(listener != null) {
            mTouchListeners.add(listener);
        }
    }

    public void removeOnTouchListener(OnTouchListener listener) {
        if(listener != null) {
            mTouchListeners.remove(listener);
        }
    }
}
