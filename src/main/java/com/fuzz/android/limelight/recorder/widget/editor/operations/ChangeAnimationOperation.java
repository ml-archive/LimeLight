/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.recorder.widget.editor.operations;

import android.animation.Animator;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.fuzz.android.limelight.R;
import com.fuzz.android.limelight.animation.AnimationHolder;
import com.fuzz.android.limelight.animation.LeftRightAnimation;
import com.fuzz.android.limelight.animation.UpDownAnimation;
import com.fuzz.android.limelight.model.ActToViewHelper;
import com.fuzz.android.limelight.recorder.widget.editor.ActEditor;

/**
 * @author Leonard Collins (Fuzz)
 */
public class ChangeAnimationOperation extends BaseOperation {
    public ChangeAnimationOperation(ActEditor actEditor) {
        super(actEditor);
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {

        final AnimationHolder mAnimation = getEditor().getAnimation();
        final Context context = parent.getContext();

        if (getEditor().getAnimation() != null) {
            mAnimation.stopAnimation();
            getAct().setAnimation(null);
        }
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                String animationName = (String) parent.getAdapter().getItem(position);
                if (!animationName.equalsIgnoreCase(context.getString(R.string.noAnimation))) {
                    String name = view.getContext().getString(R.string.upDownAnimation);

                    if (animationName.equals(name)) {
                        getEditor().setAnimation(new UpDownAnimation());
                    } else {
                        getEditor().setAnimation(new LeftRightAnimation());
                    }
                    AnimationHolder animationHolder = getEditor().getAnimation();
                    getAct().setAnimation(animationHolder);
                    Animator animator = animationHolder.getAnimation();
                    animator.setTarget(getActView());

//                    //TODO: Act Animation
                    animator.start();
                } else {
                    if (mAnimation != null) {
                        mAnimation.stopAnimation();
                        getEditor().setAnimation(null);
                    }
                    Animator anim = ActToViewHelper.getAnimationForAct(getAct());
                    if (anim != null) {
                        anim.cancel();
                    }
                    getAct().setAnimation(null);
                }

                ActToViewHelper.updateView(getActView(), true);
                ((BaseAdapter) parent.getAdapter()).notifyDataSetChanged();
            }
        }, 1000);

    }
}
