/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.animation;

import android.animation.ObjectAnimator;

/**
 * @author James Davis (Fuzz)
 */
public abstract class AnimationHolder {
    protected ObjectAnimator animation;

    public abstract String getAnimationName();

    public ObjectAnimator getAnimation() {
        return animation;
    }

    public void stopAnimation() {
        if (animation != null) {
            animation.setRepeatCount(0);
            animation.end();
        }
    }
}
