/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.recorder.widget.color;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author James Davis (Fuzz)
 */
public class ColorSquareView extends View {
    private static final float HUE_BAR_HEIGHT = 10;
    ColorHueSlider slider;
    private Paint mPaint;
    private float mCurrentHue = 0;
    private int mCurrentX = 0, mCurrentY = 0;
    private int mCurrentColor, mDefaultColor;
    private int[] mMainColors = new int[65536];
    private Shader[] mShaders = new Shader[256];
    private OnColorChangedListener mListener;
    private int mHeight = 0;

    public ColorSquareView(Context c, OnColorChangedListener l, ColorHueSlider slider, int color, int defaultColor) {
        super(c);
        mListener = l;
        mDefaultColor = defaultColor;

        this.slider = slider;
        slider.setOnColorChangedListener(new OnColorChangedListener() {
            @Override
            public void colorChanged(String key, int color) {
                float[] hsv = new float[3];
                Color.colorToHSV(color, hsv);
                mCurrentHue = hsv[0];
                updateMainColors();
                placeCircleAtPosition(mCurrentX, mCurrentY);
                invalidate();
            }
        });

        // Get the current hue from the current color and update the main color field
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        mCurrentHue = hsv[0];
        updateMainColors();

        mCurrentColor = color;

        // Initializes the Paint that will draw the View
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(12);
    }

    // Update the main field colors depending on the current selected hue
    private void updateMainColors() {
        int mainColor = slider.getCurrentColor();
        int index = 0;
        int[] topColors = new int[256];
        for (int y = 0; y < 256; y++) {
            for (int x = 0; x < 256; x++) {
                if (y == 0) {
                    mMainColors[index] = Color.rgb(255 - (255 - Color.red(mainColor)) * x / 255, 255 - (255 - Color.green(mainColor)) * x / 255, 255 - (255 - Color.blue(mainColor)) * x / 255);
                    topColors[x] = mMainColors[index];
                } else
                    mMainColors[index] = Color.rgb((255 - y) * Color.red(topColors[x]) / 255, (255 - y) * Color.green(topColors[x]) / 255, (255 - y) * Color.blue(topColors[x]) / 255);
                index++;
            }
        }

        updateShaders();
    }

    private void placeCircleAtPosition(int x, int y) {
        float scale_w = 256f / getWidth();
        float scale_h = 256f / getHeight();

        // If the touch event is located in the main field
        if (x > 0 && x < getWidth() && y > 0 && y < getHeight()) {
            mCurrentX = x;
            mCurrentY = y;
            int transX = (int) (mCurrentX * scale_w);
            int transY = (int) (mCurrentY * scale_h);
            int index = 256 * (transY - 1) + transX;
            if (index > 0 && index < mMainColors.length) {
                // Update the current color
                mCurrentColor = mMainColors[index];
                // Force the redraw of the dialog
                invalidate();
            }

            mListener.colorChanged("", mCurrentColor);
        }
    }

    private void updateShaders() {
        if (mHeight > 0) {
            for (int i = 0; i < 256; i++) {
                int[] colors = new int[2];
                colors[0] = mMainColors[i];
                colors[1] = Color.BLACK;
                mShaders[i] = new LinearGradient(0, 0, 0, mHeight, colors, null, Shader.TileMode.MIRROR);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();

                placeCircleAtPosition((int) x, (int) y);

                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float width = canvas.getWidth() / 256f;

        if (mHeight <= 0) {
            mHeight = canvas.getHeight();
            updateShaders();
        }

        // Display the main field colors using LinearGradient
        for (int x = 0; x < 256; x++) {
            mPaint.setShader(mShaders[x]);
            mPaint.setStrokeWidth(width + 1);
            canvas.drawLine(x * width, 0, (x * width) + width, canvas.getHeight(), mPaint);
        }

        mPaint.setShader(null);

        // Display the circle around the currently selected color in the main field
        if (mCurrentX >= 0 && mCurrentY >= 0) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.BLACK);
            mPaint.setStrokeWidth(3);
            canvas.drawCircle(mCurrentX, mCurrentY, 10, mPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setCurrentColor(int color) {
        mCurrentColor = color;

    }
}
