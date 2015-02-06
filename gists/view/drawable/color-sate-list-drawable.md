**Usage**:

```java
ColorStateListDrawable colorDrawable = new ColorStateListDrawable(drawable);
colorDrawable.addState(new int[]{android.R.attr.state_checked}, new ColorFilterState(selectedColor,false));
colorDrawable.addState(new int[]{android.R.attr.state_pressed}, new ColorFilterState(selectedColor,false));
olorDrawable.addState(new int[]{}, new ColorFilterState(color,false));
```

**ColorStatesListDrawable.java**
```java
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.util.StateSet;

public class ColorStateListDrawable
        extends Drawable
{

    private Drawable origin;
    private final StateListState mStateListState;
    private boolean mMutated;

    public ColorStateListDrawable(final Drawable theOrigin)
    {
        this(null, null, theOrigin);
    }

    public ColorStateListDrawable(Context theContext, int theDrawableId)
    {
        this(null, null, theContext.getResources().getDrawable(theDrawableId));
    }

    private ColorStateListDrawable(StateListState state, Resources res, Drawable theOrigin)
    {
        StateListState as = new StateListState(state, this, res);
        mStateListState = as;
        this.origin = theOrigin.getConstantState().newDrawable().mutate();
        onStateChange(getState());
    }

    @Override
    public void draw(Canvas canvas)
    {
        this.origin.draw(canvas);
    }

    @Override
    public boolean getPadding(Rect padding)
    {
        return origin.getPadding(padding);
    }

    @Override
    public Region getTransparentRegion()
    {
        return origin.getTransparentRegion();
    }

    @Override
    public int getOpacity()
    {
        return this.origin.getOpacity();
    }

    @Override
    public void setAlpha(int alpha)
    {
        this.origin.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf)
    {
        throw new UnsupportedOperationException("This operation isn't supported");
    }

    @Override
    public int getIntrinsicWidth()
    {
        return origin.getIntrinsicWidth();
    }

    @Override
    public int getIntrinsicHeight()
    {
        return origin.getIntrinsicHeight();
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom)
    {
        this.origin.setBounds(left, top, right, bottom);
        super.setBounds(left, top, right, bottom);
    }

    /**
     * 
     * @param stateSet
     * @param theColor
     *            color to paint image
     * @param theInvertAlpha
     *            if true alpha channel will be inverted (transparent regions
     *            will become solid and otherwise)
     */
    public void addState(int[] stateSet, int theColor, boolean theInvertAlpha)
    {
        addState(stateSet, new ColorFilterState(theColor, theInvertAlpha));
    }

    public void addState(int[] stateSet, ColorFilterState stateFilter)
    {
        if (stateFilter != null) {
            mStateListState.addStateSet(stateSet, stateFilter);
            // in case the new state matches our current state...
            onStateChange(getState());
        }
    }

    @Override
    public boolean isStateful()
    {
        return true;
    }

    @Override
    protected boolean onStateChange(int[] stateSet)
    {
        int idx = mStateListState.indexOfStateSet(stateSet);
        if (idx < 0) {
            idx = mStateListState.indexOfStateSet(StateSet.WILD_CARD);
        }
        if (selectDrawable(idx)) {
            return true;
        }
        return false;
    }

    public boolean selectDrawable(int thePosition)
    {
        final ColorFilterState state = this.mStateListState.getStateFilterByIndex(thePosition);
        if (state != null) {
            state.inflateOnDrawable(origin);
            invalidateSelf();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets the number of states contained in this drawable.
     * 
     * @return The number of states contained in this drawable.
     * @hide pending API council
     * @see #getStateSet(int)
     * @see #getStateDrawable(int)
     */
    public int getStateCount()
    {
        return mStateListState.getChildCount();
    }

    /**
     * Gets the state set at an index.
     * 
     * @param index
     *            The index of the state set.
     * @return The state set at the index.
     * @hide pending API council
     * @see #getStateCount()
     * @see #getStateDrawable(int)
     */
    public int[] getStateSet(int index)
    {
        return mStateListState.stateSets.get(index);
    }

    /**
     * Gets the drawable at an index.
     * 
     * @param index
     *            The index of the drawable.
     * @return The drawable at the index.
     * @hide pending API council
     * @see #getStateCount()
     * @see #getStateSet(int)
     */
    public Drawable getStateDrawable(int index)
    {
        final ColorFilterState state = mStateListState.getStateFilterByIndex(index);
        state.inflateOnDrawable(origin);
        return this.origin;
    }

    /**
     * Gets the index of the drawable with the provided state set.
     * 
     * @param stateSet
     *            the state set to look up
     * @return the index of the provided state set, or -1 if not found
     * @hide pending API council
     * @see #getStateDrawable(int)
     * @see #getStateSet(int)
     */
    public int getStateDrawableIndex(int[] stateSet)
    {
        return mStateListState.indexOfStateSet(stateSet);
    }

    @Override
    public Drawable mutate()
    {
        if (!mMutated && super.mutate() == this) {
            final ArrayList<int[]> sets = mStateListState.stateSets;
            final int count = sets.size();
            mStateListState.stateSets = new ArrayList<int[]>(count);
            for (int i = 0; i < count; i++) {
                mStateListState.stateSets.add(sets.get(i).clone());
            }
            mMutated = true;
        }
        return this;
    }

    static final class StateListState
    {
        private ArrayList<int[]> stateSets;
        private ArrayList<ColorFilterState> stateFilters;

        StateListState(StateListState orig, ColorStateListDrawable owner, Resources res)
        {

            if (orig != null) {
                stateSets = orig.stateSets;
                stateFilters = orig.stateFilters;
            } else {
                stateSets = new ArrayList<int[]>();
                stateFilters = new ArrayList<ColorStateListDrawable.ColorFilterState>();
            }
        }

        int addStateSet(int[] stateSet, ColorFilterState filter)
        {
            stateSets.add(stateSet);
            stateFilters.add(filter);
            return stateSets.size() - 1;
        }

        private int indexOfStateSet(int[] stateSet)
        {
            for (int i = 0; i < this.stateSets.size(); i++) {
                if (StateSet.stateSetMatches(this.stateSets.get(i), stateSet)) {
                    return i;
                }
            }
            return -1;
        }

        protected int getChildCount()
        {
            return this.stateSets.size();
        }

        protected ColorFilterState getStateFilterByIndex(int thePosition)
        {
            final List<ColorFilterState> states = this.stateFilters;
            if (thePosition >= 0 && thePosition < states.size()) {
                return states.get(thePosition);
            } else {
                return null;
            }
        }
    }

    public static class ColorFilterState
    {
        private ColorFilter filter;

        public ColorFilterState()
        {
        }

        public ColorFilterState(final ColorFilter theFilter)
        {
            this.filter = theFilter;
        }

        public ColorFilterState(int theColor, boolean invertAlpha)
        {
            setColorFilter(theColor, invertAlpha);
        }

        public void setColorFilter(int theColor, boolean invertAlpha)
        {
            if (invertAlpha) {
                this.filter = DrawableUtil.getInvertedColorFilter(theColor);
            } else {
                this.filter = DrawableUtil.getDirectColorFilter(theColor);
            }
        }

        public void inflateOnDrawable(final Drawable theOrigin)
        {
            theOrigin.setColorFilter(filter);
        }

        public void setFilter(ColorFilter filter)
        {
            this.filter = filter;
        }
    }
}

```
