package com.fuzz.android.limelight.test.utils;

import android.content.Context;
import android.view.View;

import com.google.android.apps.common.testing.ui.espresso.FailureHandler;
import com.google.android.apps.common.testing.ui.espresso.base.DefaultFailureHandler;

import org.hamcrest.Matcher;

import static com.google.common.base.Throwables.propagate;

/**
 * @author James Davis (Fuzz)
 */
public class LimeLightFailureHandler implements FailureHandler {
    private final FailureHandler delegate;

    public LimeLightFailureHandler(Context targetContext) {
        delegate = new DefaultFailureHandler(targetContext);
    }

    @Override
    public void handle(Throwable error, Matcher<View> matcher) {
        // TODO: Take screenshot before throwing exception
        delegate.handle(error, matcher);
    }
}
