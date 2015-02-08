import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import fr.go_detect.godetect.R;

public class TextSliderAdapter
        extends PagerAdapter {

    private int[] mData;
    private LayoutInflater mInflater;

    public TextSliderAdapter(Context context, int... data) {
        mInflater = LayoutInflater.from(context);
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        TextView view = new TextView(mInflater.getContext());
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        view.setTextAppearance(mInflater.getContext(), R.style.Set_Font_Here);
        view.setGravity(Gravity.CENTER);
        view.setText(getItem(position));
        container.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeViewAt(position);
    }

    public int getItem(int position) {
        return this.mData[position];
    }
}
