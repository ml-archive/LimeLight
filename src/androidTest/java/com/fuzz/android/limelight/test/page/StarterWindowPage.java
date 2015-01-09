package com.fuzz.android.limelight.test.page;

import android.test.ActivityInstrumentationTestCase2;

import com.fuzz.android.limelight.R;
import com.fuzz.android.limelight.test.page.base.Page;
import com.fuzz.android.limelight.test.utils.TestUtils;

import static com.fuzz.android.limelight.test.cases.base.LimeLightTest.onLimeLightView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.*;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.*;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.*;

/**
 * @author James Davis (Fuzz)
 */
public class StarterWindowPage extends Page {
    public StarterWindowPage(ActivityInstrumentationTestCase2 testCase) {
        super(testCase);
        // check if this is the right page
        onLimeLightView(R.id.starter_menu_container).check(matches(isDisplayed()));
    }

    public static StarterWindowPage launch(ActivityInstrumentationTestCase2 testCase) {
        return new StarterWindowPage(testCase);
    }

    /**
     *  clicks the hide button
     *  <br />
     *  <b>Related to Test Case #11: User can hide the menu</b>
     * @return the current Starter Window Page
     */
    public StarterWindowPage pressHideButton(){
        onLimeLightView(R.id.hide_button).perform(click());
        TestUtils.pauseTest(750);
        return this;
    }

    public RecorderWindowPage pressNewButton(){
        onLimeLightView(R.id.new_book).perform(click());
        return new RecorderWindowPage(getTestCase());
    }

    public LoadDialogPage pressLoadButton(){
        onLimeLightView(R.id.load).perform(click());
        return new LoadDialogPage(getTestCase());
    }
}
