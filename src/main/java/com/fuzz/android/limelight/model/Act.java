/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.model;

import android.animation.Animator;
import android.graphics.Typeface;
import android.widget.LinearLayout;

import com.fuzz.android.limelight.LimeLight;
import com.fuzz.android.limelight.R;
import com.fuzz.android.limelight.animation.AnimationHolder;

/**
 * @author Leonard Collins (Fuzz)
 */

public class Act {
    protected int mId;
    private String mMessage = LimeLight.getActivity().getString(R.string.act_default_message);
    private int mMessageResID;
    private int mGraphicResID;
    private boolean mIsActionBarItem;
    private double mWidthRatio;
    private double mHeightRatio;
    private String mAnimation;
    private String mActivityName;
    private AnimationHolder animationHolder;
    private int mTextColor;
    private LinearLayout mLayout;

    private Typeface mTypeface;
    private float mTextSize;

    private boolean initialCreation = true;
    private int mTextBackgroundColor;
    private boolean mTransparentBackground = false;

    public Act() {
    }

    public void setId(int id) {
        mId = id;
    }

    public void setIsActionBarItem(boolean isInActionBar) {
        mIsActionBarItem = isInActionBar;
    }

    public void setTransparentBackground(boolean transparent) {
        this.mTransparentBackground = transparent;
    }

    public void setDisplacement(double widthRatio, double heightRatio) {
        mWidthRatio = widthRatio;
        mHeightRatio = heightRatio;
    }

    public String getMessage() {
        if (mMessage == null && mMessageResID != 0) {
            mMessage = LimeLight.getActivity().getString(mMessageResID);
        }
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public int getAnchorViewID() {
        return mId;
    }

    public int getMessageResID() {
        return mMessageResID;
    }

    public void setMessageResID(int messageResID) {
        mMessageResID = messageResID;
    }

    public int getGraphicResID() {
        return mGraphicResID;
    }

    public void setGraphicResID(int resID) {
        mGraphicResID = resID;
    }

    public boolean isInActionBar() {
        return mIsActionBarItem;
    }

    public boolean hasMessage() {
        return mMessage != null;
    }

    public boolean hasTypeface() {
        return mTypeface != null;
    }

    public Typeface getTypeface() {
        return mTypeface;
    }

    public void setTypeface(Typeface typeface) {
        mTypeface = typeface;
    }

    public double getWidthRatio() {
        return mWidthRatio;
    }

    public double getHeightRatio() {
        return mHeightRatio;
    }

    public String getAnimation() {
        return mAnimation;
    }

    public void setAnimation(Object animation) {
        if (animation instanceof AnimationHolder || animation instanceof Animator) {
            mAnimation = animation.getClass().getName();
            if (animation instanceof AnimationHolder) {
                animationHolder = ((AnimationHolder) animation);
            }
        } else if (animation instanceof String) {
            try {
                Class cls = Class.forName(((String) animation));
                animationHolder = (AnimationHolder) cls.newInstance();
                mAnimation = cls.getName();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        } else if (animation instanceof Integer) {
            mAnimation = animation.toString();
        } else if (animation == null) {
            mAnimation = null;
            animationHolder = null;
        } else {
            throw new RuntimeException("animation can only be of type AnimationHolder or Animation or Integer" +
                    "where the integer refers to a value R.anim reference");
        }
    }

    public AnimationHolder getAnimationHolder() {
        return animationHolder;
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float size) {
        mTextSize = size;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int color) {
        mTextColor = color;
    }

    public boolean isInitialCreation() {
        return initialCreation;
    }

    public void setInitialCreation(boolean initialCreation) {
        this.initialCreation = initialCreation;
    }

    public int getTextBackgroundColor() {
        return mTextBackgroundColor;
    }

    public void setTextBackgroundColor(int textBackgroundColor) {
        this.mTextBackgroundColor = textBackgroundColor;
    }

    public boolean hasTransparentBackground() {
        return mTransparentBackground;
    }

    public LinearLayout getLayout() {
        if (mLayout == null) {
            mLayout = ActToViewHelper.toView(this);
        }
        return mLayout;
    }

    public boolean hasLayout() {
        return mLayout != null;
    }

    public String getActivityName() {
        return mActivityName;
    }

    public void setActivityName(String activityName) {
        this.mActivityName = activityName;
    }
}
