package com.flaterlab.parkingapp.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.flaterlab.parkingapp.R;

public class AnimatedRecyclerView extends RecyclerView {

    private Animation slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
    private Animation slideDown = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);

    public AnimatedRecyclerView(@NonNull Context context) {
        super(context);
    }

    public AnimatedRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimatedRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void toggle() {
        if (getVisibility() == INVISIBLE) {
            setVisibility(VISIBLE);
            startAnimation(slideUp);
        } else {
            startAnimation(slideDown);
            setVisibility(INVISIBLE);
        }
    }

    public void setVisible(boolean visible) {
        if (visible && getVisibility() == INVISIBLE) {
            setVisibility(VISIBLE);
            startAnimation(slideUp);
        } else if (!visible && getVisibility() == VISIBLE) {
            startAnimation(slideDown);
            setVisibility(INVISIBLE);
        }
    }
}
