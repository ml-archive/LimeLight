/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * @author Leonard Collins (Fuzz)
 */
public class ManualPositionFrameLayout extends FrameLayout {
    private boolean mDoSuperOnInterceptEvent;

    public ManualPositionFrameLayout(Context context) {
        super(context);
        setClipChildren(false);
    }

    public ManualPositionFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ManualPositionFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return (mDoSuperOnInterceptEvent ? super.onInterceptTouchEvent(ev) : false);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();

        final int parentLeft = 0;
        final int parentRight = right - left;

        final int parentTop = 0;
        final int parentBottom = bottom - top;


        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final MPLayoutParams lp = (MPLayoutParams) child.getLayoutParams();

                final int width = child.getMeasuredWidth();
                final int height = child.getMeasuredHeight();

                int childLeft = lp.absoluteXPosition;
                int childTop = lp.absoluteYPosition;

                int gravity = lp.gravity;
                if (gravity == -1) {
                    gravity = 0;
                } else if (gravity == Gravity.RIGHT) {
                    childLeft -= child.getMeasuredWidth();
                }

                child.layout(childLeft, childTop, childLeft + width, childTop + height);
            }
        }
    }

    protected void setDoSuperOnInterceptEvent(boolean touchBoolean) {
        mDoSuperOnInterceptEvent = touchBoolean;
    }


    public static class MPLayoutParams extends FrameLayout.LayoutParams {
        public int absoluteXPosition;
        public int absoluteYPosition;

        public MPLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public MPLayoutParams(int width, int height) {
            super(width, height);
        }

        public MPLayoutParams(int width, int height, int gravity) {
            super(width, height, gravity);
        }

        public MPLayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public MPLayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }
    }

}
