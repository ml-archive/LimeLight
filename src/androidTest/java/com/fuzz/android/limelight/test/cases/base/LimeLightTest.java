package com.fuzz.android.limelight.test.cases.base;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.fuzz.android.limelight.LimeLight;
import com.fuzz.android.limelight.R;
import com.fuzz.android.limelight.test.utils.LimeLightFailureHandler;
import com.fuzz.android.limelight.test.utils.TestUtils;
import com.google.android.apps.common.testing.ui.espresso.Espresso;
import com.google.android.apps.common.testing.ui.espresso.ViewInteraction;

import org.hamcrest.Matcher;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.setFailureHandler;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.RootMatchers.withDecorView;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

/**
 * @author James Davis (Fuzz)
 */
public abstract class LimeLightTest <T extends Activity> extends ActivityInstrumentationTestCase2<T> {
    public LimeLightTest(Class<T> activityClass) {
        super(activityClass);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity();
        setFailureHandler(new LimeLightFailureHandler(getInstrumentation().getTargetContext()));
        LimeLight.enableTesting();
        TestUtils.pauseTest(500);
    }

    /**
     *  Allows interactions with views used in the LimeLight interface.
     * @param id The resource id of the view used in LimeLight.
     */
    public static ViewInteraction onLimeLightView(int id){
        ViewInteraction interaction;
        try {
            interaction = onView(withId(id));
            interaction.check(matches(isDisplayed())); //used as a trigger. if interaction is invalid, exception is thrown
        } catch (Throwable thr){
            interaction = onView(withId(id))
                    .inRoot(withDecorView(not(is(LimeLight.getRootView()))));
        }
        return interaction;
    }

    /**
     *  Allows interactions with views used in the LimeLight interface.
     * @param viewMatcher The resource id of the view used in LimeLight.
     */
    public static ViewInteraction onLimeLightView(Matcher<View> viewMatcher){
        return onView(viewMatcher)
                .inRoot(withDecorView(not(is(LimeLight.getRootView()))));
    }

    /**
     *  FIXME: Needs an implementation that isn't dependent on the RecorderWindow's existence
     * @param resourceId The resource ID of the view you're looking for
     * @return The view you're searching for
     */
    public static View findRecorderWindowViewById(int resourceId){
        return LimeLight.getRecorderWindow().getContentView().findViewById(resourceId);
    }

    /**
     *  FIXME: Needs an implementation that isn't dependent on the RecorderWindow's existence
     * @param resourceId The resource ID of the view you're looking for
     * @return The view you're searching for
     */
    public static View findActWindowViewById(int resourceId){
        return LimeLight.getCurrentBook().getWindow().getContentView().findViewById(resourceId);
    }
}
