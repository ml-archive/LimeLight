package com.fuzz.android.limelight.test.page;

import android.test.ActivityInstrumentationTestCase2;

import com.fuzz.android.limelight.R;
import com.fuzz.android.limelight.test.page.base.ActEditorPage;

import static com.fuzz.android.limelight.test.cases.base.LimeLightTest.onLimeLightView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.clearText;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.typeText;

/**
 * @author James Davis (Fuzz)
 */
public class MessageActEditorPage extends ActEditorPage {
    public MessageActEditorPage(ActivityInstrumentationTestCase2 testCase) {
        super(testCase);
    }

    public static MessageActEditorPage launch(ActivityInstrumentationTestCase2 testCase){
        return new MessageActEditorPage(testCase);
    }

    public MessageActEditorPage typeMessage(String message){
        onLimeLightView(R.id.actMessageEdit)
                .perform(clearText(), typeText(message));
        return this;
    }

    public MessageActEditorPage typeDuration(String duration){
        onLimeLightView(R.id.actDurationEdit)
                .perform(clearText(), typeText(duration));
        return this;
    }

    @Override
    public IconGalleryActEditorPage pressNextArrow() {
        super.pressNextArrow();
        return new IconGalleryActEditorPage(getTestCase());
    }

    @Override
    public MessageActEditorPage pressPreviousArrow() {
        super.pressPreviousArrow();
        return this;
    }
}
