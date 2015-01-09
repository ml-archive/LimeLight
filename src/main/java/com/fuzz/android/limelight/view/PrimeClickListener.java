/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.view;

import android.view.View;

import com.fuzz.android.limelight.LimeLight;
import com.fuzz.android.limelight.util.ViewUtils;

/**
 * @author Leonard Collins (Fuzz)
 */
public class PrimeClickListener implements View.OnClickListener {
    private View.OnClickListener mOnClickListener;

    public PrimeClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @Override
    public void onClick(View v) {
        if (LimeLight.getCurrentBook().getCurrentChapter().getTime() == 0) {
            if (ViewUtils.isWaitingForActionOnViewWithId(v.getId())) {
                LimeLight.next();
            }
        }
        mOnClickListener.onClick(v);
    }
}
