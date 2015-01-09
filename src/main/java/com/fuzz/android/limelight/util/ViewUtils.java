/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.util;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.fuzz.android.limelight.LimeLight;
import com.fuzz.android.limelight.model.Chapter;

import java.util.ArrayList;

/**
 * Holds methods needed to calculate and return values of specific view components. Also holds
 * methods needed to find or evaluate other traits related to views.
 *
 * @author William Xu (Fuzz)
 */
public class ViewUtils {

    static int statusBarHeight = -1;

    /**
     * @param px pixel value to be converted
     * @return dp value converted from px
     */
    public static float dpize(float px) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        float dp = px / (displayMetrics.densityDpi / 160f);
        return dp;
    }

    /**
     * @param dp density independent pixel to be converted
     * @return px value converted from dp
     */
    public static float pixelize(float dp) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
        return px;
    }

    /**
     * @return the action bar's height as an integer pixel
     */
    public static int getActionBarHeight() {
        Activity context = LimeLight.getActivity();
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
                    context.getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    /**
     * @return the status bar's height
     */
    public static int getStatusBarHeight() {
        if (statusBarHeight == -1) {
            Rect rect = new Rect();
            Window window = LimeLight.getActivity().getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(rect);
            statusBarHeight = rect.top;
        }
        return statusBarHeight;
    }

    /**
     * @return the navigation bar's height in px
     */
    public static int getNavigationBarHeightInPixel() {
        return (int) ViewUtils.pixelize(getNavigationBarHeight());
    }

    /**
     * @return the navigation bar's height in dp
     */
    public static int getNavigationBarHeight() {
        int height = 0;
        Activity context = LimeLight.getActivity();
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = resources.getDimensionPixelSize(resourceId);
        }

        return height;
    }

    /**
     * @param id view id to be evaluated
     * @return true if current act's anchor view id matches given id
     */
    public static boolean isWaitingForActionOnViewWithId(int id) {
        Chapter chapter = LimeLight.getCurrentBook().getCurrentChapter();
        boolean isWaiting = false;
        if (chapter != null) {
            if (chapter.getAct().getAnchorViewID() == id) {
                isWaiting = true;
            }
        }
        return isWaiting;
    }

    /**
     * @param fileName the file name string to be evaluated
     * @return true if filename does not contain any of the set of reserved characters
     */
    public static boolean isFileNameValid(String fileName) {
        String[] reservedCharacters = {"|", "\\", "?", "*", "<", "\"", ":", ">"};

        for (String c : reservedCharacters) {
            if (fileName.contains(c)) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param parent the parent view to be evaluated
     * @param childView the child view to be evaluated
     * @return true if childView is determined to be in the bounds of the parent
     */
    public static boolean isViewShownInParent(ViewGroup parent, View childView){
        boolean isShown = false;
        try {
            Rect parentBounds = new Rect();
            parent.getHitRect(parentBounds);
            isShown = childView.getLocalVisibleRect(parentBounds);
        } catch (NullPointerException npe){
            if (parent == null){
                LimeLightLog.e("Parent is null");
            }
            if (childView == null){
                LimeLightLog.e("Child is null");
            }
        }

        return isShown;
    }

    /**
     * @return true if current device has physical navigation buttons
     */
    public static boolean deviceHasPhysicalNavigationButtons(){
        boolean hasMenuKey = ViewConfiguration.get(LimeLight.getContext()).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        return hasMenuKey && hasBackKey;
    }

    public static void setTextViewBackground(TextView textView, int color) {
        if (textView != null) {
            textView.setBackgroundColor(color);

            float brightness = getPerceivedBrightness(color);

            textView.setTextColor(brightness > 0.7f ? Color.BLACK : Color.WHITE);
        }
    }

    private static float getPerceivedBrightness(int color) {
        float red, blue, green;
        float brightness = 0;

        if (color != Color.TRANSPARENT) {
            red = Color.red(color) / 255f;
            blue = Color.blue(color) / 255f;
            green = Color.green(color) / 255f;

            // Formula found at http://alienryderflex.com/hsp.html
            brightness += 0.299f * red * red;
            brightness += 0.587f * green * green;
            brightness += 0.114f * blue * blue;
            brightness = ((float) Math.sqrt(brightness));
        } else {
            brightness = 1f;
        }

        return brightness;
    }

    public static Fragment getVisibleFragment() {
        Fragment fragment = null;
        Fragment visibleFragment = null;
        FragmentManager fragmentManager = LimeLight.getActivity().getFragmentManager();

        ArrayList<String> tags = LimeLight.getFragmentTags();
        for (String tag : tags) {
            fragment = fragmentManager.findFragmentByTag(tag);

            if (fragment != null && fragment.isVisible()) {
                visibleFragment = fragment;
            }
        }

        return visibleFragment;
    }

    public static boolean hasVisibleFragment() {
        boolean fragmentVisible = false;
        Fragment fragment = getVisibleFragment();

        if (fragment != null) {
            fragmentVisible = true;
        }

        return fragmentVisible;
    }
}
