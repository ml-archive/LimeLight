/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.recorder.widget.editor.operations;

import android.graphics.Color;
import android.view.View;

import com.fuzz.android.limelight.R;
import com.fuzz.android.limelight.recorder.widget.color.ColorPickDialog;
import com.fuzz.android.limelight.recorder.widget.color.OnColorChangedListener;
import com.fuzz.android.limelight.recorder.widget.editor.ActEditor;

import static com.fuzz.android.limelight.util.ViewUtils.setTextViewBackground;

/**
 * @author Leonard Collins (Fuzz)
 */
public class ChangeBackgroundColorOperation extends BaseOperation {

    public ChangeBackgroundColorOperation(ActEditor actEditor) {
        super(actEditor);
    }

    @Override
    public void onClick(final View v) {
        ColorPickDialog colorPickDialog =
                new ColorPickDialog(v.getContext(),
                        v.getContext().getString(R.string.COLOR_KEY),
                        getActTextView().getSolidColor(),
                        new OnColorChangedListener() {
                            @Override
                            public void colorChanged(String key, int color) {
                                if (getActTextView() != null) {
                                    if (false) {
                                        getActTextView().setBackgroundColor(Color.TRANSPARENT);
                                    } else {
                                        getActTextView().setBackgroundColor(color);
                                    }
                                    getAct().setTextBackgroundColor(color);
                                    setTextViewBackground((android.widget.TextView) v, color);
                                }
                            }
                        });

        colorPickDialog.setTitle(R.string.pick_color);
        colorPickDialog.show();
    }
}
