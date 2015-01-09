/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.recorder.widget.drag;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.fuzz.android.limelight.animation.AnimationHolder;
import com.fuzz.android.limelight.model.Act;

/**
 * @author Leonard Collins (Fuzz)
 */
class DragAndEditView extends FrameLayout {
    private OffSetChangeListener mOffSetChangeListener;
    private float mInitialX;
    private float mInitialY;
    private Act mAct;
    private AnimationHolder mAnimationHolder;

    public DragAndEditView(Context context) {
        super(context);
    }

    public DragAndEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragAndEditView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DragAndEditView(Context context, View child) {
        super(context);
        if (child.getParent() != null) {
            ((ViewGroup) child.getParent()).removeAllViews();
        }
        addView(child);

        mAct = (Act) child.getTag();
        mAnimationHolder = mAct.getAnimationHolder();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mAct.getAnimationHolder() != null) {
                try {
                    mAct.getAnimationHolder().getAnimation().setRepeatCount(0);
                    mAct.getAnimationHolder().getAnimation().end();
                } catch (Exception e) {

                }
            }
            mInitialX = event.getX();
            mInitialY = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float dx = event.getX() - mInitialX;
            float dy = event.getY() - mInitialY;
            mOffSetChangeListener.onOffSetChange(this, dx, dy);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mAct.getAnimationHolder() != null) {
                try {
                    mAct.getAnimationHolder().getAnimation().setTarget(mAct.getLayout());
                    mAct.getAnimationHolder().getAnimation().setRepeatCount(ValueAnimator.INFINITE);
                    //TODO: Act Animation
                    mAct.getAnimationHolder().getAnimation().start();
                } catch (Exception e) {

                }
            }
        }
        super.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mOffSetChangeListener = (OffSetChangeListener) getParent();
    }
}
