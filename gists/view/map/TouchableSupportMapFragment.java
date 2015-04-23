package fr.go_detect.godetect.view.map;

import com.google.android.gms.maps.SupportMapFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static android.view.View.OnTouchListener;

/**
 * Created by Yakiv M. on 23.04.2015.
 */
public class TouchableSupportMapFragment extends SupportMapFragment {

    private View mOriginalContentView;
    private TouchableFrameLayout mTouchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        mOriginalContentView = super.onCreateView(inflater, parent, savedInstanceState);

        mTouchView = new TouchableFrameLayout(getActivity());
        mTouchView.addView(mOriginalContentView);

        return mTouchView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mOriginalContentView = null;
        mTouchView = null;
    }

    @Override
    public View getView() {
        return mOriginalContentView;
    }

    public void addOnMapTouchListener(OnTouchListener listener) {
        if(mTouchView != null) {
            mTouchView.addOnTouchListener(listener);
        }
    }

    public void removeOnMapTouchListener(OnTouchListener listener) {
        if(mTouchView != null) {
            mTouchView.removeOnTouchListener(listener);
        }
    }
}
