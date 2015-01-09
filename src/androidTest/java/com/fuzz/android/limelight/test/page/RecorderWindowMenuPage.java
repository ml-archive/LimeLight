package com.fuzz.android.limelight.test.page;

import android.test.ActivityInstrumentationTestCase2;

import com.fuzz.android.limelight.R;
import com.fuzz.android.limelight.test.page.base.Page;
import com.google.android.apps.common.testing.ui.espresso.Espresso;

import junit.framework.AssertionFailedError;

import static com.fuzz.android.limelight.test.cases.base.LimeLightTest.onLimeLightView;
import static com.google.android.apps.common.testing.testrunner.util.Checks.checkNotNull;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;

/**
 * @author James Davis (Fuzz)
 */
public class RecorderWindowMenuPage extends Page {
    public RecorderWindowMenuPage(ActivityInstrumentationTestCase2 testCase) {
        super(testCase);
    }

    public static RecorderWindowMenuPage launch(ActivityInstrumentationTestCase2 testCase){
        return new RecorderWindowMenuPage(testCase);
    }

    // TODO: Find good way to handle "back to starting menu"

    public RecorderWindowPage pressCancel(){
        onLimeLightView(R.id.cancelButton)
                .perform(click());
        return new RecorderWindowPage(getTestCase());
    }

    public SaveDialogPage pressSaveProgress(){
        onLimeLightView(R.id.saveProgressButton)
                .perform(click());
        return new SaveDialogPage(getTestCase());
    }

    public RecorderWindowPage cancelDialog() {
        try {
            onLimeLightView(R.id.dialog_menu_layout);
            Espresso.pressBack();
        } catch (Throwable thr){
            throw new AssertionFailedError("There is no dialog to dismiss!");
        }
        return new RecorderWindowPage(getTestCase());
    }
}
