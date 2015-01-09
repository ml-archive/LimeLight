package com.fuzz.android.limelight.test.page;

import android.test.ActivityInstrumentationTestCase2;

import com.fuzz.android.limelight.R;
import com.fuzz.android.limelight.test.page.base.ActEditorPage;

import static com.fuzz.android.limelight.test.cases.base.LimeLightTest.onLimeLightView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;

/**
 * @author James Davis (Fuzz)
 */
public class TextActEditorPage extends ActEditorPage {
    public TextActEditorPage(ActivityInstrumentationTestCase2 testCase) {
        super(testCase);
    }

    public static TextActEditorPage launch(ActivityInstrumentationTestCase2 testCase){
        return new TextActEditorPage(testCase);
    }

    public ColorSelectorPage pressTextColorButton(){
        onLimeLightView(R.id.actColorBtn)
                .perform(click());
        return new ColorSelectorPage(getTestCase());
    }

    public ColorSelectorPage pressBackgroundButton(){
        onLimeLightView(R.id.actColorBackgroundBtn)
                .perform(click());
        return new ColorSelectorPage(getTestCase());
    }

    public TextActEditorPage pressIncreaseTextSizeButton(){
        onLimeLightView(R.id.increaseTextButton)
                .perform(click());
        return this;
    }

    public TextActEditorPage pressDecreaseTextSizeButton(){
        onLimeLightView(R.id.decreaseTextButton)
                .perform(click());
        return this;
    }

    public TextActEditorPage pressTransparentCheckBox(){
        onLimeLightView(R.id.transparentCheckBox)
                .perform(click());
        return this;
    }

    @Override
    public ActEditorPage pressNextArrow() {
        super.pressNextArrow();
        return new AnimationActEditorPage(getTestCase());
    }

    @Override
    public ActEditorPage pressPreviousArrow() {
        super.pressPreviousArrow();
        return new IconGalleryActEditorPage(getTestCase());
    }
}
