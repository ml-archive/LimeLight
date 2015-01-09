/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.recorder.widget.snap;

import android.graphics.Rect;

import com.fuzz.android.limelight.util.ViewUtils;

/**
 * @author Leonard Collins (Fuzz)
 */
class SnapInfo {
    public Rect rect;
    public int id;

    public void initialize(boolean isInActionBar) {
        int actionBarHeight = 0;
        actionBarHeight = -ViewUtils.getStatusBarHeight();

        rect.offset(0, actionBarHeight);
    }
}
