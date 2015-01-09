/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.model;

import android.graphics.Typeface;

/**
 * @author Leonard Collins (Fuzz)
 */
public class BaseChapter implements Chapter {
    private Book mBook;

    private long mTime = 1500;
    private Act mAct;
    private boolean mHasActView = false;

    @Override
    public void run() {
        mBook.read();
    }

    public void setAct(Act act, long time) {
        mAct = act;
        mTime = time;
    }

    public void setHasActView(boolean actFlag) {
        mHasActView = actFlag;
    }

    @Override
    public Act getAct() {
        return mAct;
    }

    public void setAct(Act act) {
        mAct = act;
    }

    @Override
    public void setBook(Book book) {
        mBook = book;
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public boolean isDisplayable() {
        return true;
    }

    public boolean hasActView() {
        return mHasActView;
    }

    @Override
    public boolean shouldDismissOnNextChapterReady() {
        return true;
    }

    @Override
    public void update() {
        setTypeface(mBook.getFont());
    }

    @Override
    public void setTime(long seconds) {
        mTime = seconds;
    }

    @Override
    public long getTime() {
        return mTime;
    }

    public void setTypeface(Typeface typeface) {
        if (mAct != null) {
            mAct.setTypeface(typeface);
        }
    }


}
