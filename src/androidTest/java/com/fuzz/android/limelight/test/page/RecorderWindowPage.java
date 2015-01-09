package com.fuzz.android.limelight.test.page;

import android.graphics.Point;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.fuzz.android.limelight.R;
import com.fuzz.android.limelight.recorder.RecorderWindow;
import com.fuzz.android.limelight.test.cases.base.LimeLightTest;
import com.fuzz.android.limelight.test.page.base.ActEditorPage;
import com.fuzz.android.limelight.test.page.base.Page;
import com.fuzz.android.limelight.test.utils.TestUtils;
import com.fuzz.android.limelight.util.LimeLightLog;

import static com.fuzz.android.limelight.test.cases.base.LimeLightTest.onLimeLightView;
import static com.fuzz.android.limelight.test.utils.TestUtils.TOAST_LONG_MILLISECONDS;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.scrollTo;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.AllOf.allOf;

/**
 * @author James Davis (Fuzz)
 */
public class RecorderWindowPage extends Page {
    public RecorderWindowPage(ActivityInstrumentationTestCase2 testCase) {
        super(testCase);
        try {
            onLimeLightView(withText(R.string.empty_book_warning)) // Toast after saving an empty book
                .check(matches(isDisplayed()));
            TestUtils.pauseTest(TOAST_LONG_MILLISECONDS + 500);
        } catch (Throwable thr) {}
        finally {
            // check if this is the right page
            onLimeLightView(R.id.recorder_content_view).check(matches(isDisplayed()));
        }
    }

    public static RecorderWindowPage launch(ActivityInstrumentationTestCase2 testCase){
        return new RecorderWindowPage(testCase);
    }

    public RecorderWindowPage pressPlayButton(){
        onLimeLightView(R.id.play).perform(click());
        return this;
    }

    public RecorderWindowPage pressBackButton(){
        onLimeLightView(R.id.back).perform(click());
        return this;
    }

    public RecorderWindowPage pressForwardButton(){
        onLimeLightView(R.id.forward).perform(click());
        return this;
    }

    public RecorderWindowMenuPage pressMenuButton(){
        onLimeLightView(R.id.menu).perform(click());
        return new RecorderWindowMenuPage(getTestCase());
    }

    public RecorderWindowPage pressEditButton(){
        onLimeLightView(R.id.record).perform(click());
        return this;
    }

    public RecorderWindowPage pressAddButton(){
        try {
            onLimeLightView(R.id.add).perform(click());
        } catch (Throwable thr){
            LimeLightLog.e("Add button could not be clicked", thr);
        }
        return this;
    }

    public RecorderWindowPage pressDeleteButton(){
        onLimeLightView(R.id.delete).perform(click());
        return this;
    }

    public FontDialogPage pressFontButton(){
        onLimeLightView(R.id.font).perform(click());
        return new FontDialogPage(getTestCase());
    }

    public RecorderWindowPage pressHideButton(){
        onLimeLightView(R.id.hide_button).perform(click());
        TestUtils.pauseTest(750);
        return this;
    }

    public RecorderWindowPage pressScrubberItemAtPosition(int position){
        // FIXME: Populate this with something meaningful
        return this;
    }

    public RecorderWindowPage pressScrubberItemWithLabel(String label){
        onLimeLightView(allOf(withId(R.id.time_slice), withText(label)))
                .perform(scrollTo(), click());
        return this;
    }

    public MessageActEditorPage pressAct(){
        TestUtils.pauseTest(250);
        View actImage = LimeLightTest.findActWindowViewById(R.id.actImage);
        TestUtils.click(actImage, getTestCase());
        return new MessageActEditorPage(getTestCase());
    }

    public RecorderWindowPage dragActTo(int x, int y){
        View actImage = LimeLightTest.findActWindowViewById(R.id.actImage);
        TestUtils.drag(actImage, new Point(x, y), 2000, getTestCase());
        return this;
    }

    public RecorderWindowPage dragActTo(int x, int y, int milliseconds){
        View actImage = LimeLightTest.findActWindowViewById(R.id.actImage);
        TestUtils.drag(actImage, new Point(x, y), milliseconds, getTestCase());
        return this;
    }

    public RecorderWindowPage dragWindowTo(int x, int y, int milliseconds){
        View hideButton = LimeLightTest.findRecorderWindowViewById(R.id.hide_button);
        TestUtils.drag(hideButton, new Point(x, y), milliseconds, getTestCase());
        return this;
    }

    public RecorderWindowPage dragWindowTo(int x, int y){
        View hideButton = LimeLightTest.findRecorderWindowViewById(R.id.hide_button);
        TestUtils.drag(hideButton, new Point(x, y), 2000, getTestCase());
        return this;
    }
}
