/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.recorder.widget.editor.operations;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.fuzz.android.limelight.model.Act;
import com.fuzz.android.limelight.recorder.widget.editor.ActEditor;

/**
 * @author Leonard Collins (Fuzz)
 */
public class BaseOperation implements View.OnClickListener, AdapterView.OnItemClickListener{

    private ActEditor mEditor;

    public BaseOperation(ActEditor actEditor){
        this.mEditor = actEditor;
    }

    public Act getAct(){
        return mEditor.getAct();
    }

    public View getActView(){
        return mEditor.getActView();
    }

    public TextView getActTextView(){
        return mEditor.getActTextView();
    }

    public ActEditor getEditor(){
        return mEditor;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
