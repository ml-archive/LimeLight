/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.recorder;

import android.view.View;

/**
 * @author Leonard Collins (Fuzz)
 */
public class RecorderClickListener implements View.OnClickListener {
    private View.OnClickListener mOnClickListener;

    public RecorderClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @Override
    public void onClick(View v) {
        Recorder.recordClick(v);

        mOnClickListener.onClick(v);
    }
}