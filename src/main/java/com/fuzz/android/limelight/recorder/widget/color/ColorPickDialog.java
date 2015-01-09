/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.recorder.widget.color;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.fuzz.android.limelight.R;

/**
 * The custom color picker dialog that lets the user select colors to use for
 * customization purposes.
 *
 * @author William Xu (Fuzz)
 */
public class ColorPickDialog extends AlertDialog {

    private static final int PADDING_DP = 20;

    private static final int CONTROL_SPACING_DP = 20;
    private static final int SELECTED_COLOR_HEIGHT_DP = 50;
    private static final int BORDER_DP = 1;
    private static final int BORDER_COLOR = Color.BLACK;

    private OnColorChangedListener listener;
    private ColorSquareView colorSquareView;
    private ColorHueSlider colorHueSlider;
    private View selectedColorView;
    private EditText hexCodeEdit;
    private int selectedColor;
    private int initialColor;

    private String key;
    private OnClickListener clickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
                case BUTTON_POSITIVE:
                    listener.colorChanged(key, selectedColor);
                    break;
            }
        }
    };

    public ColorPickDialog(Context context, String key, int initialColor, final OnColorChangedListener listener) {
        super(context);
        this.selectedColor = initialColor;
        this.initialColor = initialColor;
        this.listener = listener;
        this.key = key;

        View root = getLayoutInflater().inflate(R.layout.color_picker_dialog, null);
        final View currentColorView = root.findViewById(R.id.currentColorView);
        final View initialColorView = root.findViewById(R.id.previousColorView);
        final EditText hextEdit = ((EditText) root.findViewById(R.id.hexEdit));

        currentColorView.setBackgroundColor(initialColor);
        initialColorView.setBackgroundColor(initialColor);
        hextEdit.setText(getHexCodeString(initialColor));

        OnColorChangedListener l = new OnColorChangedListener() {
            @Override
            public void colorChanged(String key, int color) {
                selectedColor = color;
                currentColorView.setBackgroundColor(color);
                hextEdit.setText(getHexCodeString(color));
            }
        };

        hextEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (validateHexCode(s.toString())) {
                    int color = Color.parseColor(("#FF" + s.toString()));
                    currentColorView.setBackgroundColor(color);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(6);
        hextEdit.setFilters(filterArray);

        colorHueSlider = new ColorHueSlider(context, initialColor);
        colorSquareView = new ColorSquareView(context, l, colorHueSlider, initialColor, 0);

        RelativeLayout colorSquare = (RelativeLayout) root.findViewById(R.id.color_square_view);
        RelativeLayout colorSlider = (RelativeLayout) root.findViewById(R.id.color_hue_selector);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        colorSquare.addView(colorSquareView, lp);

        lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        colorSlider.addView(colorHueSlider, lp);

        setButton(BUTTON_NEGATIVE, context.getString(android.R.string.cancel), clickListener);
        setButton(BUTTON_POSITIVE, context.getString(android.R.string.ok), clickListener);

        setView(root, 20, 20, 20, 20);
    }

    /**
     * @param color the component value of a color to be converted
     * @return the determined hex code of color
     */
    private String getHexCodeString(int color) {
        String hexCode = Integer.toHexString(color).toUpperCase();
        if (hexCode.equalsIgnoreCase("0")) {
            hexCode = "FF000000";
        }
        hexCode = hexCode.substring(2);
        return hexCode;
    }

    /**
     * @param hexCode color hex code to be evaluated
     * @return true hexCode is evaluated to be in correct formatting
     */
    private boolean validateHexCode(String hexCode) {
        boolean valid = false;
        try {
            if (hexCode.length() == 6) {
                Long.parseLong("FF" + hexCode, 16);
                valid = true;
            }
        } catch (Throwable thr) {
            valid = false;
        }
        return valid;
    }
}
