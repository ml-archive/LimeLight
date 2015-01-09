/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.recorder.widget.color;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.widget.SeekBar;

/**
 * @author James Davis (Fuzz)
 */
public class ColorHueSlider extends SeekBar {

    GradientDrawable thumb = new GradientDrawable();
    OnColorChangedListener listener;
    private int currentColor;

    public ColorHueSlider(Context context, int color) {
        super(context);

        currentColor = color;

        thumb.setColor(Color.TRANSPARENT);
        thumb.setStroke(6, Color.WHITE);
        thumb.setCornerRadius(6);
        thumb.setSize(30, 60);
        setThumb(thumb);

        setMax(256 * 7 - 1);

        setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int r = 0;
                    int g = 0;
                    int b = 0;

                    if (progress < 256) {
                        b = progress;
                    } else if (progress < 256 * 2) {
                        g = progress % 256;
                        b = 256 - progress % 256;
                    } else if (progress < 256 * 3) {
                        g = 255;
                        b = progress % 256;
                    } else if (progress < 256 * 4) {
                        r = progress % 256;
                        g = 256 - progress % 256;
                        b = 256 - progress % 256;
                    } else if (progress < 256 * 5) {
                        r = 255;
                        g = 0;
                        b = progress % 256;
                    } else if (progress < 256 * 6) {
                        r = 255;
                        g = progress % 256;
                        b = 256 - progress % 256;
                    } else if (progress < 256 * 7) {
                        r = 255;
                        g = 255;
                        b = progress % 256;
                    }

                    currentColor = Color.argb(255, r, g, b);
                    thumb.setColor(currentColor);

                    if (listener != null) {
                        listener.colorChanged("", currentColor);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        LinearGradient gradient =
                new LinearGradient(0.0f, 0.0f, ((float) getWidth()), 0.0f,
                        new int[]{0xFF000000, 0xFF0000FF, 0xFF00FF00, 0xFF00FFFF,
                                0xFFFF0000, 0xFFFF00FF, 0xFFFFFF00, 0xFFFFFFFF},
                        null, Shader.TileMode.CLAMP);
        ShapeDrawable shape = new ShapeDrawable(new RectShape());
        shape.getPaint().setShader(gradient);

        Rect bounds = getProgressDrawable().getBounds();
        setProgressDrawable(shape);
        getProgressDrawable().setBounds(bounds);
    }

    public void setOnColorChangedListener(OnColorChangedListener listener) {
        this.listener = listener;
    }

    public int getCurrentColor() {
        return currentColor;
    }
}
