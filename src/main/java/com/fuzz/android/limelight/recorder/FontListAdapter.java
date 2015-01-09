/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.recorder;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fuzz.android.limelight.LimeLight;
import com.fuzz.android.limelight.R;
import com.fuzz.android.limelight.util.FontUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * FontListAdapter customizes the list items to be displayed in the font dialog in Recording Window.
 *
 * @author William Xu (Fuzz)
 */
public class FontListAdapter extends ArrayAdapter<String> {
    private ArrayList<String> fonts = null;

    public FontListAdapter(Context context, int resource, List<String> fonts) {
        super(context, resource, fonts);

        this.fonts = (ArrayList) fonts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = new TextView(LimeLight.getActivity());
        textView.setPadding(20, 20, 20, 20);

        if (fonts.get(position).equals(Recorder.getCurrentFont())) {
            //if it is the current user selected font then the fontlist should reflect that through the specific textview
            textView.setBackgroundColor(LimeLight.getActivity().getResources().getColor(R.color.alternate_green));
            textView.setTextColor(Color.WHITE);
        } else {
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.BLACK);
        }

        textView.setText(FontUtils.sanitizeFontName(fonts.get(position)));

        return textView;
    }


}