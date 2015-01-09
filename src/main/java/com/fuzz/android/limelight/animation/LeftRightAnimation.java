/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.animation;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;

import com.fuzz.android.limelight.LimeLight;
import com.fuzz.android.limelight.R;

/**
 * @author William Xu (Fuzz)
 */
public class LeftRightAnimation extends AnimationHolder {

    public LeftRightAnimation() {
        animation = new ObjectAnimator();
        animation.setPropertyName("x");
        animation.setFloatValues(0, 15);
        animation.setRepeatMode(ValueAnimator.REVERSE);
        animation.setRepeatCount(ValueAnimator.INFINITE);
    }

    @Override
    public String getAnimationName() {
        return LimeLight.getActivity().getString(R.string.leftRightAnimation);
    }
}
