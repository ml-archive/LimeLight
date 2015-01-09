package com.fuzz.android.limelight.test.page;

import android.content.res.TypedArray;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.GridView;

import com.fuzz.android.limelight.R;
import com.fuzz.android.limelight.test.page.base.ActEditorPage;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onData;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;

/**
 * @author James Davis (Fuzz)
 */
public class IconGalleryActEditorPage extends ActEditorPage {

    private TypedArray mIcons;

    public IconGalleryActEditorPage(ActivityInstrumentationTestCase2 testCase) {
        super(testCase);
        mIcons = getActivity().getResources().obtainTypedArray(R.array.act_icons);
    }

    public static IconGalleryActEditorPage launch(ActivityInstrumentationTestCase2 testCase){
        return new IconGalleryActEditorPage(testCase);
    }

    public IconGalleryActEditorPage selectIconAtIndex(int index){
        onData(allOf(is(String.class), is(mIcons.getString(index))))
                .inAdapterView(withId(R.id.menu_2))
                .perform(click());

        return this;
    }

    private void recycleArray(){
        mIcons.recycle();
    }

    @Override
    public RecorderWindowPage pressCancelButton() {
        recycleArray();
        return super.pressCancelButton();
    }

    @Override
    public RecorderWindowPage pressSaveButton() {
        recycleArray();
        return super.pressSaveButton();
    }

    @Override
    public TextActEditorPage pressNextArrow() {
        recycleArray();
        super.pressNextArrow();
        return new TextActEditorPage(getTestCase());
    }

    @Override
    public ActEditorPage pressPreviousArrow() {
        recycleArray();
        super.pressPreviousArrow();
        return new MessageActEditorPage(getTestCase());
    }
}
