/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.model;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.fuzz.android.limelight.LimeLight;

/**
 * @author Leonard Collins (Fuzz)
 *         <p>Helper for model package</p>
 */
class ModelHelper {

    public static void performIdentifierAction(int id) {
        LimeLight.getMenu().performIdentifierAction(id, 0);
    }

    public static int getScreenHeight() {
        int screenHeight = LimeLight.getActivity().getResources().getDisplayMetrics().heightPixels;
        return screenHeight;
    }

    public static void openDrawer() {
        if (LimeLight.getDrawerAutomator() != null) {
            LimeLight.getDrawerAutomator().openDrawer();
        }
    }

    public static void closeDrawer() {
        if (LimeLight.getDrawerAutomator() != null) {
            LimeLight.getDrawerAutomator().closeDrawer();
        }
    }

    public static void clickDrawerItem(int position) {
        if (LimeLight.getDrawerAutomator() != null) {
            LimeLight.getDrawerAutomator().clickItem(position);
        }
    }

    public static Rect getAbsolutePositionOnScreen(View v) {
        Rect rect = new Rect();
        v.getHitRect(rect);
        if (v.getClass().getName().contains("ActionMenuItemView")) {
            //TODO Actionbar items will have to be offset to the right, but this is not the right
            //implementation, however, it should work for the first element
            int left = getScreenWidth() - rect.width();
            rect.offsetTo(left, 0);
        }
        return rect;
    }

    public static int getScreenWidth() {
        int screenWidth = LimeLight.getActivity().getResources().getDisplayMetrics().widthPixels;
        return screenWidth;
    }

    public static void scrollViewToPosition(View view, int position) {
        if (view instanceof ListView) {
            ListView listView = ((ListView) view);
            listView.smoothScrollToPosition(position);
        } else {
            Log.w("LimeLight | scrollViewToPosition()", "This view is not a list view. Cannot be scrolled.");
        }
    }

    public static void scrollViewUntilChildIsVisible(View view, View child) {
        float value = child.getY();
        view.scrollTo(0, (int) value);
    }

    public static int getID(String id) {
        String defPackageName = LimeLight.getActivity().getPackageName();
        return LimeLight.getActivity().getResources().getIdentifier(id, "id", defPackageName);
    }

    public static int getResourceIntValue(String id) {
        return LimeLight.getActivity().getResources().getIdentifier(id, null, null);
    }

}
