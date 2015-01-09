package com.fuzz.android.limelight.test.page;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.fuzz.android.limelight.R;
import com.fuzz.android.limelight.test.cases.base.LimeLightTest;
import com.fuzz.android.limelight.test.page.base.Page;
import com.fuzz.android.limelight.test.utils.TestUtils;
import com.google.android.apps.common.testing.ui.espresso.Espresso;

import junit.framework.AssertionFailedError;

import java.util.Random;

import static com.fuzz.android.limelight.test.cases.base.LimeLightTest.onLimeLightView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;

/**
 * @author James Davis (Fuzz)
 */
public class ColorSelectorPage extends Page{
    public ColorSelectorPage(ActivityInstrumentationTestCase2 testCase) {
        super(testCase);
    }

    public static ColorSelectorPage launch(ActivityInstrumentationTestCase2 testCase){
        return new ColorSelectorPage(testCase);
    }

    public TextActEditorPage cancelDialog(){
        try {
            onLimeLightView(withText("Choose Color")).check(matches(isDisplayed()));
            Espresso.pressBack();
        } catch (Throwable thr){
            throw new AssertionFailedError("There is no dialog to dismiss!");
        }

        return new TextActEditorPage(getTestCase());
    }

    public ColorSelectorPage selectRandomColorHue(){
        // FIXME: Needs to get reference to color hue slider.
//        View hueBar = getActivity().findViewById(R.id.color_hue_selector);
//        TestUtils.clickRandomSpotInView(hueBar, getTestCase());
        return this;
    }

    public ColorSelectorPage selectRandomColorSaturation(){
        // FIXME: Needs to get reference to color saturation selector.
//        View hueBar = getActivity().findViewById(R.id.color_square_view);
//        TestUtils.clickRandomSpotInView(hueBar, getTestCase());
        return this;
    }
}
