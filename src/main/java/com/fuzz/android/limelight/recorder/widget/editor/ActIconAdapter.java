/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */
package com.fuzz.android.limelight.recorder.widget.editor;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.fuzz.android.limelight.R;

/**
 * ActIconAdapter populates the icon grid in ActEditor and provides methods to help access and
 * manipulate items.
 *
 * @author William Xu (Fuzz)
 */
public class ActIconAdapter extends BaseAdapter {
    private TypedArray mIcons;
    private int selectedIconPosition = -1;

    public ActIconAdapter(Context context) {
        mIcons = context.getResources().obtainTypedArray(R.array.act_icons);
    }

    @Override
    public int getCount() {
        return mIcons.length();
    }

    @Override
    public String getItem(int position) {
        return mIcons.getString(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView iconImage;

        if (convertView == null) {
            iconImage = new ImageView(parent.getContext());
            GridView.LayoutParams params = new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150);
            iconImage.setLayoutParams(params);
            iconImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            iconImage.setPadding(10, 10, 10, 10);
        } else {
            iconImage = (ImageView) convertView;
        }
        iconImage.setBackgroundColor(Color.WHITE);
        if (position == selectedIconPosition) {
            iconImage.setBackgroundColor(Color.LTGRAY);
        }

        iconImage.setImageResource(mIcons.getResourceId(position, -1));

        return iconImage;
    }

    /**
     * @param position the position of an item on the populated list
     * @return the drawable in the given position on the list
     */
    public Drawable getDrawable(int position) {
        return mIcons.getDrawable(position);
    }

    /**
     * saves the given position to manipulate the corresponding list item later
     * @param position the position of an item on the populated list
     */
    public void setIconSelected(int position) {
        selectedIconPosition = position;
    }

    /**
     * @param position the position of an item on the populated list
     * @return the resource id of the drawable in the given position on the list
     */
    public int getDrawableResId(int position) {
        return mIcons.getResourceId(position, -1);
    }
}
