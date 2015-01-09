package com.fuzz.android.limelight.test.page;

import android.test.ActivityInstrumentationTestCase2;

import com.fuzz.android.limelight.test.page.base.ActEditorPage;

/**
 * @author James Davis (Fuzz)
 */
public class AnimationActEditorPage extends ActEditorPage {
    public AnimationActEditorPage(ActivityInstrumentationTestCase2 testCase) {
        super(testCase);
    }

    public static AnimationActEditorPage launch(ActivityInstrumentationTestCase2 testCase){
        return new AnimationActEditorPage(testCase);
    }

    // TODO: Populate with tests while disabling resulting act animations


    @Override
    public ActEditorPage pressPreviousArrow() {
        super.pressNextArrow();
        return new TextActEditorPage(getTestCase());
    }

    @Override
    public ActEditorPage pressNextArrow() {
        super.pressPreviousArrow();
        return this;
    }
}
