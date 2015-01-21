/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.model;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuzz.android.limelight.LimeLight;
import com.fuzz.android.limelight.R;
import com.fuzz.android.limelight.animation.AnimationHolder;
import com.fuzz.android.limelight.util.ViewUtils;
import com.fuzz.android.limelight.widget.ManualPositionFrameLayout;

/**
 * @author Leonard Collins (Fuzz)
 */
public class ActToViewHelper {
    /**
     * <p>Responsible for converting an Act into a view that will be places on the LimeLight windows
     * next to the view it is anchored to.</p>
     *
     * @param act
     * @return
     */
    public static LinearLayout toView(Act act) {
        View rootView = LimeLight.getRootView();
        LinearLayout view = new LinearLayout(LimeLight.getActivity());
        view.setOrientation(LinearLayout.VERTICAL);
        int graphicID = act.getGraphicResID();
        if (graphicID != 0) {
            ImageView imageView = createGraphicView(view);
            imageView.setImageResource(graphicID);
        }
        if (!act.hasMessage()) {
            act.setMessage("");
        }

        TextView messageTextView = createMessageTextView(view);
        updateMessageView(messageTextView, act);
        messageTextView.setMaxWidth(450);

        setLayoutParams(act, rootView, view);
        view.setTag(act);
        view.setClipChildren(false);
        return view;
    }

    private static ImageView createGraphicView(ViewGroup parent) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView imageView = new ImageView(LimeLight.getActivity());
        imageView.setId(R.id.actImage);
        parent.addView(imageView, 0, params);
        return imageView;
    }

    private static TextView createMessageTextView(ViewGroup parent) {
        TextView textView = new TextView(LimeLight.getActivity());
        textView.setId(R.id.actText);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setPadding(7, 5, 7, 5);
        params.setMargins(0, 5, 0, 0);
        parent.addView(textView, params);
        return textView;
    }

    private static void updateMessageView(TextView textView, Act act) {
        if (act.hasTypeface()) {
            textView.setTypeface(act.getTypeface());
        }
        textView.setText(act.getMessage());
        textView.setTextColor(act.getTextColor());
        textView.setBackgroundColor(act.hasTransparentBackground() ?
                Color.TRANSPARENT :
                act.getTextBackgroundColor());

        if (act.getTextSize() == 0) {
            act.setTextSize(12);
        }
        textView.setTextSize(act.getTextSize());

    }

    protected static void setLayoutParams(Act act, View rootView, View view) {

        View anchorView = rootView.findViewById(act.getAnchorViewID());

        double widthRatio = act.getWidthRatio();
        double heightRatio = act.getHeightRatio();

        int[] xy = new int[2];
        Rect absolutePositionOnScreen;
        if (anchorView != null) {
            anchorView.getLocationOnScreen(xy);
            absolutePositionOnScreen = ModelHelper.getAbsolutePositionOnScreen(anchorView);
        } else {
            absolutePositionOnScreen = ModelHelper.getAbsolutePositionOnScreen(rootView);
            widthRatio = heightRatio = 0.5;
        }

        // START OF ALIGNMENT CODE
        int x = (int) (xy[0] + (widthRatio * absolutePositionOnScreen.width()));
        int y = (int) (xy[1] + (heightRatio * absolutePositionOnScreen.height()));

        // END OF ALIGNMENT CODE

        int statusBarHeight = 0;
        statusBarHeight += ViewUtils.getStatusBarHeight();

        ManualPositionFrameLayout.MPLayoutParams mpLayoutParams =
                new ManualPositionFrameLayout.MPLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        mpLayoutParams.absoluteXPosition = x;
        mpLayoutParams.absoluteYPosition = y - statusBarHeight;
        view.setLayoutParams(mpLayoutParams);
    }

    public static void updateView(final View view, boolean isChangeAnimation) {
        Act act = (Act) view.getTag();
        view.setId(act.getAnchorViewID());
        ImageView imageView = (ImageView) view.findViewById(R.id.actImage);
        TextView textView = (TextView) view.findViewById(R.id.actText);

        int graphicID = act.getGraphicResID();

        if (graphicID != 0) {
            if (imageView == null) {
                imageView = createGraphicView((ViewGroup) view);
            }
            imageView.setImageResource(graphicID);
        }

        if (!act.hasMessage()) {
            act.setMessage("");
        }

        if (textView == null) {
            textView = createMessageTextView((ViewGroup) view);
        }

        updateMessageView(textView, act);

        final Animator animation = getAnimationForAct(act);
        Animation prevAnim = view.getAnimation();
        if (animation != null && isChangeAnimation) {
            if (prevAnim != null) {
                prevAnim.cancel();
            }
            try {
                animation.start();
            }catch (Throwable t){}
        } else {
            view.clearAnimation();
        }
    }

    public static Animator getAnimationForAct(Act act) {
        Object animation = null;
        Animator animator = null;
        String animationString = null;
        final AnimationHolder animationHolder = act.getAnimationHolder();
        if (animationHolder != null) {
            animation = animationHolder;
            animator = animationHolder.getAnimation();
        } else {
            animationString = act.getAnimation();
        }
        if (animationString != null) {
            int animationID = 0;
            try {
                animationID = Integer.parseInt(animationString);
            } catch (Throwable t) {
            }
            if (animationID != 0) {
                animation = AnimatorInflater.loadAnimator(LimeLight.getActivity(), animationID);
            } else {
                try {
                    Class objectClass = Class.forName(animationString);
                    Object anim = objectClass.newInstance();
                    if (anim instanceof Animator) {
                        animation = (Animator) anim;
                        animator = animationHolder.getAnimation();
                    } else if (anim instanceof AnimationHolder) {
                        animation = ((AnimationHolder) anim);
                        animator = ((AnimationHolder) anim).getAnimation();
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        act.setAnimation(animation);
        return animator;
    }

    public static String getAnimationName(Act act) {
        String animationString = act.getAnimation();
        if (animationString != null) {
            try {
                Class objectClass = Class.forName(animationString);
                Object anim = objectClass.newInstance();
                if (anim instanceof AnimationHolder) {
                    animationString = ((AnimationHolder) anim).getAnimationName();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return animationString;
    }

}
