/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.recorder;

import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.fuzz.android.limelight.LimeLight;
import com.fuzz.android.limelight.R;
import com.fuzz.android.limelight.animation.UpDownAnimation;
import com.fuzz.android.limelight.model.Act;
import com.fuzz.android.limelight.model.BaseChapter;
import com.fuzz.android.limelight.model.Book;
import com.fuzz.android.limelight.model.Chapter;
import com.fuzz.android.limelight.model.ChapterTransition;
import com.fuzz.android.limelight.text.PrimeTextWatcher;
import com.fuzz.android.limelight.view.PrimeClickListener;

import java.lang.reflect.Field;


/**
 * @author Leonard Collins (Fuzz)
 */
public class Recorder {
    private static Book sCurrentBook;
    private static Act sCurrentAct;
    private static Chapter sCurrentChapter;
    private static String sCurrentFont;
    private static ChapterTransition sCurrentTransition;
    private static boolean sIsTutorial = true;
    private static boolean sIsPlaying;
    private static boolean sIsSaved = true;
    private static int sCurrentChapterIndex = -1;

    static void startRecording() {
        replaceListeners();
        sCurrentBook = new Book();
        sCurrentBook.setPackage(LimeLight.getActivity().getPackageName());
        sCurrentBook.create();
        LimeLight.setCurrentBook(sCurrentBook);
    }

    static void startChapter() {
        if (sCurrentChapter != null /**&& isTutorial()**/) {
            ChapterTransition transition = new ChapterTransition();
            transition.clickAnchorView();
            transition.setAct(sCurrentChapter.getAct());
            sCurrentBook.addChapter(transition);
        }
        sCurrentChapter = new BaseChapter();
        addAct();
        sCurrentBook.addChapter(sCurrentChapter);

        Chapter nextChapter = sCurrentBook.getNextChapterToRead();

        if (nextChapter instanceof ChapterTransition){
            nextChapter = sCurrentBook.getNextChapterToRead();
        }
        sCurrentBook.showChapter(nextChapter);
    }

    static void addAct() {
        Act act = sCurrentAct = new Act();

        if (sCurrentBook.getFont() == null) {
            Recorder.setFont("Ubuntu-Regular.ttf");
        }

        act.setTypeface(sCurrentBook.getFont());
        act.setId(LimeLight.getRootView().getId());
        act.setGraphicResID(R.drawable.arrow_up);
        act.setMessage(LimeLight.getActivity().getString(R.string.act_default_message));
        if (act.isInitialCreation()) {
            act.setInitialCreation(false);
            if (!LimeLight.isTesting()) {
                act.setAnimation(new UpDownAnimation());
            }
        }

        act.setActivityName(LimeLight.getActivity().getClass().getSimpleName());

        Resources res = LimeLight.getActivity().getResources();

        act.setDisplacement(-.5, 1.5);
        act.setTextColor(res.getColor(R.color.dark_green));
        act.setTextBackgroundColor(Color.LTGRAY);
        act.setTextSize(12f);
        if (sCurrentChapter instanceof BaseChapter) {
            ((BaseChapter) sCurrentChapter).setAct(sCurrentAct);
        }
    }

    static void setFont(String fontName) {
        sCurrentBook.setFont(fontName);
        sCurrentFont = fontName;
    }

//    static boolean isTutorial(){
//        return sIsTutorial;
//    }

    static void recordAsTutorial() {
        sIsTutorial = true;
    }

    static void recordAsHint() {
        sIsTutorial = false;
    }

    static boolean isSaved() {
        return sIsSaved;
    }

    static void setIdForCurrentAct(int id) {
        sCurrentAct.setId(id);
    }

    static void setGraphicResIDForCurrentAct(int resID) {
        sCurrentAct.setGraphicResID(resID);
    }

    static void setMessageForCurrentAct(String message) {
        sCurrentAct.setMessage(message);
    }

    static void setCurrentActDuration(long duration) {
        if (sCurrentChapter instanceof BaseChapter) {
            ((BaseChapter) sCurrentChapter).setTime(duration);
        }
    }

    static void endChapter() {
    }

    static Book endRecording() {
        Book Book = sCurrentBook;
        sCurrentBook = null;
        return Book;
    }

    public static ChapterTransition startTransition() {
        sCurrentTransition = new ChapterTransition();
        return sCurrentTransition;
    }

    public static void setBook(Book book) {
        sCurrentBook = book;
    }

    static Book getCurrentBook() {
        return sCurrentBook;
    }

    public static String getCurrentFont() {
        return sCurrentFont;
    }

    public static void moveToChapter(Chapter chapter) {
        sCurrentChapter = chapter;
        sCurrentChapterIndex = sCurrentBook.getIndexOfChapter(chapter);
        sCurrentBook.moveToChapter(chapter, true);
    }

    public static void showChapter(Chapter chapter){
        sCurrentChapter = chapter;
        sCurrentChapterIndex = sCurrentBook.getIndexOfChapter(chapter);
        sCurrentBook.moveToChapter(chapter, false);
    }

    public static void deleteTransition(ChapterTransition transition) {
        //write deleteTransition method to be called in RecorderWindow when choosing not to save

    }

    public static Chapter getCurrentChapter() {
        return sCurrentChapter;
    }

    static void replaceListeners() {
        replaceListeners(LimeLight.getRootView());
    }

    public static void replaceListeners(View view) {
        if (view instanceof EditText) {
            setPrimeTextWatcher((TextView) view);
        } else {
            setPrimeOnClickListener(view);
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    replaceListeners(viewGroup.getChildAt(i));
                }
            }
        }
    }

    static Field findField(Object obj, String fieldName) {
        Field clickListener = null;
        Class objClass = obj.getClass();
        boolean keepChecking = true;
        while (keepChecking) {
            try {
                clickListener = objClass.getDeclaredField(fieldName);
                keepChecking = false;
            } catch (Throwable t) {
                if (objClass != null) {
                    objClass = objClass.getSuperclass();
                } else {
                    keepChecking = false;
                }
            }
        }
        if (clickListener != null) {
            clickListener.setAccessible(true);
        }
        return clickListener;
    }

    static void setPrimeTextWatcher(TextView v) {
        v.addTextChangedListener(new PrimeTextWatcher());
    }

    static View.OnClickListener wrapListener(View.OnClickListener listener) {
        View.OnClickListener onClickListener;
        if (LimeLight.isRecorderBuild()) {
            onClickListener = new RecorderClickListener(listener);
        } else {
            onClickListener = new PrimeClickListener(listener);
        }
        return onClickListener;
    }

    static void setPrimeOnClickListener(View v) {
        try {
            Field clickListener = findField(v, "mListenerInfo");
            clickListener.setAccessible(true);
            Object listenerInfo = clickListener.get(v);
            clickListener = findField(listenerInfo, "mOnClickListener");

            View.OnClickListener onClickListener = (View.OnClickListener) clickListener.get(listenerInfo);
            if (onClickListener != null && !(onClickListener instanceof PrimeClickListener)) {
                v.setOnClickListener(wrapListener(onClickListener));
            }
        } catch (Throwable e) {
        }
    }

    public static void recordClick(View v) {
        if (isRecording() && isInTransitionMode()) {
            ChapterTransition chapterTransition = sCurrentTransition;
            try {
                chapterTransition.setId(v.getId());
            } catch (Resources.NotFoundException e) {
                RuntimeException exception = new RuntimeException("The view labelled " + getContent(v) +
                        " does not have a Resource ID, You must assign a resource ID in order to use " +
                        "the recording feature of LimeLight.", e);
                throw exception;
            }
            endTransition();
        }
    }

    public static boolean isRecording() {
        return sCurrentBook != null;
    }

    public static boolean isInTransitionMode() {
        return sCurrentTransition != null;
    }

    static String getContent(View v) {
        String label;
        if (v instanceof TextView) {
            label = ((TextView) v).getText().toString();
        } else {
            label = "[NO LABEL FOUND FOR CLASS: " + v.getClass().getSimpleName() + "]";
        }
        return label;
    }

    public static void endTransition() {
        sCurrentBook.addChapter(sCurrentTransition);
        sCurrentTransition = null;
    }

    static String getResourceEntryName(int id) {
        return LimeLight.getActivity().getResources().getResourceEntryName(id);
    }

    public static void play() {
        if (!sIsPlaying) {
            if (sIsTutorial) {
                sIsPlaying = true;
                sCurrentBook.reset();
                sCurrentBook.setOnCompleteListener(new Book.OnBookStateChangeListener() {
                    @Override
                    public void onComplete(Book book) {
                        if (Recorder.isPlaying()) {
                            sCurrentBook.reset();
                            sIsPlaying = false;
                            LimeLight.turnOff();

                            Recorder.showChapter(getCurrentBook().getChapterAt(0));

                            //reset scrubber after playback is finished, reset icon as well
                            LimeLight.resetRecorderWindow();
                        }
                    }

                    @Override
                    public void onCurrentChapterChanged(Chapter currentChapter) {
                        RecorderWindow window = (RecorderWindow) LimeLight.createRecordingWindow(null);
                        window.updateScrubber();
                    }
                });
                LimeLight.turnOnTutorialMode();
            } else {
                LimeLight.turnOnHintMode();
            }

            sCurrentBook.read();
        }
    }

    public static void deleteCurrentChapter() {
        if (sCurrentChapter != null) {
            deleteChapter(sCurrentChapter);
        }
    }

    public static void deleteChapter(Chapter chapter) {
        sCurrentBook.removeChapter(chapter);
        sCurrentChapter = sCurrentBook.getCurrentChapter();
        sCurrentChapterIndex = sCurrentBook.getCurrentChapterIndex();
    }

    public static void setIsPlaying(boolean playingFlag) {
        sIsPlaying = playingFlag;
    }

    public static void setIsSaved(boolean isSaved) {
        sIsSaved = isSaved;
    }

    public static boolean isPlaying(){
        return sIsPlaying;
    }
}
