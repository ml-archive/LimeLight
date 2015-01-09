package com.fuzz.android.limelight.test.utils;

import android.app.Fragment;
import android.app.Instrumentation;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.view.MotionEvent;
import android.view.View;

import com.fuzz.android.limelight.LimeLight;
import com.fuzz.android.limelight.util.LimeLightLog;
import com.google.android.apps.common.testing.ui.espresso.Espresso;
import com.google.android.apps.common.testing.ui.espresso.EspressoException;
import com.google.android.apps.common.testing.ui.espresso.NoMatchingViewException;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import java.util.Random;

/**
 * @author James Davis (Fuzz)
 */
public class TestUtils {
    public static final int TOAST_LONG_MILLISECONDS = 3500;
    public static final int TOAST_SHORT_MILLISECONDS = 2000;
    private static Random random = new Random();

    public static void pauseTest(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void drag(Point start, Point end, int dragTime, ActivityInstrumentationTestCase2 testCase){
        Instrumentation instrumentation = testCase.getInstrumentation();
        Point size = new Point();
        testCase.getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        int width = size.x;
        int height = size.y;

        if (start.x < 0 || start.y < 0 || start.x > width || start.y > height){
            String error = String.format("The start coordinates (%1$d, %2$d) are not on the screen! | Max width: %3$d, Max height: %4$d", start.x, start.y, size.x, size.y);
            Assert.fail(error);
        }

        Point distance = new Point(end.x - start.x, end.y - start.y);
        if (dragTime <= 0){
            dragTime = 2000;
        }
        float velocity_x = (float) distance.x / dragTime;
        float velocity_y = (float) distance.y / dragTime;
        float x = start.x;
        float y = start.y;

        long downTime = SystemClock.uptimeMillis();
        float lastRecordedTime = downTime;

        instrumentation.sendPointerSync(MotionEvent.obtain(downTime, downTime,
                MotionEvent.ACTION_DOWN, start.x, start.y, 0));

        for (int i = 0; i < dragTime;) {
            float delta = SystemClock.uptimeMillis() - lastRecordedTime;
            lastRecordedTime = SystemClock.uptimeMillis();
            x += velocity_x * delta;
            y += velocity_y * delta;
            instrumentation.sendPointerSync(MotionEvent.obtain(downTime, SystemClock.uptimeMillis(),
                    MotionEvent.ACTION_MOVE, x, y, 0));
            i += delta;
        }

        instrumentation.sendPointerSync(MotionEvent.obtain(downTime, SystemClock.uptimeMillis(),
                MotionEvent.ACTION_UP, end.x, end.y, 0));
    }

    public static void drag(View view, Point end, int dragTime, ActivityInstrumentationTestCase2 testCase){
        int[] viewPosition = new int[2];
        view.getLocationOnScreen(viewPosition);

        Point size = new Point();
        testCase.getActivity().getWindowManager().getDefaultDisplay().getSize(size);

        drag(new Point(viewPosition[0], viewPosition[1]), end, dragTime, testCase);
    }


    public static void click(View view, ActivityInstrumentationTestCase2 testCase){
        int[] viewPosition = new int[2];
        view.getLocationOnScreen(viewPosition);
        click(viewPosition[0], viewPosition[1], testCase);
    }

    public static void click(int x, int y, ActivityInstrumentationTestCase2 testCase){
        Instrumentation instrumentation = testCase.getInstrumentation();
        Point size = new Point();
        testCase.getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        int width = size.x;
        int height = size.y;

        long downTime = SystemClock.uptimeMillis();

        if (x < 0 || y < 0 || x > width || y > height){
            String error = String.format("The start coordinates (%1$d, %2$d) are not on the screen! | Max width: %3$d, Max height: %4$d", x, y, size.x, size.y);
            Assert.fail(error);
        }

        instrumentation.sendPointerSync(MotionEvent.obtain(downTime, SystemClock.uptimeMillis(),
                MotionEvent.ACTION_DOWN, x, y, 0));
        instrumentation.sendPointerSync(MotionEvent.obtain(downTime, SystemClock.uptimeMillis(),
                MotionEvent.ACTION_UP, x, y, 0));
    }

    public static void clickRandomSpotInView(View view, ActivityInstrumentationTestCase2 testCase){
        int[] viewPosition = new int[2];
        view.getLocationOnScreen(viewPosition);
        click(viewPosition[0], viewPosition[1], testCase);
        Rect rect = new Rect();
        view.getHitRect(rect);

        click(viewPosition[0] + random.nextInt(rect.width()), viewPosition[1] + random.nextInt(rect.height()), testCase);
    }
}
