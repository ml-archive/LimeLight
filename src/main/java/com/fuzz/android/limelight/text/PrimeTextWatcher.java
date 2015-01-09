/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.text;

import android.text.Editable;
import android.text.TextWatcher;

import com.fuzz.android.limelight.LimeLight;

/**
 * @author Leonard Collins (Fuzz)
 */
public class PrimeTextWatcher implements TextWatcher {

    public PrimeTextWatcher() {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        LimeLight.next();
    }
}
