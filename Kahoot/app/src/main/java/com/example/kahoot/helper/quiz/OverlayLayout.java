package com.example.kahoot.helper.quiz;

import android.animation.LayoutTransition;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.kahoot.R;


public class OverlayLayout {
    private final FrameLayout overlayOuterLayout;
    public OverlayCallback closeOverlayCallback = view -> {

    };

    public OverlayLayout(FrameLayout overlayOuterLayout) {
        this.overlayOuterLayout = overlayOuterLayout;
    }

    public void show(String message) {
        show(message, null, null);
    }

    public void show(String message, OverlayCallback callback) {
        show(message, callback, view -> hide());
    }

    public void show(String message, OverlayCallback callback, OverlayCallback closeOverlayCallback) {
        ((TextView) overlayOuterLayout.findViewById(R.id.overlayMessage)).setText(message);
        ConstraintLayout overlayLayout = (ConstraintLayout) overlayOuterLayout.findViewById(R.id.overlayInnerLayout);
        overlayLayout.setVisibility(View.VISIBLE);
        LayoutTransition layoutTransition = overlayOuterLayout.getLayoutTransition();
        for (LayoutTransition.TransitionListener transitionListener : layoutTransition.getTransitionListeners()) {
            layoutTransition.removeTransitionListener(transitionListener);
        }
        layoutTransition.addTransitionListener(new LayoutTransition.TransitionListener() {
            @Override
            public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {

            }

            @Override
            public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                if (view.getId() == overlayLayout.getId() && transitionType == LayoutTransition.APPEARING) {
                    if (callback != null) {
                        callback.onComplete(overlayOuterLayout);
                    }
                }
            }
        });
        ((ImageButton) overlayOuterLayout.findViewById(R.id.overlayHideButton)).setOnClickListener(v -> hide(closeOverlayCallback));
    }

    public void hide() {
        hide(null);
    }

    public void hide(OverlayCallback callback) {
        ConstraintLayout overlayLayout = (ConstraintLayout) overlayOuterLayout.findViewById(R.id.overlayInnerLayout);
        overlayLayout.setVisibility(View.GONE);
        LayoutTransition layoutTransition = overlayOuterLayout.getLayoutTransition();
        for (LayoutTransition.TransitionListener transitionListener : layoutTransition.getTransitionListeners()) {
            layoutTransition.removeTransitionListener(transitionListener);
        }
        layoutTransition.addTransitionListener(new LayoutTransition.TransitionListener() {
            @Override
            public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {

            }

            @Override
            public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                if (view.getId() == overlayLayout.getId() && transitionType == LayoutTransition.DISAPPEARING) {
                    if (callback != null) {
                        callback.onComplete(overlayOuterLayout);
                    }
                }
            }
        });
    }
}
