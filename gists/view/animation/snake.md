###Image Snake Animation

![Header](/assets/images/gists/view-animation-snake.gif)

####Usage

```java
SnakeAnimation.Params params = SnakeAnimation.Params.create(view)
        .setDelay(DELAY_DURATION)
        .setScale(SCALE_DURATION, SCALE_FACTOR)
        .setTranslate(TRANSLATE_DURATION, TRANSLATION_OFFSET, TRANSLATE_REPEAT_COUNT)
        .setRotate(ROTATE_DURATION, ROTATE_DEGREE, ROTATE_REPEAT_COUNT)
        .repeatOnEnd();
new SnakeAnimation(params).start();
```

####Sources

`SnakeAnimation` class
```java
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class SnakeAnimation {

    public static final int DELAY_DURATION = 1000;
    public static final int SCALE_DURATION = 700;
    public static final float SCALE_FACTOR = 1.5f;

    public static final int TRANSLATE_DURATION = 150;
    public static final int ROTATE_DURATION = 50;

    public static final int TRANSLATE_REPEAT_COUNT = 2;
    public static final int ROTATE_REPEAT_COUNT = 5;

    public static final int ROTATE_DEGREE = 4;
    public static final int TRANSLATION_OFFSET = 4;

    public static class Params {
        private View mView;
        private AnimatorListenerAdapter mListener;

        private int mStartDelay;
        private boolean mRepeatOnEnd;

        private int mScaleDuration;
        private float mScale;

        private int mTranslateDuration;
        private int mTranslateOffset;
        private int mTranslateRepeatCount;

        private int mRotationDuration;
        private int mRotateDegree;
        private int mRotateRepeatCount;

        private Params(View view) {
            mView = view;
        }

        public static Params create(@NonNull View view) {
            return new Params(view);
        }

        public static Params createDefault(@NonNull View view) {
            return SnakeAnimation.Params.create(view)
                    .setDelay(DELAY_DURATION)
                    .setScale(SCALE_DURATION, SCALE_FACTOR)
                    .setTranslate(TRANSLATE_DURATION, TRANSLATION_OFFSET, TRANSLATE_REPEAT_COUNT)
                    .setRotate(ROTATE_DURATION, ROTATE_DEGREE, ROTATE_REPEAT_COUNT)
                    .repeatOnEnd();
        }

        public Params setListener(AnimatorListenerAdapter listener) {
            mListener = listener;
            return this;
        }

        public Params setDelay(int startDelay) {
            mStartDelay = startDelay;
            return this;
        }

        public Params setScale(int scaleDuration, float scale) {
            mScaleDuration = scaleDuration;
            mScale = scale;
            return this;
        }

        public Params setTranslate(int duration, int toOffset, int repeatCount) {
            mTranslateDuration = duration;
            mTranslateOffset = toOffset;
            mTranslateRepeatCount = repeatCount;
            return this;
        }

        public Params setRotate(int duration, int toDegree, int repeatCount) {
            mRotationDuration = duration;
            mRotateDegree = toDegree;
            mRotateRepeatCount = repeatCount;
            return this;
        }

        public Params repeatOnEnd() {
            mRepeatOnEnd = true;
            return this;
        }
    }

    private Params mParams;
    private AnimatorSet mSnake;

    public SnakeAnimation(@NonNull Params params) {
        mParams = params;
    }

    public void start() {
        ObjectAnimator scaleIn = ObjectAnimator.ofPropertyValuesHolder(mParams.mView,
                PropertyValuesHolder.ofFloat("scaleX", mParams.mScale),
                PropertyValuesHolder.ofFloat("scaleY", mParams.mScale));
        scaleIn.setDuration(mParams.mScaleDuration);

        AnimatorSet translationSet = createTranslationSet();
        AnimatorSet rotationSet = createRotationSet();

        AnimatorSet translateAndRotate = new AnimatorSet();
        translateAndRotate.playTogether(translationSet, rotationSet);

        ObjectAnimator scaleOut = ObjectAnimator.ofPropertyValuesHolder(mParams.mView,
                PropertyValuesHolder.ofFloat("scaleX", 1.0f),
                PropertyValuesHolder.ofFloat("scaleY", 1.0f));
        scaleOut.setDuration(mParams.mScaleDuration);

        if (mSnake != null) {
            mSnake.cancel();
            mSnake = null;
        }

        mSnake = new AnimatorSet();
        mSnake.setStartDelay(mParams.mStartDelay);
        mSnake.playSequentially(scaleIn, translateAndRotate, scaleOut);
        if (mParams.mListener != null) {
            mSnake.addListener(mParams.mListener);
        }
        if (mParams.mRepeatOnEnd) {
            mSnake.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    mSnake.start();
                }
            });
        }
        mSnake.start();
    }

    public void cancel() {
        if (mSnake != null) {
            mSnake.cancel();
        }
    }

    private AnimatorSet createTranslationSet() {
        ObjectAnimator translateStart = ObjectAnimator.ofPropertyValuesHolder(
                mParams.mView,
                PropertyValuesHolder.ofFloat("translationX", 0, -mParams.mTranslateOffset)
        );
        translateStart.setDuration(mParams.mTranslateDuration / 2);

        List<Animator> translationLeftToRight = createTranslationLeftToRight();

        ObjectAnimator translateEnd = ObjectAnimator.ofPropertyValuesHolder(
                mParams.mView,
                PropertyValuesHolder.ofFloat("translationX", -mParams.mTranslateOffset, 0)
        );
        translateEnd.setDuration(mParams.mTranslateDuration / 2);

        List<Animator> translateAnimations = new ArrayList<>();
        translateAnimations.add(translateStart);
        translateAnimations.addAll(translationLeftToRight);
        translateAnimations.add(translateEnd);

        final AnimatorSet translation = new AnimatorSet();
        translation.playSequentially(translateAnimations);

        return translation;
    }

    private List<Animator> createTranslationLeftToRight() {
        List<Animator> result = new ArrayList<>();

        for (int i = 0; i < mParams.mTranslateRepeatCount; i++) {
            ObjectAnimator translateFromRight = ObjectAnimator.ofPropertyValuesHolder(
                    mParams.mView,
                    PropertyValuesHolder.ofFloat(
                            "translationX", -mParams.mTranslateOffset, mParams.mTranslateOffset
                    )
            );
            translateFromRight.setDuration(mParams.mTranslateDuration);

            ObjectAnimator translateFromLeft = ObjectAnimator.ofPropertyValuesHolder(
                    mParams.mView,
                    PropertyValuesHolder.ofFloat(
                            "translationX", mParams.mTranslateOffset, -mParams.mTranslateOffset
                    )
            );
            translateFromLeft.setDuration(mParams.mTranslateDuration);

            result.add(translateFromRight);
            result.add(translateFromLeft);
        }
        return result;
    }

    private AnimatorSet createRotationSet() {
        ObjectAnimator rotateStart = ObjectAnimator.ofPropertyValuesHolder(
                mParams.mView,
                PropertyValuesHolder.ofFloat("rotation", 0, mParams.mRotateDegree)
        );
        rotateStart.setDuration(mParams.mRotationDuration / 2);

        List<Animator> rotationRightToLeft = createRotationRightToLeft();

        ObjectAnimator rotateEnd = ObjectAnimator.ofPropertyValuesHolder(
                mParams.mView,
                PropertyValuesHolder.ofFloat("rotation", mParams.mRotateDegree, 0)
        );
        rotateEnd.setDuration(mParams.mRotationDuration / 2);

        List<Animator> rotationAnimations = new ArrayList<>();
        rotationAnimations.add(rotateStart);
        rotationAnimations.addAll(rotationRightToLeft);
        rotationAnimations.add(rotateEnd);

        final AnimatorSet rotation = new AnimatorSet();
        rotation.playSequentially(rotationAnimations);

        return rotation;
    }

    private List<Animator> createRotationRightToLeft() {
        List<Animator> result = new ArrayList<>();

        for (int i = 0; i < mParams.mRotateRepeatCount; i++) {
            ObjectAnimator rotateFromRight = ObjectAnimator.ofPropertyValuesHolder(
                    mParams.mView,
                    PropertyValuesHolder.ofFloat(
                            "rotation", mParams.mRotateDegree, -mParams.mRotateDegree
                    )
            );
            rotateFromRight.setDuration(mParams.mRotationDuration);

            ObjectAnimator rotateFromLeft = ObjectAnimator.ofPropertyValuesHolder(
                    mParams.mView,
                    PropertyValuesHolder.ofFloat(
                            "rotation", -mParams.mRotateDegree, mParams.mRotateDegree
                    )
            );
            rotateFromLeft.setDuration(mParams.mRotationDuration);

            result.add(rotateFromRight);
            result.add(rotateFromLeft);
        }
        return result;
    }
}
```
