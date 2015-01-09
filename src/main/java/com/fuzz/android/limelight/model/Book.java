/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.model;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.fuzz.android.limelight.LimeLight;
import com.fuzz.android.limelight.recorder.Recorder;
import com.fuzz.android.limelight.recorder.RecorderWindow;
import com.fuzz.android.limelight.util.LimeLightLog;
import com.fuzz.android.limelight.util.ViewUtils;
import com.fuzz.android.limelight.widget.ManualPositionFrameLayout;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Leonard Collins (Fuzz)
 */
public class Book {
    ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private boolean isSet = false;
    private ArrayList<Chapter> mChapters = new ArrayList<Chapter>();
    private Typeface mTypeface;
    private String mFontName;
    private int mCurrentIndex = -1;
    private Chapter mCurrent;
    private PopupWindow mPopupWindow;
    private ViewGroup mPopupWindowContentView;
    private String mTitle = "testTitle";
    private String mPackage;
    private OnBookStateChangeListener mListener;

    public void initialize() {
        Act act;

        if (mChapters != null) {
            for (Chapter chapter : mChapters) {
                if (chapter instanceof ChapterTransition) {
                    act = findActById(((ChapterTransition) chapter).getID());
                    ((ChapterTransition) chapter).setAct(act);
                }
            }
        }
    }

    public Act findActById(int id) {
        Act act = null;

        if (mChapters != null) {
            for (Chapter chapter : mChapters) {
                if (chapter.getAct().getAnchorViewID() == id) {
                    act = chapter.getAct();
                    break;
                }
            }
        }

        return act;
    }

    public void setOnCompleteListener(OnBookStateChangeListener listener) {
        mListener = listener;
    }

    public void read() {
        //if time is 0 then it's by default a hint, any positive number means to queue it up in auto-play

        Chapter chapter = getCurrentChapter();
        if (chapter != null && chapter.shouldDismissOnNextChapterReady()) {
            dismiss(chapter);
        }

        chapter = getNextChapterToRead();

        if (chapter != null) {
            showChapter(chapter);
            if (chapter.getTime() > 0 && LimeLight.isTutorialModeOn()) {
                Runnable runnable;
                if (mListener != null) {
                    final Chapter fChapter = chapter;
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (fChapter.equals(getLast())) {
                                LimeLight.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mListener != null) {
                                            mListener.onComplete(Book.this);
                                        }
                                    }
                                });
                            } else {
                                LimeLight.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mListener != null) {
                                            mListener.onCurrentChapterChanged(fChapter);
                                            if (Recorder.isPlaying()) {
                                                fChapter.run();
                                            }
                                            LimeLight.getRecorderWindow().focusOnCurrentScrubberView();
                                        }
                                    }
                                });
                            }
                        }
                    };
                } else {
                    runnable = chapter;
                }
                service.schedule(runnable, chapter.getTime(), TimeUnit.MILLISECONDS);
            }
        } else {
            mListener = null;
        }
    }

    public Chapter getCurrentChapter() {
        return mCurrent;
    }

    public void dismiss(final Chapter chapter) {
        BaseChapter baseChapter = null;

        if (chapter instanceof BaseChapter) {
            baseChapter = (BaseChapter) chapter;
        }

        if (chapter.isDisplayable()) {
            final BaseChapter finalBaseChapter = baseChapter;
            LimeLight.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    ViewGroup view = getView();
                    Act act = chapter.getAct();
                    if (view != null) {
                        View childView = view.findViewWithTag(act);
                        if (childView != null) {
                            childView.setAnimation(null);
                            view.removeView(childView);
                        } else {
                            LimeLightLog.e("No child view can be found to dismiss!");
                        }
                    } else {
                        LimeLightLog.e("The PopupWindowContentView is null.");
                    }

                    if (finalBaseChapter != null) {
                        finalBaseChapter.setHasActView(false);
                    }
                }
            });
        }
    }

    public Chapter getNextChapterToRead() {
        if (mCurrentIndex < mChapters.size() - 1) {
            mCurrent = mChapters.get(++mCurrentIndex);
        } else {
            mCurrent = null;
        }
        return mCurrent;
    }

    public Chapter getPreviousChapter() {
        return mCurrentIndex > 0 ? mChapters.get(mCurrentIndex - 1) : null;
    }

    public Chapter getNextChapter() {
        return mCurrentIndex >= 0 && mCurrentIndex < getChapterCount() ? mChapters.get((mCurrentIndex + 1)) : null;
    }

    public void showChapter(Chapter chapter) {
        BaseChapter baseChapter = null;

        if (chapter instanceof BaseChapter) {
            baseChapter = (BaseChapter) chapter;
        }

        if (chapter.isDisplayable()) {
            Act act = chapter.getAct();
            showAct(act);
            if (!baseChapter.hasActView()) {
                baseChapter.setHasActView(true);
            }
        }

        LimeLight.updateScrubber(View.FOCUS_RIGHT);
    }

    protected Chapter getLast() {
        return mChapters.get(mChapters.size() - 1);
    }

    public ViewGroup getView() {
        return mPopupWindowContentView;
    }

    public void showAct(final Act act) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                final View view = act.getLayout();
                view.setId(act.getAnchorViewID());

                int statusBarHeight = ViewUtils.getStatusBarHeight();

                createPopupWindow();

                try {
                    mPopupWindowContentView.removeViewAt(mPopupWindowContentView.getChildCount() - 1);
                } catch (NullPointerException npe){ }

                mPopupWindowContentView.addView(view);
                Animator animation = ActToViewHelper.getAnimationForAct(act);
                if (animation != null) {
                    animation.setTarget(view);
                    //TODO: Act Animation
                    animation.start();
                }
                mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, statusBarHeight);
            }
        };
        if (Thread.currentThread().getId() == 1) {
            runnable.run();
        } else {
            LimeLight.runOnUIThread(runnable);
        }
    }

    private void createPopupWindow() {
        if (mPopupWindow == null) {

            int statusBarHeight = ViewUtils.getStatusBarHeight();

            int screenWidth = ModelHelper.getScreenWidth();
            int screenHeight = ModelHelper.getScreenHeight();

            FrameLayout layout;

            if (LimeLight.isRecorderBuild()) {

                layout = createEditorLayout(LimeLight.getActivity());
            } else {
                layout = new ManualPositionFrameLayout(LimeLight.getActivity());
            }

            layout.setLayoutParams(new ViewGroup.LayoutParams(screenWidth, screenHeight - statusBarHeight));

            mPopupWindowContentView = layout;
            mPopupWindow = new PopupWindow(mPopupWindowContentView);

            mPopupWindow.setTouchable(false);
            mPopupWindow.setHeight(screenHeight - statusBarHeight);
            mPopupWindow.setWidth(screenWidth);

        }
    }

    private FrameLayout createEditorLayout(Context context) {
        FrameLayout layout = null;
        try {
            Class editorLayoutClass = Class.forName("com.fuzz.android.limelight.recorder.widget.drag.EditorFrameLayout");
            Constructor constructor = editorLayoutClass.getConstructor(Context.class);
            layout = (FrameLayout) constructor.newInstance(context);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return layout;
    }

    public int getCurrentChapterIndex() {
        return mChapters.indexOf(mCurrent);
    }

    public PopupWindow showWindow() {
        PopupWindow popupWindow = null;

        createPopupWindow();
        if (!mPopupWindow.isShowing()) {
            int statusBarHeight = ViewUtils.getStatusBarHeight();
            mPopupWindow.showAtLocation(LimeLight.getRootView(), Gravity.NO_GRAVITY, 0, statusBarHeight);
            popupWindow = mPopupWindow;
        }

        return popupWindow;
    }

    public PopupWindow getWindow() {
        return mPopupWindow;
    }

    public void refreshWindow() {
        destroyPopupWindow();
        PopupWindow window = showWindow();
        RecorderWindow recorderWindow = LimeLight.getRecorderWindow();
        recorderWindow.setMainWindow(window);
    }

    //TODO consider dismissing the window after the last chapter of evolution
    public void destroyPopupWindow() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            mPopupWindow = null;
            mPopupWindowContentView = null;
        }
    }

    public void addChapter(Chapter chapter) {
        if (mChapters == null) {
            mChapters = new ArrayList<Chapter>();
        }
        mChapters.add(chapter);
        chapter.setBook(this);
    }

    public Typeface getFont() {
        return mTypeface;
    }

    public void setFont(String fontName) {
        mFontName = fontName;

        mTypeface = Typeface.createFromAsset(LimeLight.getActivity().getAssets(), fontName);
        if (mChapters != null) {
            Chapter chapter;
            Act act;
            View actView;
            for (int i = 0; i < mChapters.size(); i++) {
                chapter = mChapters.get(i);
                chapter.update();
                act = chapter.getAct();
                actView = mPopupWindowContentView.findViewWithTag(act);
                if (actView != null) {
                    ActToViewHelper.updateView(actView, true);
                }
            }
        }
        invalidate();
    }

    public void invalidate() {
        if (mPopupWindowContentView != null) {
            for (int i = 0; i < mPopupWindowContentView.getChildCount(); i++) {
                View view = mPopupWindowContentView.getChildAt(i);
                if (view.getTag() != null) {
                    ActToViewHelper.updateView(view, true);
                }
            }
            mPopupWindowContentView.invalidate();
        }
    }

    public String getFontName() {
        return mFontName;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void create() {
        createPopupWindow();
    }

    public int getChapterCount() {
        return mChapters != null ? mChapters.size() : 0;
    }

    public Chapter getChapterAt(int i) {
        return mChapters.get(i);
    }

    public ArrayList<Chapter> getChapters() {
        return mChapters;
    }

    public int getIndexOfChapter(Chapter chapter) {
        return mChapters.indexOf(chapter);
    }

    public void removeChapter(Chapter deletedChapter) {
        Chapter chapter = null;
        if (deletedChapter.equals(mCurrent)) {
            if (isCurrentChapterLast()) {
                LimeLightLog.d("Moving Into Previous Chapter");
                chapter = getPreviousChapter();
            } else {
                LimeLightLog.d("Moving Into Next Chapter");
                chapter = getNextChapter();
            }
        }

        mChapters.remove(deletedChapter);
        moveToChapter(chapter, false);
        if (mChapters.isEmpty()) {
            reset();
        } else {
            updateCurrent();
        }
    }

    public boolean isCurrentChapterLast() {
        return mCurrentIndex == mChapters.size() - 1;
    }

    public void moveToChapter(final Chapter chapter, boolean continuePlaying) {
        Chapter currentChapter = getCurrentChapter();
        if (currentChapter != null && currentChapter != chapter && currentChapter.shouldDismissOnNextChapterReady()) {
            dismiss(currentChapter);
        }
        if (chapter != null) {
            mCurrent = chapter;
            mCurrentIndex = mChapters.indexOf(mCurrent);

            if (chapter instanceof ChapterTransition) {
                LimeLight.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        ((ChapterTransition) chapter).doTransition();
                    }
                });
            } else {
                showChapter(chapter);
            }

            if (continuePlaying) {
                if (chapter.getTime() > 0) {
                    Runnable runnable;
                    if (mListener != null) {
                        final Chapter fChapter = chapter;
                        runnable = new Runnable() {
                            @Override
                            public void run() {
                                    fChapter.run();
                                    if (fChapter.equals(getLast())) {
                                        LimeLight.runOnUIThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mListener.onComplete(Book.this);
                                            }
                                        });
                                    }
                                }
                        };
                    } else {
                        runnable = chapter;
                    }

                    if (Recorder.isPlaying()) {
                        service.schedule(runnable, chapter.getTime(), TimeUnit.MILLISECONDS);
                    }
                }
            }
        }
    }

    public void reset() {
        if (mPopupWindowContentView != null) {
            mPopupWindowContentView.removeAllViews();
        }
        mCurrentIndex = -1;
        mCurrent = null;
        mListener = null;
    }

    private void updateCurrent() {
        mCurrentIndex = mChapters.indexOf(mCurrent);
    }

    public boolean isEmpty() {
        return mChapters == null || mChapters.isEmpty();
    }

    public String getPackage() {
        return mPackage;
    }

    public void setPackage(String mPackage) {
        this.mPackage = mPackage;
    }

    public static interface OnBookStateChangeListener {
        void onComplete(Book book);

        void onCurrentChapterChanged(Chapter currentChapter);
    }
}
