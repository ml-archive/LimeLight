/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.model;

/**
 * @author Leonard Collins (Fuzz)
 */
public interface Chapter extends Runnable {
    Act getAct();

    int getType(); //getType will return 0 if it's a ChapterTransition, will return 1 if it's a BaseChapter

    long getTime();

    void setTime(long seconds);

    void setBook(Book book);

    boolean isDisplayable();

    boolean shouldDismissOnNextChapterReady();

    void update();
}
