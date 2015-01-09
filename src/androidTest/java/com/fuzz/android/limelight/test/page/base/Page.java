package com.fuzz.android.limelight.test.page.base;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import com.fuzz.android.limelight.test.utils.TestUtils;

/**
 * @author James Davis (Fuzz)
 */
public abstract class Page {
    ActivityInstrumentationTestCase2 testCase;

    public Page(ActivityInstrumentationTestCase2 testCase){
        this.testCase = testCase;
        TestUtils.pauseTest(750);
    }

    protected Activity getActivity(){
        return testCase.getActivity();
    }

    protected ActivityInstrumentationTestCase2 getTestCase(){
        return testCase;
    }
}
