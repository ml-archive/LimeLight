package com.fuzz.android.limelight.test.page;

import android.test.ActivityInstrumentationTestCase2;

import com.fuzz.android.limelight.R;
import com.fuzz.android.limelight.test.page.base.Page;
import com.fuzz.android.limelight.test.utils.TestUtils;
import com.google.android.apps.common.testing.ui.espresso.Espresso;

import junit.framework.AssertionFailedError;

import static com.fuzz.android.limelight.test.cases.base.LimeLightTest.onLimeLightView;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.clearText;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;

/**
 * @author James Davis (Fuzz)
 */
public class SaveDialogPage extends Page {
    public SaveDialogPage(ActivityInstrumentationTestCase2 testCase) {
        super(testCase);
    }

    public static SaveDialogPage launch(ActivityInstrumentationTestCase2 testCase){
        return new SaveDialogPage(testCase);
    }

    public RecorderWindowPage saveFile(String filename){

        onLimeLightView(R.id.fileNameEdit)
                .perform(clearText());

        onLimeLightView(R.id.fileNameEdit)
                .perform(typeText(filename));

        onView(withId(android.R.id.button1))
                .perform(click());

        TestUtils.pauseTest(500);

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
