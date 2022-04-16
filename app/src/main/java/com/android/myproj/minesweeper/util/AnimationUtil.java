package com.android.myproj.minesweeper.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class AnimationUtil {

    public static void fadeIn(View viewToShow, int shortAnimationDuration) {
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        viewToShow.setAlpha(0f);
        viewToShow.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        viewToShow.animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration)
                .setListener(null);
    }

    public static void fadeOut(View viewToRemove, ViewGroup parent, int shortAnimationDuration) {
        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        viewToRemove.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        viewToRemove.setVisibility(View.GONE);
                        parent.removeView(viewToRemove);
                    }
                });
    }

}
