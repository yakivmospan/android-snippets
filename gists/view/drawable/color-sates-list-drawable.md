**Usage**:

```java
 ascentColor = getAscentColor();

final Drawable unSelected = context.getResources().getDrawable(
        R.drawable.unselected
);

final Drawable selected = context.getResources().getDrawable(
        R.drawable.tab_selected
);

ColorStatesListDrawable result = new ColorStatesListDrawable();

result.addState(
        new int[]{android.R.attr.state_selected}, selected,
        DrawableUtil.getDirectColorFilter(ascentColor)
);

result.addState(
        new int[]{android.R.attr.state_checked}, selected,
        DrawableUtil.getDirectColorFilter(ascentColor)
);

result.addState(
        new int[]{android.R.attr.state_enabled}, unSelected,
        DrawableUtil.getDirectColorFilter(ascentColor)
);
```

**ColorStatesListDrawable.java**
```java
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.StateSet;

import java.util.ArrayList;
import java.util.List;

public class ColorStatesListDrawable extends StateListDrawable {

    private List<State> mStates = new ArrayList<State>();

    public ColorStatesListDrawable() {
        super();
    }

    @Override
    public ConstantState getConstantState() {
        return new ColorConstantState(super.getConstantState());
    }

    @Override
    protected boolean onStateChange(int[] states) {
        State state = findState(states);
        if (state != null) {
            applyFilter(state);
        } else {
            clearColorFilter();
        }

        return super.onStateChange(states);
    }

    public void addState(int[] stateSet, Drawable drawable, ColorFilter colorFilter) {
        super.addState(stateSet, drawable);
        mStates.add(new State(stateSet, drawable, colorFilter));
    }

    @Override
    public void addState(int[] stateSet, Drawable drawable) {
        addState(stateSet, drawable, null);
    }

    void applyFilter(State state) {
        setColorFilter(state.colorFilter);
    }

    private State findState(int[] stateSet) {
        if (mStates != null) {
            for (int i = 0; i < mStates.size(); i++) {
                State state = mStates.get(i);
                if (StateSet.stateSetMatches(state.set, stateSet)) {
                    return state;
                }
            }
        }
        return null;
    }

    static final class State {

        int[] set;
        Drawable drawable;
        ColorFilter colorFilter;

        State(int[] stateSet, Drawable drawable, ColorFilter colorFilter) {
            this.set = stateSet;
            this.drawable = drawable;
            this.colorFilter = colorFilter;
        }
    }

    public class ColorConstantState extends ConstantState {

        private ConstantState mConstantState;

        public ColorConstantState(ConstantState constantState) {
            mConstantState = constantState;
        }

        @Override
        public Drawable newDrawable() {
            ColorStatesListDrawable statesListDrawable = new ColorStatesListDrawable();

            for (State state : mStates) {
                statesListDrawable.addState(
                        state.set.clone(),
                        state.drawable.getConstantState().newDrawable(),
                        state.colorFilter);
            }

            return statesListDrawable;
        }

        @Override
        public int getChangingConfigurations() {
            return mConstantState.getChangingConfigurations();
        }
    }
}
```
