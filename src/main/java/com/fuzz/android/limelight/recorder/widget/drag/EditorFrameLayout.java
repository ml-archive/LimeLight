/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.recorder.widget.drag;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fuzz.android.limelight.LimeLight;
import com.fuzz.android.limelight.model.Act;
import com.fuzz.android.limelight.recorder.widget.editor.ActEditor;
import com.fuzz.android.limelight.recorder.widget.snap.SnapHelper;
import com.fuzz.android.limelight.widget.ManualPositionFrameLayout;

/**
 * Layout that holds the ActViews and controller functions that interact with them
 *
 * @author Leonard Collins (Fuzz)
 */
public class EditorFrameLayout extends ManualPositionFrameLayout implements OffSetChangeListener,
        View.OnClickListener {

    private Rect mSnapRect = new Rect();
    private Paint mSnapPaint = new Paint();
    private boolean mEditable;
    private float dX;
    private float dY;

    private TextView mDebugTextView;

    public EditorFrameLayout(Context context) {
        super(context);
        initialize(context, null, -1);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyle) {
        setDoSuperOnInterceptEvent(true);
        mSnapPaint.setColor(0x6600FF00);
        mDebugTextView = new TextView(context);
        mDebugTextView.setTextColor(Color.WHITE);
        mDebugTextView.setLayoutParams(new MPLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        super.addView(mDebugTextView);
    }

    public EditorFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, -1);
    }

    public EditorFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context, attrs, defStyle);
    }

    public void logOnScreen(String log) {
        mDebugTextView.setText(log);
    }

    /**
     * sets editable state of act views
     * @param isEditable
     */
    public void setEditable(boolean isEditable) {
        mEditable = isEditable;
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mEditable) {
            if (mSnapRect != null) {
                canvas.drawRect(mSnapRect, mSnapPaint);
            }
        }
        super.dispatchDraw(canvas);
    }

    /**
     *
     * @param child
     */
    @Override
    public void addView(View child) {
        final DragAndEditView dragAndEditView = new DragAndEditView(getContext(), child);
        MPLayoutParams childLayoutParams = (MPLayoutParams) child.getLayoutParams();
        dragAndEditView.setLayoutParams(childLayoutParams);
        dragAndEditView.setTag(child.getTag());
        dragAndEditView.setId(child.getId());
        dragAndEditView.setOnClickListener(this);
        dragAndEditView.setClipChildren(false);
        super.addView(dragAndEditView);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dragAndEditView.getId() > 0) {
                    snapViewToNearestNeighbor(dragAndEditView);
                } else {
                    mSnapRect = null;
                    invalidate();
                    requestLayout();
                }
            }
        }, 100);

    }

    @Override
    public void removeAllViews() {
        super.removeAllViews();
        super.addView(mDebugTextView);
    }

    public void snapViewToNearestNeighbor(View view) {
        onOffSetChange(view, 0, 0);
    }

    @Override
    public void onOffSetChange(View v, float x, float y) {
        dX += x;
        dY += y;
        invalidate();
        MPLayoutParams viewParams = (MPLayoutParams) v.getLayoutParams();
        viewParams.absoluteXPosition += x;
        viewParams.absoluteYPosition += y;
        mSnapRect = SnapHelper.findNearestNeighborAndUpdateAnchor(v);
        requestLayout();
    }

    @Override
    public void onClick(View v) {
        int displacement = (int) Math.sqrt((dX * dX) + (dY * dY));
        if (displacement < 10) {
            buildAlertDialog(((DragAndEditView)v).getChildAt(0));
        }
        dX = dY = 0;
    }

    /**
     * creates ActEditor alertdialog and attaches it to current ActView
     * @param v
     */
    public void buildAlertDialog(final View v) {

        DisplayMetrics displayMetrics = v.getResources().getDisplayMetrics();
        final int height = displayMetrics.heightPixels / 2;
        final int width = displayMetrics.widthPixels;

        final Act act = (Act) v.getTag();

        final int[] xy = new int[2];

        v.getLocationOnScreen(xy);

        final ActEditor actEditor = new ActEditor(act, v, LimeLight.getActivity(), height, width, height > xy[1]);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (height > xy[1]) {
                    //display popupwindow on bottom half of screen
                    actEditor.showAtLocation(Gravity.NO_GRAVITY, xy[0] + (v.getWidth() / 2),
                            xy[1] + v.getHeight());
                } else {
                    //display popupwindow on top half of screen
                    actEditor.showAtLocation(Gravity.NO_GRAVITY, xy[0] + (v.getWidth() / 2),
                            xy[1] - actEditor.getHeight());
                }
            }
        }, 100L);
    }
}
