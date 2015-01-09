/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.recorder.widget.snap;

import android.content.res.Resources;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.fuzz.android.limelight.LimeLight;
import com.fuzz.android.limelight.R;
import com.fuzz.android.limelight.model.Act;
import com.fuzz.android.limelight.util.ViewUtils;

import java.util.ArrayList;

/**
 * @author Leonard Collins (Fuzz)
 */
public class SnapHelper {

    private static ArrayList<SnapInfo> cache;
    private static int ACTION_BAR_CONTAINER;
    private static String activityName;
    private static String fragmentTag;

    private static ArrayList<SnapInfo> buildSnapInfoList() {

        if (activityName == null) {
            activityName = LimeLight.getActivity().getClass().getSimpleName();
        } else if (!activityName.equals(LimeLight.getActivity().getClass().getSimpleName())
                && cache != null) {
            cache.clear();
            activityName = LimeLight.getActivity().getClass().getSimpleName();
        }

        if (ViewUtils.hasVisibleFragment()) {
            if (fragmentTag == null) {
                fragmentTag = ViewUtils.getVisibleFragment().getTag();
            } else if (!fragmentTag.equals(ViewUtils.getVisibleFragment().getTag())) {
                cache.clear();
                fragmentTag = ViewUtils.getVisibleFragment().getTag();
            }
        }

        ACTION_BAR_CONTAINER = Resources.getSystem().getIdentifier("action_bar_container", "id", "android");
        ArrayList<SnapInfo> snapInfos = cache = new ArrayList<SnapInfo>();
        buildSnapInfoList(LimeLight.getRootView(), snapInfos, false, 0, 0);

        return cache;
    }

    private static void buildSnapInfoList(View view, ArrayList<SnapInfo> snaps, boolean isInActionBar, int leftOffset, int topOffset) {
        SnapInfo snapInfo = new SnapInfo();
        snapInfo.id = view.getId();
        snapInfo.rect = new Rect();
        view.getHitRect(snapInfo.rect);
        isInActionBar = isInActionBar || view.getId() == ACTION_BAR_CONTAINER;

        snapInfo.rect.offset(leftOffset, topOffset);

        int[] loc = new int[2];
        view.getLocationOnScreen(loc);

        snapInfo.rect.offsetTo(loc[0], loc[1]);

        boolean shouldAdd = !isInActionBar || (isInActionBar && snapInfo.id != -1);

        if (shouldAdd) {
            snapInfo.initialize(isInActionBar);
            snaps.add(snapInfo);
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            View child;
            for (int i = 0; i < group.getChildCount(); i++) {
                child = group.getChildAt(i);
                buildSnapInfoList(child, snaps, isInActionBar, 0, 0);
            }
        }
    }

    static double distanceFromSnapInfo(SnapInfo snapInfo, Rect hitRect) {
        int x0 = snapInfo.rect.left;
        int y0 = snapInfo.rect.top;
        int x1 = hitRect.left;
        int y1 = hitRect.top;
        return Math.sqrt(((x1 - x0) * (x1 - x0)) + ((y1 - y0) * (y1 - y0)));
    }

    public static Rect findNearestNeighborRect(View actView) {
        return findNearestNeighbor(actView).rect;
    }

    public static SnapInfo findNearestNeighbor(View actView) {

        ArrayList<SnapInfo> snapInfos = buildSnapInfoList();

        Rect hitRect = new Rect();
        actView.getHitRect(hitRect);

        double nearestDistance = -1;
        SnapInfo nearestSnap = null;

        if (!snapInfos.isEmpty()) {
            nearestSnap = snapInfos.get(0);
            nearestDistance = distanceFromSnapInfo(nearestSnap, hitRect);
        }

        double distance;
        SnapInfo snapInfo;

        for (int i = 1; i < snapInfos.size(); i++) {
            snapInfo = snapInfos.get(i);
            distance = distanceFromSnapInfo(snapInfo, hitRect);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestSnap = snapInfo;
            }
        }

        return nearestSnap;
    }

    public static Rect findNearestNeighborAndUpdateAnchor(View actView) {
        SnapInfo snapInfo = findNearestNeighbor(actView);
        Act act = (Act) actView.getTag();
        act.setId(snapInfo.id);
        actView.setId(act.getAnchorViewID());

        Rect activeViewDimens = new Rect();
        actView.getHitRect(activeViewDimens);

        double xDisplacement = activeViewDimens.left - snapInfo.rect.left;
        double yDisplacement = activeViewDimens.top - snapInfo.rect.top;

        double widthRatio = xDisplacement / snapInfo.rect.width();
        double heightRatio = yDisplacement / snapInfo.rect.height();
        Log.d("SNAPHELPER", act.getAnchorViewID() + " snapInfo Rect Width:" + snapInfo.rect.width());
        Log.d("SNAPHELPER", act.getAnchorViewID() + " snapInfo Rect Height:" + snapInfo.rect.height());
        Log.d("SNAPHELPER", act.getAnchorViewID() + " snapInfo xDisplacement:" + xDisplacement);
        Log.d("SNAPHELPER", act.getAnchorViewID() + " snapInfo yDisplacement:" + yDisplacement);
        Log.d("SNAPHELPER", act.getAnchorViewID() + " snapInfo xDisplacement after mul:" + widthRatio);
        Log.d("SNAPHELPER", act.getAnchorViewID() + " snapInfo yDisplacement after mul:" + heightRatio);
        act.setDisplacement(widthRatio, heightRatio);

        return snapInfo.rect;
    }
}
