/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.fuzz.android.limelight.automate.DrawerAutomator;
import com.fuzz.android.limelight.model.Book;
import com.fuzz.android.limelight.recorder.Recorder;
import com.fuzz.android.limelight.recorder.RecorderWindow;
import com.fuzz.android.limelight.util.LimeLightLog;

import java.util.ArrayList;

/**
 * @author Leonard Collins (Fuzz)
 *         <p>Public interface</p>
 */


final public class LimeLight {

    final static int HINT = 0;
    final static int TUTORIAL = 1;
    final static int OFF = 2;

    final static int HELP_MENU_ID = 0x98324;

    static Activity sCurrentActivity;
    static Menu sMenu;
    static ListView sDrawerList;
    static int      sMode;
    static DrawerLayout.DrawerListener sListener;
    static boolean firstFocus = true;
    static RecorderWindow mRecorderWindow;
    private static boolean mIsMenuMode = true;
    private static DrawerAutomator sDrawerAutomator;
    static private Book book;
    static ArrayList<String> mFragmentTags = new ArrayList<String>();

    private static boolean sIsTesting = false;

    public static Activity getActivity() {
        return sCurrentActivity;
    }

    public static void onCreateOptionsMenu(Activity activity, Menu menu) {
        sMenu = menu;
    }

    static public void play() {
        if (book == null) {
            book = getCurrentBook();
            mRecorderWindow.setMainWindow(book.getWindow());
        } else {
            book.reset();
        }
        book.read();
    }

    static public RecorderWindow getRecorderWindow(){
        return mRecorderWindow;
    }

    static public void onResume(Activity activity) {
        LimeLightLog.d("Resuming Activity");

        if (sCurrentActivity != activity){
            sCurrentActivity = activity;
        }
        mRecorderWindow = new RecorderWindow(activity);
    }

    //TODO: NEEDS REFACTOR
    public static void onWindowFocusChanged(Activity activity, boolean hasFocus) {
        if (hasFocus) {
            if (firstFocus) {
                firstFocus = false;
                if (mRecorderWindow == null) {
                    onResume(activity);
                }
                if (book != null) {
                    mRecorderWindow.setMainWindow(book.getWindow());

                    PopupWindow popupWindow = book.getWindow();
                    if (popupWindow != null) {
                        Context context = popupWindow.getContentView().getContext();

                        if (context != activity) {
                            createDelayedRecordingWindow(activity, 200);
                        }
                    } else {
                        createDelayedRecordingWindow(activity, 200);
                    }
                } else {
                    createDelayedRecordingWindow(activity, 200);
                }
            }
        }
    }

    private static void createDelayedRecordingWindow(Activity activity, int delay) {
        activity.getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (book != null) {
                    book.refreshWindow();
                }
                if (!mRecorderWindow.isShowing()) {
                    mRecorderWindow.show(getRootView());
                }
                updateScrubber(View.FOCUS_RIGHT);
                LimeLightLog.i("Created recording window.");
            }
        }, delay);
    }

    static public PopupWindow createRecordingWindow(Activity activity) {
        if (activity != null) {
            mRecorderWindow = new RecorderWindow(activity);
        }
        if (!mRecorderWindow.isShowing()){
            mRecorderWindow.show(getRootView());
        }
        updateScrubber(View.FOCUS_RIGHT);
        return mRecorderWindow;
    }

    private static void updateScrubber() {
        updateScrubber(-1);
    }

    static public void resetRecorderWindow(){
        if (mRecorderWindow != null){
            mRecorderWindow.resetButtons(true);
            mRecorderWindow.updateScrubber();
            mRecorderWindow.focusOnCurrentScrubberView();
        }
    }

    static public void updateScrubber(int scrollPageDirection){
        if (mRecorderWindow != null){
            mRecorderWindow.updateScrubber(scrollPageDirection);
        }
    }

    public static boolean isRecorderBuild() {
        return true;
    }

    static public void destroyRecordingWindow() {
        if (mRecorderWindow != null) {
            mRecorderWindow.dismiss();
            mRecorderWindow = null;
        }
    }

    static public void onPause(Activity activity) {
        LimeLightLog.d("Pausing Activity");
        sCurrentActivity = null;
        firstFocus = true;
        destroyRecordingWindow();
        Book book = LimeLight.getCurrentBook();
        if (book != null) {
            book.destroyPopupWindow();
        }
    }

    public static View getRootView() {
        return sCurrentActivity.getWindow().getDecorView();
    }

    public static View getRootView(Activity activity) {
        View view = null;
        try {
            view = activity.getWindow().getDecorView();
        } catch (NullPointerException npe){
            LimeLightLog.e("The provided activity was null");
        }
        return view;
    }

    public static void runOnUIThread(Runnable runnable) {
        sCurrentActivity.runOnUiThread(runnable);
    }

    public static void turnOnHintMode(){
        sMode = HINT;
        Recorder.replaceListeners(getRootView());
    }

    public static void turnOnTutorialMode(){
        sMode = TUTORIAL;
    }

    public static void turnOff(){
        sMode = OFF;
    }

    public static Menu getMenu() {
        return sMenu;
    }

    public static ListView getDrawerList() {
        return sDrawerList;
    }

    public static void setDrawerList(ListView listView) {
        sDrawerList = listView;
    }

    public static boolean isMenuMode() {
        return mIsMenuMode;
    }

    public static boolean isHintModeOn(){
        return sMode == HINT;
    }

    public static boolean isTutorialModeOn(){
        return sMode == TUTORIAL;
    }

    public static boolean isOff(){
        return sMode == OFF;
    }

    public static void setMenuMode(boolean isMenuMode) {
        mIsMenuMode = isMenuMode;
    }

    public static DrawerLayout.DrawerListener setDrawerListener(
            final DrawerLayout.DrawerListener listener) {
        sListener = new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View view, float v) {
                listener.onDrawerSlide(view, v);
            }

            @Override
            public void onDrawerOpened(View view) {
                listener.onDrawerOpened(view);
            }

            @Override
            public void onDrawerClosed(View view) {
                listener.onDrawerClosed(view);
            }

            @Override
            public void onDrawerStateChanged(int i) {
                listener.onDrawerStateChanged(i);
            }
        };
        return sListener;
    }

    public static DrawerAutomator getDrawerAutomator() {
        return sDrawerAutomator;
    }

    public static void setDrawerAutomator(DrawerAutomator drawerAutomator) {
        sDrawerAutomator = drawerAutomator;
    }

    public static void next() {
        book.read();
    }

    public static Book getCurrentBook() {
        return book;
    }

    public static void setCurrentBook(Book newBook) {
        book = newBook;
    }

    public static Context getContext(){
        Context context = null;
        if (sCurrentActivity != null){
            context = sCurrentActivity.getApplication();
        }
        return context;
    }

    public static void enableTesting() {
        sIsTesting = true;
    }

    public static boolean isTesting(){
        return sIsTesting;
    }

    public static void addFragmentTag(String tag) {
        mFragmentTags.add(tag);
    }

    public static ArrayList<String> getFragmentTags() {
        return mFragmentTags;
    }
}
