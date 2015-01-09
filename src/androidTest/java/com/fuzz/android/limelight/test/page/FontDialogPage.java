package com.fuzz.android.limelight.test.page;

import android.test.ActivityInstrumentationTestCase2;

import com.fuzz.android.limelight.test.page.base.Page;
import com.google.android.apps.common.testing.ui.espresso.Espresso;

import junit.framework.AssertionFailedError;

import static com.fuzz.android.limelight.test.cases.base.LimeLightTest.onLimeLightView;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onData;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

/**
 * @author James Davis (Fuzz)
 */
public class FontDialogPage extends Page {

    public FontDialogPage(ActivityInstrumentationTestCase2 testCase) {
        super(testCase);
    }

    public static FontDialogPage launch(ActivityInstrumentationTestCase2 testCase){
        return new FontDialogPage(testCase);
    }

    public RecorderWindowPage openFont(String filename){
        onData(allOf(is(instanceOf(String.class)), is(filename)))
                .perform(click());
        return new RecorderWindowPage(getTestCase());
    }

    public RecorderWindowPage cancelDialog(){
        try {
            onLimeLightView(android.R.id.text1); // in simple_list_item_single_choice
            Espresso.pressBack();
        } catch (Throwable thr){
            throw new AssertionFailedError("There is no dialog to dismiss!");
        }
        return new RecorderWindowPage(getTestCase());
    }
}
