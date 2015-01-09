package com.fuzz.android.limelight.test.page.base;

import android.test.ActivityInstrumentationTestCase2;

import com.fuzz.android.limelight.R;
import com.fuzz.android.limelight.test.page.RecorderWindowPage;
import com.fuzz.android.limelight.test.utils.TestUtils;
import com.google.android.apps.common.testing.ui.espresso.EspressoException;
import com.google.android.apps.common.testing.ui.espresso.NoMatchingViewException;

import junit.framework.Assert;

import static com.fuzz.android.limelight.test.cases.base.LimeLightTest.onLimeLightView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;

/**
 * @author James Davis (Fuzz)
 */
public abstract class ActEditorPage extends Page {
    public ActEditorPage(ActivityInstrumentationTestCase2 testCase) {
        super(testCase);
    }

    public RecorderWindowPage pressCancelButton(){
        try {
            onLimeLightView(R.id.cancel)
                    .perform(click());
        } catch (NoMatchingViewException ex){
            Assert.fail("Cancel button was not found. Did you press the 'edit' button before pressing the Act?");
        }
        return new RecorderWindowPage(getTestCase());
    }

    public RecorderWindowPage pressSaveButton(){
        try {
            onLimeLightView(R.id.save)
                    .perform(click());
        } catch (NoMatchingViewException ex){
            Assert.fail("Save button was not found. Did you press the 'edit' button before pressing the Act?");
        }
        return new RecorderWindowPage(getTestCase());
    }

    public ActEditorPage pressNextArrow(){
        try {
            onLimeLightView(R.id.next_menu)
                    .perform(click());
        } catch (NoMatchingViewException ex){
            Assert.fail("Next arrow was not found. Did you press the 'edit' button before pressing the Act?");
        }
        return this;
    }

    public ActEditorPage pressPreviousArrow(){
        try {
            onLimeLightView(R.id.previous_menu)
                    .perform(click());
        } catch (NoMatchingViewException ex){
            Assert.fail("Next arrow was not found. Did you press the 'edit' button before pressing the Act?");
        }
        return this;
    }

}
