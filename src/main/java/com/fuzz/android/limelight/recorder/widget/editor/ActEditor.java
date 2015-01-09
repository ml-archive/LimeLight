/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.recorder.widget.editor;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.fuzz.android.limelight.LimeLight;
import com.fuzz.android.limelight.R;
import com.fuzz.android.limelight.animation.AnimationHolder;
import com.fuzz.android.limelight.model.Act;
import com.fuzz.android.limelight.model.ActToViewHelper;
import com.fuzz.android.limelight.recorder.Recorder;
import com.fuzz.android.limelight.recorder.widget.editor.operations.ChangeAnimationOperation;
import com.fuzz.android.limelight.recorder.widget.editor.operations.ChangeBackgroundColorOperation;
import com.fuzz.android.limelight.recorder.widget.editor.operations.SetTextColorOperation;
import com.fuzz.android.limelight.util.ViewUtils;

import static com.fuzz.android.limelight.util.ViewUtils.setTextViewBackground;

/**
 * @author William Xu (Fuzz)
 */
public class ActEditor implements View.OnClickListener {

    private static final int MAX_CHARACTERS = 150;
    private static final int ARROW_HEIGHT = 30;
    private static final int BUTTON_BOX_HEIGHT = 48;
    private static final int CONTENT_AREA_HEIGHT = 150;
    private static int NUMBER_OF_PAGES;
    final EditText messageEdit;
    final EditText durationEdit;
    final ListView animationListView;
    private final boolean mOriginalTransparency;
    View mCurrentView;
    private PopupWindow mWindow;
    private View mWindowView;
    private Animation mSlideOutRight;
    private Animation mSlideInRight;
    private Animation mSlideInLeft;
    private Animation mSlideOutLeft;
    private Triangle mTriangle;
    private ImageView mActImage;
    private TextView mActTextView;
    private Button increaseTextSizeButton;
    private Button decreaseTextSizeButton;
    private TextView textSizeLabel;
    private float mOriginalTextSize;
    private int mOriginalTextColor;
    private int mOriginalTextBackgroundColor;
    private String mOriginalMessage;
    private long mOriginalDuration;
    private Act mAct;
    private String mNewMessage;
    private ViewGroup parent;
    private int mOriginalGraphicId;
    private View mActView;
    private AnimationHolder mAnimation;
    private AnimationHolder mOriginalAnimation;
    private ViewFlipper flipper;
    private int MIN_TEXT_SIZE = 10;
    private int MAX_TEXT_SIZE = 24;
    private boolean firstTextSizeOpen = true;

    public ActEditor(final Act act, View actView, final Context context, int screenHeight, int screenWidth, boolean pointingUp) {
        mAct = act;
        mActView = actView;
        final View contentView;
        if (pointingUp) {
            contentView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.act_editor_view_up, parent, false);
        } else {
            contentView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.act_editor_view_down, parent, false);
        }

        Resources resources = context.getResources();

        mWindow = new PopupWindow(contentView, screenWidth, getWindowHeight(screenHeight), true);
        mWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mWindowView = mWindow.getContentView();
        mWindow.setFocusable(true);

        flipper = (ViewFlipper) contentView.findViewById(R.id.viewFlipper);
        NUMBER_OF_PAGES = flipper.getChildCount();

        LinearLayout triangleBar = (LinearLayout) mWindowView.findViewById(R.id.arrowBar);
        mTriangle = new Triangle(context, pointingUp);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) ViewUtils.dpize(100), LinearLayout.LayoutParams.WRAP_CONTENT);
        mTriangle.setLayoutParams(params);
        triangleBar.addView(mTriangle);

        contentView.findViewById(R.id.next_menu).setOnClickListener(this);
        contentView.findViewById(R.id.previous_menu).setOnClickListener(this);

        //provides animation on entry for popupwindow
        mSlideInRight = new TranslateAnimation(screenWidth, 0, 0, 0);
        mSlideInRight.setDuration(500);
        mSlideInRight.setFillAfter(true);

        mSlideOutRight = new TranslateAnimation(0, screenWidth, 0, 0);
        mSlideOutRight.setDuration(500);
        mSlideOutRight.setFillAfter(true);

        mSlideOutLeft = new TranslateAnimation(0, -screenWidth, 0, 0);
        mSlideOutLeft.setDuration(500);
        mSlideOutLeft.setFillAfter(true);

        mSlideInLeft = new TranslateAnimation(-screenWidth, 0, 0, 0);
        mSlideInLeft.setDuration(500);
        mSlideInLeft.setFillAfter(true);

        mOriginalGraphicId = act.getGraphicResID();

        durationEdit = (EditText) mWindowView.findViewById(R.id.actDurationEdit);

        mOriginalDuration = Recorder.getCurrentChapter().getTime();
        mOriginalDuration = mOriginalDuration / 1000;
        durationEdit.setText(String.valueOf(mOriginalDuration));
        durationEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !s.toString().isEmpty()) {
                    long time = Long.valueOf(s.toString().trim());
                    time = time * 1000;
                    Recorder.getCurrentChapter().setTime(time);
                } else {
                    Recorder.getCurrentChapter().setTime(0);
                }
            }
        });
        durationEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

        messageEdit = (EditText) mWindowView.findViewById(R.id.actMessageEdit);

        mOriginalMessage = mAct.getMessage();
        mOriginalTextColor = mAct.getTextColor();
        mOriginalTextBackgroundColor = mAct.getTextBackgroundColor();
        mOriginalTextSize = mAct.getTextSize();
        messageEdit.setText(mOriginalMessage + " ");

        messageEdit.setHint(R.string.enter_act_msg);

        messageEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_CHARACTERS)});

        messageEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mActTextView.setText(s);
                mAct.setMessage(s.toString().trim());
            }
        });

        increaseTextSizeButton = ((Button) mWindowView.findViewById(R.id.increaseTextButton));
        decreaseTextSizeButton = ((Button) mWindowView.findViewById(R.id.decreaseTextButton));
        textSizeLabel = ((TextView) mWindowView.findViewById(R.id.textSizeLabel));

        increaseTextSizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shiftTextSize(1);
            }
        });

        decreaseTextSizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shiftTextSize(-1);
            }
        });

        // initializes textviews
        shiftTextSize(0);


        mCurrentView = mWindowView.findViewById(R.id.menu_1);
        GridView mGridView;
        mGridView = (GridView) mWindowView.findViewById(R.id.menu_2);
        mGridView.setBackgroundColor(Color.WHITE);
        mGridView.setNumColumns(getNumberOfColumns(screenWidth));

        final ActIconAdapter iconAdapter = new ActIconAdapter(context);
        mGridView.setAdapter(iconAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                iconAdapter.setIconSelected(position);
                iconAdapter.notifyDataSetChanged();
                mAct.setGraphicResID(iconAdapter.getDrawableResId(position));
                ActToViewHelper.updateView(mActView, true);
            }
        });

        ImageButton button = (ImageButton) mWindowView.findViewById(R.id.cancel);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAct();
                dismiss();
            }
        });

        button = (ImageButton) mWindowView.findViewById(R.id.save);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.post(new Runnable() {
                        @Override
                        public void run() {
                        ActToViewHelper.updateView(mActView, true);
                        dismiss();
                    }
                });

            }
        });

        final ArrayAdapter<String> animAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, resources.getStringArray(R.array.act_animations)) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                try {
                    if (position == animationListView.getCheckedItemPosition()) {
                        setTextViewBackground(((TextView) view.findViewById(android.R.id.text1)), mActTextView.getCurrentTextColor());
                    } else {
                        setTextViewBackground((TextView) view, Color.TRANSPARENT);
                    }
                } catch (Throwable thr) {
                }
                return view;
            }
        };

        final Button textColorBtn = (Button) mWindowView.findViewById(R.id.actColorBtn);
        final Button textColorBackgroundBtn = (Button) mWindowView.findViewById(R.id.actColorBackgroundBtn);
        final CheckBox transparentCheck = (CheckBox) mWindowView.findViewById(R.id.transparentCheckBox);

        setTextViewBackground(textColorBtn, mOriginalTextColor);
        setTextViewBackground(textColorBackgroundBtn, mOriginalTextBackgroundColor);

        transparentCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mActTextView != null) {
                    if (isChecked) {
                        mActTextView.setBackgroundColor(Color.TRANSPARENT);
                    } else {
                        mActTextView.setBackgroundColor(mAct.getTextBackgroundColor());
                    }
                }

                mAct.setTransparentBackground(isChecked);

            }
        });

        textColorBtn.setOnClickListener(new SetTextColorOperation(this));

        textColorBackgroundBtn.setOnClickListener(new ChangeBackgroundColorOperation(this));

        transparentCheck.setChecked(mOriginalTransparency = mAct.hasTransparentBackground());

        mOriginalAnimation = act.getAnimationHolder();

        if (mOriginalAnimation != null) {
            mAct.setAnimation(mOriginalAnimation);
            //TODO: Act Animation
            mOriginalAnimation.getAnimation().start();
        }

        mAnimation = mOriginalAnimation;

        animationListView = ((ListView) mWindowView.findViewById(R.id.animationListView));
        animationListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        animationListView.setAdapter(animAdapter);

        String animationName = ActToViewHelper.getAnimationName(mAct);

        animationListView.setItemChecked(findPositionOfAnimation(animationName, animationListView), true);

        animationListView.setOnItemClickListener(new ChangeAnimationOperation(this));

        setTextView(((TextView) mActView.findViewById(R.id.actText)));

        ((ImageButton) mWindowView.findViewById(R.id.previous_menu)).setClickable(false);
    }

    private int getWindowHeight(int screenHeight) {
        //this method should take the device's screen height and return a height that can comfortably
        //fit the number

        return (int) ViewUtils.pixelize(BUTTON_BOX_HEIGHT + ARROW_HEIGHT + CONTENT_AREA_HEIGHT);
    }

    private void shiftTextSize(int delta) {
        int newSize = (int) (mAct.getTextSize() + delta);
        if (newSize >= MIN_TEXT_SIZE && newSize <= MAX_TEXT_SIZE) {
            textSizeLabel.setText(newSize + " sp");
            mAct.setTextSize((float) newSize);
            try {
                mActTextView.setTextSize((float) newSize);
            } catch (NullPointerException npe) {
            }
        }
    }

    private int getNumberOfColumns(int screenWidth) {
        //this method should take the device's screen width and return the number of columns best fit
        //in the popupwindow taking up screen's width

//        int col = (int) ViewUtils.pixelize(100);
//        return screenWidth/col;
        return 4;
    }

    public void resetAct() {
        if (mAct != null) {
            mAct.setMessage(mOriginalMessage);
            mAct.setTextColor(mOriginalTextColor);
            mAct.setTextBackgroundColor(mOriginalTextBackgroundColor);
            mAct.setTransparentBackground(mOriginalTransparency);
            mAct.setTextSize(mOriginalTextSize);
            mAct.setGraphicResID(mOriginalGraphicId);
            mAct.setAnimation(mOriginalAnimation);
        }
        ActToViewHelper.updateView(mActView, true);
    }

    public void dismiss() {
        mWindow.dismiss();
    }



    private int findPositionOfAnimation(String animationName, ListView listView) {
        Adapter listAdapter = listView.getAdapter();

        int position = 0;

        for (int i = 0; i < listAdapter.getCount(); i++) {
            if (listAdapter.getItem(i).equals(animationName)) {
                position = i;
                break;
            }
        }

        return position;
    }

    public void setTextView(TextView textView) {
        mActTextView = textView;

        if (mActTextView != null && mActTextView.getTextSize() > 0) {
            EditText messageEdit = (EditText) mWindowView.findViewById(R.id.actMessageEdit);
            mActTextView.setTextSize(mOriginalTextSize);
            messageEdit.setTextSize(mOriginalTextSize);

        }
        ActToViewHelper.updateView(mActView, true);

        shiftTextSize(0);
    }

    private int findPosition(float textSize, Spinner spinner) {
        String size = String.valueOf((int) textSize);
        Adapter spinnerAdapter = spinner.getAdapter();

        int position = 0;

        for (int i = 0; i < spinnerAdapter.getCount(); i++) {
            if (spinnerAdapter.getItem(i).equals(size)) {
                position = i;
                break;
            }
        }

        return position;
    }

    public void showAtLocation(int gravity, int xOffset, int yOffset) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mTriangle.getLayoutParams();
        params.setMargins(xOffset, 0, 0, 0);
        mWindow.showAtLocation(LimeLight.getRootView(), gravity, 0, yOffset);
    }

    public boolean isShowing() {
        return mWindow.isShowing();
    }

    public void setPreviewImageView(ImageView imageView) {
//        mActImage = imageView;
//        ActToViewHelper.updateView(mActView);
    }

    private void setOutAnimationListener(final View v, Animation animation) {
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }    @Override
    public void onClick(View v) {
        if (flipper != null) {
            if (v.getId() == R.id.next_menu) {
                ((ImageButton) mWindowView.findViewById(R.id.previous_menu)).setImageResource(R.drawable.chevron_dark_left);
                ((ImageButton) mWindowView.findViewById(R.id.previous_menu)).setClickable(true);
                flipper.showNext();
                if (flipper.getDisplayedChild() + 1 == NUMBER_OF_PAGES) {
                    ((ImageButton) v.findViewById(R.id.next_menu)).setImageResource(R.drawable.chevron_dark_right_disabled);
                    v.findViewById(R.id.next_menu).setClickable(false);
                } else {
                    ((ImageButton) v.findViewById(R.id.next_menu)).setImageResource(R.drawable.chevron_dark_right);
                }
            } else if (v.getId() == R.id.previous_menu) {
                ((ImageButton) mWindowView.findViewById(R.id.next_menu)).setImageResource(R.drawable.chevron_dark_right);
                ((ImageButton) mWindowView.findViewById(R.id.next_menu)).setClickable(true);
                flipper.showPrevious();
                if (flipper.getDisplayedChild() == 0) {
                    ((ImageButton) v.findViewById(R.id.previous_menu)).setImageResource(R.drawable.chevron_dark_left_disabled);
                    v.findViewById(R.id.previous_menu).setClickable(false);
                } else {
                    ((ImageButton) v.findViewById(R.id.previous_menu)).setImageResource(R.drawable.chevron_dark_left);
                }
            }
        }
    }

    private void setInAnimationListener(final View v, Animation animation) {
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public int getHeight() {
        return mWindow.getHeight();
    }

    public void setAct(Act act) {
        mAct = act;
    }

    public void setActView(View v) {
        mActView = v;
    }

    public AnimationHolder getAnimation(){
        return mAnimation;
    }

    public Act getAct() {
        return mAct;
    }

    public View getActView() {
        return mActView;
    }

    public TextView getActTextView() {
        return mActTextView;
    }

    public void setAnimation(AnimationHolder animation) {
        this.mAnimation = animation;
    }

    class Triangle extends View {
        boolean up = false;
        private Path mPath;
        private Path mStrokePath;
        private Paint mPaint;
        private Paint mStrokePaint;

        public Triangle(Context context, boolean up) {
            super(context);
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(Color.WHITE);

            mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mStrokePaint.setStyle(Paint.Style.STROKE);
            mStrokePaint.setStrokeWidth(ViewUtils.dpize(10));

            this.up = up;
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            if (w > 0 && h > 0) {
                if (up) {
                    mPath = new Path();
                    mPath.moveTo(0, h);
                    mPath.lineTo(w, h);
                    mPath.lineTo(w / 2, 0);
                    mPath.close();
                    mStrokePath = new Path();
                    mStrokePath.moveTo(0, h);
                    mStrokePath.lineTo(w / 2, 0);
                    mStrokePath.lineTo(w, h);
                } else {
                    mPath = new Path();
                    mPath.lineTo(w, 0);
                    mPath.lineTo(w / 2, h);
                    mPath.close();
                    mStrokePath = new Path();
                    mStrokePath.lineTo(w / 2, h);
                    mStrokePath.lineTo(w, 0);
                }
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(mPath, mPaint);
            canvas.drawPath(mStrokePath, mStrokePaint);
        }
    }



}