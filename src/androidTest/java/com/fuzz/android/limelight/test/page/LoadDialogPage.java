package com.fuzz.android.limelight.test.page;

import android.test.ActivityInstrumentationTestCase2;

import com.fuzz.android.limelight.R;
import com.fuzz.android.limelight.test.page.base.Page;
import com.google.android.apps.common.testing.ui.espresso.Espresso;

import junit.framework.AssertionFailedError;

import static com.fuzz.android.limelight.test.cases.base.LimeLightTest.onLimeLightView;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onData;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasEntry;

/**
 * @author James Davis (Fuzz)
 */
public class LoadDialogPage extends Page {
    public LoadDialogPage(ActivityInstrumentationTestCase2 testCase) {
        super(testCase);
    }

    public static LoadDialogPage launch(ActivityInstrumentationTestCase2 testCase){
        return new LoadDialogPage(testCase);
    }

    public LoadDialogPage goToDefaultFolder(){
        onData(allOf(is(instanceOf(String.class)), is("LimeLight")))
                .perform(click());
        return this;
    }

    public RecorderWindowPage loadFile(String filename){
        onData(allOf(is(instanceOf(String.class)), is(filename)))
                .perform(click());
        return new RecorderWindowPage(getTestCase());
    }

    public StarterWindowPage cancelDialog(){
        try {
            onLimeLightView(R.id.fileHeaderHolder);
            Espresso.pressBack();
        } catch (Throwable thr){
            throw new AssertionFailedError("There is no dialog to dismiss!");
        }

        return new StarterWindowPage(getTestCase());
    }
}
