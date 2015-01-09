/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.model;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.fuzz.android.limelight.LimeLight;
import com.fuzz.android.limelight.R;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Leonard Collins (Fuzz)
 */
public class ChapterTransition extends Act implements Chapter {

    public final static String CLICK_VIEW = "ckv";
    public final static String CLICK_MENU_ITEM = "ckmi";
    public final static String OPEN_DRAWER = "od";
    public final static String CLOSE_DRAWER = "cd";
    public final static String CLICK_DRAWER_ITEM = "ckdi";
    public final static String SCROLL = "scl";
    public final static String SCROLL_TO_CHILD = "stc";
    public final static String NONE = "none";
    long mTime = 1000;
    private Book mBook;
    private Act mAct;
    private int mItemPosition;
    private static int mDrawerItemPosition = 0;
    private int mChildID;

    public static ChapterTransition fromJson(JSONObject jsonObject) {
        ChapterTransition transition = new ChapterTransition();

        return transition;
    }

    public void clickViewWithId(int id) {
        setId(id);
        setMessage(CLICK_VIEW);
    }

    public void clickAnchorView() {
        setMessage(CLICK_VIEW);
    }

    public void clickMenuItemWithId(int id) {
        setId(id);
        setMessage(CLICK_MENU_ITEM);
    }

    public void closeDrawerMenu() {
        setMessage(CLOSE_DRAWER);
    }

    public void openDrawerMenu() {
        setMessage(OPEN_DRAWER);
    }

    public void scrollUntilItemIsVisible(int viewToScrollID, int itemPosition) {
        setId(viewToScrollID);
        setMessage(SCROLL);
        mItemPosition = itemPosition;
    }

    public void scrollUntilChildIsVisible(int viewToScrollID, int childID) {
        setId(viewToScrollID);
        setMessage(SCROLL_TO_CHILD);
        mChildID = childID;
    }

    public int getID() {
        return mId;
    }

    @Override
    public Act getAct() {
        return this;
    }

    @Override
    public int getType() {
        return 0;
    }

    public void setAct(Act act) {
        this.mAct = act;
    }

    public int getItemPosition() {
        return mItemPosition;
    }

    @Override
    public long getTime() {
        return mTime;
    }

    public void setItemPosition(int itemPosition) {
        this.mItemPosition = itemPosition;
    }

    public int getChildID() {
        return mChildID;
    }

    public void setChildID(int childID) {
        this.mChildID = childID;
    }

    @Override
    public void setTime(long seconds) {
        this.mTime = seconds;
    }

    @Override
    public void run() {
        doTransition();
        mBook.read();
    }

    public void doTransition() {
        final int id = getAnchorViewID();

        Runnable runnable = null;
        if (CLICK_VIEW.equals(getMessage())) {
            final View view = LimeLight.getRootView().findViewById(id);
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (view != null) {
                        view.performClick();
                    } else {
                        Log.w("LimeLight | doTransition()", "This transition is not associated with a view to click.");
                    }
                }
            };
        } else if (CLICK_MENU_ITEM.equals(getMessage())) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    ModelHelper.performIdentifierAction(id);
                }
            };
        } else if (OPEN_DRAWER.equals(getMessage())) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    ModelHelper.openDrawer();
                }
            };
        } else if (CLOSE_DRAWER.equals(getMessage())) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    ModelHelper.closeDrawer();
                }
            };
        } else if (CLICK_DRAWER_ITEM.equals(getMessage())) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    ModelHelper.clickDrawerItem(mDrawerItemPosition);
                }
            };
        } else if (SCROLL.equals(getMessage())) {
            final View view = LimeLight.getRootView().findViewById(id);
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (view != null) {
                        ModelHelper.scrollViewToPosition(view, mItemPosition);
                    } else {
                        Log.w("LimeLight | doTransition()", "This transition is not associated with a view to scroll.");
                    }
                }
            };
        } else if (SCROLL_TO_CHILD.equals(getMessage())) {
            final View view = LimeLight.getRootView().findViewById(id);
            final View childView = LimeLight.getRootView().findViewById(mChildID);
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (view != null && childView != null) {
                        ModelHelper.scrollViewUntilChildIsVisible(view, childView);
                    } else {
                        if (view == null) {
                            Log.w("LimeLight | doTransition()", "This transition is not associated with a view to scroll.");
                        }
                        if (childView == null) {
                            Log.w("LimeLight | doTransition()", "This transition is not associated with a child view to scroll toward.");
                        }
                    }
                }
            };
        }

        if (runnable != null) {
            LimeLight.runOnUIThread(runnable);
        }
    }

    @Override
    public int getAnchorViewID() {
        return mAct.getAnchorViewID();
    }

    @Override
    public void setBook(Book book) {
        mBook = book;
    }

    public Act getmAct() {
        return mAct;
    }

    public static void getDrawerPosition() {
        LayoutInflater inflater = LimeLight.getActivity().getLayoutInflater();
        View drawerView = inflater.inflate(R.layout.choose_drawer_item, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(LimeLight.getActivity());
        builder.setTitle(R.string.choose_drawer_item);
        builder.setView(drawerView);

        final Spinner positionSpinner = (Spinner) drawerView.findViewById(R.id.positionSpinner);

        ArrayList<String> items = new ArrayList<String>();
        for (int i = 0; i < LimeLight.getDrawerList().getAdapter().getCount(); i++) {
            items.add(i + "");
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(LimeLight.getActivity(),
                android.R.layout.simple_spinner_dropdown_item, items);

        positionSpinner.setAdapter(spinnerArrayAdapter);

        positionSpinner.setSelection(mDrawerItemPosition);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 mDrawerItemPosition = Integer.valueOf(positionSpinner.getSelectedItem().toString());
            }
        });

        builder.setNegativeButton(R.string.cancel, null);

        builder.create();
        builder.show();
    }

    @Override
    public boolean isDisplayable() {
        return false;
    }

    @Override
    public boolean shouldDismissOnNextChapterReady() {
        return false;
    }

    @Override
    public void update() {

    }
}