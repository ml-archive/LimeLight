/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.recorder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.fuzz.android.limelight.LimeLight;
import com.fuzz.android.limelight.R;
import com.fuzz.android.limelight.model.Book;
import com.fuzz.android.limelight.model.Chapter;
import com.fuzz.android.limelight.model.ChapterTransition;
import com.fuzz.android.limelight.recorder.widget.drag.EditorFrameLayout;
import com.fuzz.android.limelight.recorder.widget.filedialog.FileDirectoryDialog;
import com.fuzz.android.limelight.recorder.widget.filedialog.OnDirectorySelectListener;
import com.fuzz.android.limelight.util.FontUtils;
import com.fuzz.android.limelight.util.LimeLightLog;
import com.fuzz.android.limelight.util.ViewUtils;

import java.util.ArrayList;

import static com.fuzz.android.limelight.util.ViewUtils.setTextViewBackground;


/**
 * @author Leonard Collins (Fuzz)
 */
public class RecorderWindow extends PopupWindow {

    private static final int TOUCH_TIME_THRESHOLD = 150;
    boolean isBeingDragged;
    private boolean mMainIsTouchable;
    private PopupWindow mMainWindow;
    private HorizontalScrollView mScrubberScrollView;
    private ViewGroup mScrubber;
    private View mContentView;
    private View mPanel;
    private float mLastY;
    private View mLastClicked;
    private long lastTouchDown;
    private boolean mIsPlaying;
    private static int MAX_HEIGHT = 0;
    View.OnClickListener mMoveToChapter = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mIsPlaying) {
                //reset the playback system

                mPlayButton.setImageResource(R.drawable.control_dark_play);
                mIsPlaying = false;

                LimeLight.getCurrentBook().reset();
                Recorder.setIsPlaying(false);
                LimeLight.turnOff();

                resetButtons(false);
                updateScrubber();
            }

            if (mLastClicked != null) {
                ((TransitionDrawable) mLastClicked.getBackground()).reverseTransition(250);
            }
            mLastClicked = v;
            ((TransitionDrawable) v.getBackground()).startTransition(250);

            //if it's a ChapterTransition, should bring up dialog that lets user choose what style
            Chapter chapter = (Chapter) v.getTag();

            if (chapter instanceof ChapterTransition) {
                ChapterTransition transition = (ChapterTransition) chapter;
                showTransitionPicker(transition);
            } else {
                Recorder.showChapter(chapter);
            }
        }
    };
    private ImageButton mPlayButton;
    private String mCurrentFileName = "myFirstLight.json";
    private boolean mFirstExec = true;
    private int mWindowInitYPosition;
    private int mWindowCurrentYPosition;
    private WindowState mWindowState = WindowState.SHOWING;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            if (viewId == R.id.hide_button) {
                final int width = v.getResources().getDisplayMetrics().widthPixels;
                TranslateAnimation animation;
                if (mWindowState == WindowState.SHOWING) { //Showing
                    ((ImageButton) v).setImageResource(R.drawable.chevron_left);
                    animation = new TranslateAnimation(0, width, 0, 0);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mPanel.setPadding(width, 0, 0, 0);
                            mPanel.requestLayout();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    mWindowState = WindowState.HIDDEN;

                } else {
                    ((ImageButton) v).setImageResource(R.drawable.chevron_right);
                    animation = new TranslateAnimation(width, 0, 0, 0);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            mPanel.setPadding(0, 0, 0, 0);
                            mPanel.requestLayout();
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    mWindowState = WindowState.SHOWING;
                }
                animation.setDuration(500);
                animation.setFillAfter(true);
                mPanel.startAnimation(animation);
            } else if (viewId == R.id.new_book) {
                if (!Recorder.isRecording()) {
                    LimeLight.setMenuMode(false);
                    Recorder.startRecording();
                    LimeLight.destroyRecordingWindow();
                    Recorder.getCurrentBook().showWindow();
                    RecorderWindow window = (RecorderWindow) LimeLight.createRecordingWindow(LimeLight.getActivity());
                    window.setMainWindow(Recorder.getCurrentBook().getWindow());
                    int moveToY = window.getContentView().getResources().getDisplayMetrics().heightPixels - ViewUtils.getNavigationBarHeight();
                    window.update(0, moveToY, -1, -1);
                }
            } else if (viewId == R.id.menu) {
                showMenu();
            } else if (viewId == R.id.add) {
                if (Recorder.isRecording()) {
                    Recorder.startChapter();
                    updateScrubber();

                    View view = mScrubber.getChildAt(mScrubber.getChildCount() - 1);

                    if (mLastClicked != null) {
                        ((TransitionDrawable) mLastClicked.getBackground()).reverseTransition(250);
                    }
                    mLastClicked = view;

                    ((TransitionDrawable) view.getBackground()).startTransition(250);
                    Recorder.setIsSaved(false);
                    scrollScrubberToEdge(View.FOCUS_RIGHT);
                }
            } else if (viewId == R.id.delete) {
                if (Recorder.isRecording()) {
                    //need to retrieve currently viewed item from Scrubber

                    Book book = LimeLight.getCurrentBook();

                    if (!book.isEmpty()) {
                        int index = book.getCurrentChapterIndex();

                        View view = mScrubber.getChildAt(index);

                        if (mLastClicked != null) {
                            ((TransitionDrawable) mLastClicked.getBackground()).reverseTransition(250);
                        }

                        try {
                            mLastClicked = mScrubber.getChildAt(index - 1);
                        } catch (Throwable thr) {
                            mLastClicked = null;
                        }

                        if (view != null) {
                            ((TransitionDrawable) view.getBackground()).startTransition(250);

                            ViewGroup parent = (ViewGroup) view.getParent();
                            parent.removeView(view);

                            Chapter chapter = (Chapter) view.getTag();
                            Recorder.deleteChapter(chapter);
                        }
                        updateScrubber();
                        Recorder.showChapter(Recorder.getCurrentChapter());
                        Recorder.setIsSaved(false);

                        if (mLastClicked != null) {
                            ((TransitionDrawable) mLastClicked.getBackground()).reverseTransition(250);
                        }
                        view = mScrubber.findViewWithTag(book.getCurrentChapter());
                        mLastClicked = view;
                        if (view != null) {
                            ((TransitionDrawable) view.getBackground()).startTransition(250);
                        }
                    }
                }
            } else if (viewId == R.id.record) {
                if (Recorder.isRecording()) {
                    ImageButton button = (ImageButton) v;

                    Integer stringResID;

                    try {
                        stringResID = (Integer) button.getTag();
                    } catch (ClassCastException ex) {
                        stringResID = R.string.edit;
                    }

                    TransitionDrawable transition = ((TransitionDrawable) button.getBackground());
                    if (stringResID != null) {
                        toggleEditable(stringResID == R.string.edit);
                        if (stringResID == R.string.edit) {
                            button.setTag(R.string.view);
                            transition.startTransition(250);
                            button.setImageResource(R.drawable.edit);
                        } else {
                            button.setTag(R.string.edit);
                            transition.reverseTransition(250);
                            button.setImageResource(R.drawable.dark_edit);
                        }
                    } else {
                        button.setTag(R.string.edit);
                        toggleEditable(false);
                    }

                    mMainWindow.update();
                }
            } else if (viewId == R.id.font) {
                if (Recorder.isRecording()) {
                    showFontList();
                }
            } else if (viewId == R.id.play) {
                //set button text to play or stop depending on mode
                //on press stop, have to reset the playback system

                if (Recorder.getCurrentBook().getChapterCount() == 0) {
                    Toast.makeText(LimeLight.getActivity(), R.string.nothing_play, Toast.LENGTH_LONG).show();
                    return;
                }

                if (mIsPlaying) {

                    mPlayButton.setImageResource(R.drawable.control_dark_play);
                    mIsPlaying = false;

                    //reset the playback system
                    View view = mScrubber.getChildAt(0);

                    LimeLight.getCurrentBook().reset();
                    Recorder.setIsPlaying(false);
                    LimeLight.turnOff();

                    if (mLastClicked != null) {
                        ((TransitionDrawable) mLastClicked.getBackground()).reverseTransition(250);
                    }

                    if (view != null) {
                        mLastClicked = view;

                        Chapter chapter = LimeLight.getCurrentBook().getChapterAt(0);
                        Recorder.showChapter(chapter);
                    }

                    resetButtons(true);
                    updateScrubber();
                } else {
                    if (Recorder.getCurrentBook().getChapterCount() > 0) {
                        mPlayButton.setImageResource(R.drawable.control_dark_stop);
                        mIsPlaying = true;

                        scrollScrubberToEdge(View.FOCUS_LEFT);

                        //begin playback system
                        Recorder.play();
                        focusOnCurrentScrubberView(mScrubber.getChildAt(0));
                    }
                }
            } else if (viewId == R.id.load) {
                showFileWindow(FileDirectoryDialog.FILE_LOAD);
            } else if (viewId == R.id.forward) {
                Book book = LimeLight.getCurrentBook();

                if (!book.isEmpty()) {

                    int forwardIndex = LimeLight.getCurrentBook().getCurrentChapterIndex() + 1;

                    //check if current item is already last item
                    if (forwardIndex < (book.getChapterCount())) {
                        //update scrubber and chapter
                        View view = mScrubber.getChildAt(forwardIndex);

                        if (mLastClicked != null) {
                            ((TransitionDrawable) mLastClicked.getBackground()).reverseTransition(250);
                        }
                        mLastClicked = view;
                        ((TransitionDrawable) view.getBackground()).startTransition(250);

                        Chapter chapter = book.getChapterAt(forwardIndex);
                        Recorder.moveToChapter(chapter);

                        updateScrubber();

                        if (!ViewUtils.isViewShownInParent(mScrubberScrollView, mLastClicked)){
                            scrollScrubberByChapter(View.FOCUS_RIGHT);
                        }
                    }
                }
            } else if (viewId == R.id.back) {
                Book book = LimeLight.getCurrentBook();
                if (!book.isEmpty()) {

                    int backIndex = LimeLight.getCurrentBook().getCurrentChapterIndex() - 1;

                    //check if current item is already first item
                    if (backIndex >= 0) {
                        //update scrubber and chapter
                        View view = mScrubber.getChildAt(backIndex);

                        if (mLastClicked != null) {
                            ((TransitionDrawable) mLastClicked.getBackground()).reverseTransition(250);
                        }
                        mLastClicked = view;
                        ((TransitionDrawable) view.getBackground()).startTransition(250);

                        Chapter chapter = book.getChapterAt(backIndex);
                        Recorder.moveToChapter(chapter);

                        updateScrubber();

                        if (!ViewUtils.isViewShownInParent(mScrubberScrollView, mLastClicked)){
                            scrollScrubberByChapter(View.FOCUS_LEFT);
                        }
                    }
                }
            }
        }
    };

    public RecorderWindow(final Activity activity) {
        super(LimeLight.getContext());

        Display display = activity.getWindowManager().getDefaultDisplay();

        Point size = new Point(0, 0);
        try {
            display.getRealSize(size);
        } catch (Throwable thr) {
            size.set(display.getWidth(), display.getHeight());
        }
        int screenWidth = size.x;

        final DisplayMetrics metrics = LimeLight.getRootView(activity).getResources().getDisplayMetrics();

        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setWidth(screenWidth);

        if (LimeLight.isMenuMode()) {
            mContentView = LayoutInflater.from(activity).inflate(R.layout.main_menu, null);
        } else {
            mContentView = LayoutInflater.from(activity).inflate(R.layout.recorder_content_view, null);
            mScrubber = (ViewGroup) mContentView.findViewById(R.id.time_slices);
            mScrubberScrollView = ((HorizontalScrollView) mContentView.findViewById(R.id.horizontalScrollView));
        }
        mPanel = mContentView.findViewById(R.id.panel);

        setContentView(mContentView);

        mContentView.findViewById(R.id.hide_button).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean handle = true;
                float y = event.getRawY();
                int action = event.getAction();
                mWindowInitYPosition = mWindowCurrentYPosition;

                mContentView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                int height = mContentView.getMeasuredHeight();

                int newYPosition = (int) (mWindowInitYPosition + y - mLastY);
                int ZERO = ViewUtils.getActionBarHeight() + ViewUtils.getStatusBarHeight();

                int navBarHeight = ViewUtils.getNavigationBarHeight();

                if (action == MotionEvent.ACTION_DOWN) {
                    lastTouchDown = System.currentTimeMillis();
                    isBeingDragged = true;
                } else if (action == MotionEvent.ACTION_UP) {
                    if (System.currentTimeMillis() - lastTouchDown < TOUCH_TIME_THRESHOLD) {
                        mOnClickListener.onClick(v);
                    }
                    isBeingDragged = false;
                    handle = false;
                } else if (action == MotionEvent.ACTION_MOVE) {

                    if (newYPosition >= ZERO && newYPosition + height <= MAX_HEIGHT) {
                        if (isBeingDragged) {
                            move(y - mLastY);
                        }
                    }
                }

                mLastY = y;

                return handle;
            }
        });

        mPlayButton = (ImageButton) mContentView.findViewById(R.id.play);

        if (Recorder.isRecording() && !LimeLight.isMenuMode()) {
            mContentView.findViewById(R.id.add).setOnClickListener(mOnClickListener);
            mContentView.findViewById(R.id.delete).setOnClickListener(mOnClickListener);
            mContentView.findViewById(R.id.font).setOnClickListener(mOnClickListener);
            mContentView.findViewById(R.id.record).setOnClickListener(mOnClickListener);
            mPlayButton.setOnClickListener(mOnClickListener);
            mContentView.findViewById(R.id.menu).setOnClickListener(mOnClickListener);
            mContentView.findViewById(R.id.back).setOnClickListener(mOnClickListener);
            mContentView.findViewById(R.id.forward).setOnClickListener(mOnClickListener);

            mContentView.findViewById(R.id.add).setVisibility(View.VISIBLE);
            mContentView.findViewById(R.id.delete).setVisibility(View.VISIBLE);
            mContentView.findViewById(R.id.font).setVisibility(View.VISIBLE);
            mContentView.findViewById(R.id.record).setVisibility(View.VISIBLE);
        } else {
            mContentView.findViewById(R.id.new_book).setOnClickListener(mOnClickListener);
            mContentView.findViewById(R.id.load).setOnClickListener(mOnClickListener);
        }

        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void move(float yOffset) {
        mWindowCurrentYPosition = (int) (mWindowInitYPosition + yOffset);
        update(0, mWindowCurrentYPosition, -1, -1);
    }

    public static void showLoadedBook(Book book) {
        Recorder.setBook(book);
        LimeLight.setMenuMode(false);
        LimeLight.destroyRecordingWindow();

        Recorder.getCurrentBook().showWindow();
        RecorderWindow window = (RecorderWindow) LimeLight.createRecordingWindow(LimeLight.getActivity());
        window.setMainWindow(Recorder.getCurrentBook().getWindow());
        window.updateScrubber();
        int moveToY = window.getContentView().getResources().getDisplayMetrics().heightPixels - ViewUtils.getNavigationBarHeight();
        window.update(0, moveToY, -1, -1);
    }

    public void updateScrubber() {
        Book book = Recorder.getCurrentBook();
        int count = book.getChapterCount();
        if (count > 0) {
            Chapter chapter;
            for (int i = 0; i < count; i++) {
                chapter = book.getChapterAt(i);
                if (mScrubber.findViewWithTag(chapter) == null) {
                    mScrubber.addView(newTimeSlice(i, chapter));
                }
            }
        }
    }

    public void updateScrubber(int scrollScrubberDirection){
        try {
            updateScrubber();

            try {
                if (scrollScrubberDirection == View.FOCUS_LEFT || scrollScrubberDirection == View.FOCUS_RIGHT) {
                    if (!ViewUtils.isViewShownInParent(mScrubberScrollView, mLastClicked)) {
                        scrollScrubberByPage(scrollScrubberDirection);
                    }
                }
            } catch (NullPointerException npe) {
            }
        } catch (NullPointerException npe) {
            LimeLightLog.w("The scrubber cannot be updated as it does not exist yet.");
        }
    }

    public void focusOnCurrentScrubberView(){
        if (mLastClicked != null) {
            ((TransitionDrawable) mLastClicked.getBackground()).reverseTransition(250);
        }
        View view = mScrubber.findViewWithTag(Recorder.getCurrentBook().getCurrentChapter());
        mLastClicked = view;
        if (view != null) {
            ((TransitionDrawable) view.getBackground()).startTransition(250);
        }
    }

    public void focusOnCurrentScrubberView(View view){
        if (mLastClicked != null) {
            ((TransitionDrawable) mLastClicked.getBackground()).reverseTransition(250);
        }
        mLastClicked = view;
        if (view != null) {
            ((TransitionDrawable) view.getBackground()).startTransition(250);
        }
    }

    private View newTimeSlice(int index, Chapter chapter) {
        TextView view = new TextView(getContentView().getContext());
        TransitionDrawable drawable =
                (TransitionDrawable) ((index % 2 == 0) ?
                        LimeLight.getActivity().getResources().getDrawable(R.drawable.crossfade_scrubber_light) :
                        LimeLight.getActivity().getResources().getDrawable(R.drawable.crossfade_scrubber_dark));
        view.setBackground(drawable);
        view.setTypeface(null, Typeface.BOLD);
        view.setGravity(Gravity.CENTER);
        view.setText(chapter instanceof ChapterTransition ? "T" : (index + 1) + "");
        view.setTag(chapter);
        view.setId(R.id.time_slice);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) ViewUtils.pixelize(58),
                ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);
        view.setOnClickListener(mMoveToChapter);
        return view;
    }

    public void show(final View parent) {
        DisplayMetrics metrics = parent.getResources().getDisplayMetrics();
        final int screenHeight = metrics.heightPixels;
        final int navBarHeight = ViewUtils.getNavigationBarHeight();
        int actionBarHeight = ViewUtils.getActionBarHeight();

        MAX_HEIGHT = screenHeight + actionBarHeight;

        mWindowInitYPosition = mWindowCurrentYPosition = MAX_HEIGHT;

        mContentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mContentView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                int height = mContentView.getMeasuredHeight();

                mWindowInitYPosition = mWindowCurrentYPosition -= (height + 1);

                if (!ViewUtils.deviceHasPhysicalNavigationButtons()) {
                    MAX_HEIGHT -= navBarHeight;
                    mWindowInitYPosition = mWindowCurrentYPosition -= navBarHeight;
                }
                showAtLocation(parent, Gravity.NO_GRAVITY, 0, mWindowInitYPosition);
                getContentView().requestLayout();
            }
        }, 200);
    }

    private void toggleEditable(boolean editable) {
        mMainWindow.setTouchable(editable);
        View view = Recorder.getCurrentBook().getView();
        if (view instanceof EditorFrameLayout) {
            ((EditorFrameLayout) view).setEditable(editable);
        }
    }

    private void showFontList() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(LimeLight.getActivity());
        final ArrayList<String> adapterFonts = FontUtils.getFonts();

        builder.setTitle(R.string.choose_font);

        final FontListAdapter fontListAdapter = new FontListAdapter(LimeLight.getActivity(), android.R.layout.simple_list_item_single_choice, adapterFonts);

        builder.setAdapter(fontListAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String font = adapterFonts.get(which);
                Recorder.setFont(font);
                fontListAdapter.notifyDataSetChanged();
                Recorder.setIsSaved(false);
            }
        });

        builder.show();
    }

    private void showTransitionPicker(final ChapterTransition transition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LimeLight.getActivity());
        builder.setTitle(R.string.choose_transition);
        builder.setCancelable(true);

        int selection = 0;
        if (transition.getMessage() != null && !transition.getMessage().isEmpty()) {
            if (transition.getMessage().equals(ChapterTransition.NONE)) {
                selection = 0;
            } else if (transition.getMessage().equals(ChapterTransition.CLICK_VIEW)) {
                selection = 1;
            } else if (transition.getMessage().equals(ChapterTransition.CLICK_MENU_ITEM)) {
                selection = 2;
            } else if (transition.getMessage().equals(ChapterTransition.OPEN_DRAWER)) {
                selection = 3;
            } else if (transition.getMessage().equals(ChapterTransition.CLOSE_DRAWER)) {
                selection = 4;
            } else if (transition.getMessage().equals(ChapterTransition.SCROLL)) {
                selection = 5;
            } else if (transition.getMessage().equals(ChapterTransition.SCROLL_TO_CHILD)) {
                selection = 6;
            }
        }

        final int fSelection = selection;
        String[] types = LimeLight.getActivity().getResources().getStringArray(R.array.transition_types);
        ArrayAdapter<String> transitionAdapter = new ArrayAdapter<String>(LimeLight.getActivity(), android.R.layout.simple_list_item_1, types){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                try {

                    TextView selectedView = ((TextView) view.findViewById(android.R.id.text1));
                    if (selectedView != null) {
                        if (position == fSelection) {
                            setTextViewBackground(selectedView, LimeLight.getActivity().getResources().getColor(R.color.alternate_green));
                        } else {
                            setTextViewBackground(selectedView, Color.TRANSPARENT);

                        }
                    }
                } catch (Throwable thr) {
                }
                return view;
            }
        };

        builder.setSingleChoiceItems(transitionAdapter, selection, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        transition.setMessage(ChapterTransition.NONE);
                        break;
                    case 1:
                        transition.setMessage(ChapterTransition.CLICK_VIEW);
                        break;
                    case 2:
                        transition.setMessage(ChapterTransition.CLICK_MENU_ITEM);
                        break;
                    case 3:
                        transition.setMessage(ChapterTransition.OPEN_DRAWER);
                        break;
                    case 4:
                        transition.setMessage(ChapterTransition.CLOSE_DRAWER);
                        break;
                    case 5:
                        ChapterTransition.getDrawerPosition();
                        transition.setMessage(ChapterTransition.CLICK_DRAWER_ITEM);
                        break;
                    case 6:
                        transition.setMessage(ChapterTransition.SCROLL);
                        break;
                    case 7:
                        transition.setMessage(ChapterTransition.SCROLL_TO_CHILD);
                        break;
                }

                dialog.cancel();
                Recorder.moveToChapter(transition);
            }
        });

        builder.show();
    }

    private void showMenu() {
        LayoutInflater inflater = LimeLight.getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_menu, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(LimeLight.getActivity());
        builder.setView(dialogView);

        Button saveProgress = (Button) dialogView.findViewById(R.id.saveProgressButton);
        Button backToMenu = (Button) dialogView.findViewById(R.id.backToMenuButton);
        Button cancel = (Button) dialogView.findViewById(R.id.cancelButton);

        final AlertDialog dialog = builder.create();
        dialog.show();

        saveProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileWindow(FileDirectoryDialog.FILE_SAVE);
                Recorder.setIsSaved(true);
                dialog.cancel();
            }
        });

        backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertSave(dialog);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

    }

    private void showFileWindow(final int fileSelectType) {
        //show directory folder with saved act files for user to choose and load
        //create FilePickerDialog and use the FILE_LOAD option

        FileDirectoryDialog filePickerDialog = new FileDirectoryDialog(LimeLight.getActivity(), fileSelectType, new OnDirectorySelectListener() {
            @Override
            public void onChosenDir(String chosenDir) {
                //called on press of positive button of dialog
                //attach to function that will take the chosen file, convert to loadable Book format, and then setup the book acts/transitions

                if (!mCurrentFileName.equals(chosenDir)) {
                    mCurrentFileName = chosenDir;
                }

                if (fileSelectType == FileDirectoryDialog.FILE_SAVE) {
//                    Primate.setMenuMode(true);
//                    Recorder.endRecording();
//                    Primate.destroyRecordingWindow();
                } else {
                    if (!Recorder.isRecording()) {
                        LimeLight.setMenuMode(false);
                        Recorder.startRecording();
                        LimeLight.destroyRecordingWindow();
                        Recorder.getCurrentBook().showWindow();
                        RecorderWindow window = (RecorderWindow) LimeLight.createRecordingWindow(LimeLight.getActivity());
                        window.setMainWindow(Recorder.getCurrentBook().getWindow());
                        int moveToY = window.getContentView().getResources().getDisplayMetrics().heightPixels - ViewUtils.getNavigationBarHeight();
                        window.update(0, moveToY, -1, -1);
                    }
                }
            }
        });

        filePickerDialog.chooseFileOrDir();

    }

    private void alertSave(final AlertDialog alertDialog) {
        //check save status and alert if there are unsaved changes
        if (Recorder.isSaved() == false) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LimeLight.getActivity());
            builder.setMessage(R.string.save_warning);

            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    alertDialog.cancel();

                    Recorder.setIsSaved(true);
                    LimeLight.setMenuMode(true);

                    resetMenu();
                }
            });

            builder.setNegativeButton(R.string.cancel, null);

            builder.create();
            builder.show();
        } else {
            LimeLight.setMenuMode(true);
            alertDialog.cancel();

            resetMenu();
        }
    }

    private void resetMenu() {
        int j = Recorder.getCurrentBook().getChapterCount();
        for (int i = 0; i < j; i++) {
            Recorder.deleteChapter(Recorder.getCurrentBook().getChapterAt(0));
        }

        Recorder.getCurrentBook().reset();
        EditorFrameLayout editorFrameLayout = (EditorFrameLayout) Recorder.getCurrentBook().getView();
        editorFrameLayout.removeAllViews();
        editorFrameLayout.setEditable(false);

        LimeLight.destroyRecordingWindow();
        Recorder.startRecording();
        RecorderWindow window = (RecorderWindow) LimeLight.createRecordingWindow(LimeLight.getActivity());
        window.setMainWindow(Recorder.getCurrentBook().getWindow());
        Recorder.endRecording();
    }

    public void setMainWindow(PopupWindow window) {
        if (mMainWindow != null && mMainWindow != window) {
            mMainWindow.dismiss();
        }
        mMainWindow = window;
    }

    private void convertToHint() {
        Book book = Recorder.getCurrentBook();
        ArrayList<Chapter> chapters = book.getChapters();

        for (Chapter chapter : chapters) {
            if (chapter instanceof ChapterTransition) {
                ChapterTransition transition = (ChapterTransition) chapter;
                Recorder.deleteTransition(transition);
            }
        }
    }

    private void convertToTransition() {
        Book book = Recorder.getCurrentBook();
        ArrayList<Chapter> chapters = book.getChapters();

        //with each BaseChapter, check to see if it has a following transition
        if (chapters.size() > 1) {

        }

    }

    /**
     * <p>Handles scrolling in response to a "home/end" shortcut press. This
     * method will scroll the view to the left or right and give the focus
     * to the leftmost/rightmost component in the new visible area. If no
     * component is a good candidate for focus, this scrollview reclaims the
     * focus.</p>
     *
     * @param direction the scroll direction: {@link android.view.View#FOCUS_LEFT}
     *                  to go the left of the view or {@link android.view.View#FOCUS_RIGHT}
     *                  to go the right
     */
    public void scrollScrubberToEdge(final int direction) {
        mContentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScrubberScrollView.fullScroll(direction);
            }
        }, 100L);
    }

    /**
     * <p>Handles scrolling in response to a "page left/page right"-like shortcut press. This
     * method will scroll the view to the left or right and give the focus
     * to the leftmost/rightmost component in the new visible area. If no
     * component is a good candidate for focus, this scrollview reclaims the
     * focus.</p>
     *
     * @param direction the scroll direction: {@link android.view.View#FOCUS_LEFT}
     *                  to go the left of the view or {@link android.view.View#FOCUS_RIGHT}
     *                  to go the right
     */
    public void scrollScrubberByPage(final int direction) {
        mContentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mScrubberScrollView != null)
                    mScrubberScrollView.pageScroll(direction);
            }
        }, 100L);
    }

    /**
     * <p>Handles scrolling in response to a "arrow left/arrow right" shortcut press. This
     * method will scroll the view to the left or right and give the focus
     * to the leftmost/rightmost component in the new visible area. If no
     * component is a good candidate for focus, this scrollview reclaims the
     * focus.</p>
     *
     * @param direction the scroll direction: {@link android.view.View#FOCUS_LEFT}
     *                  to go the left of the view or {@link android.view.View#FOCUS_RIGHT}
     *                  to go the right
     */
    public void scrollScrubberByChapter(final int direction) {
        mContentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScrubberScrollView.arrowScroll(direction);
            }
        }, 100L);
    }

    public void resetButtons(boolean scrollToStart) {
        mPlayButton.setImageResource(R.drawable.control_dark_play);
        mIsPlaying = false;

        //reset the playback system
        View view = mScrubber.getChildAt(0);

        if (mLastClicked != null) {
            ((TransitionDrawable) mLastClicked.getBackground()).reverseTransition(250);
        }

        if (view != null) {
            mLastClicked = view;

            Chapter chapter = LimeLight.getCurrentBook().getChapterAt(0);
            Recorder.showChapter(chapter);
        }

        if (scrollToStart) {
            scrollScrubberToEdge(View.FOCUS_LEFT);
        }
    }

    private static enum WindowState {
        SHOWING, HIDDEN
    }
}
