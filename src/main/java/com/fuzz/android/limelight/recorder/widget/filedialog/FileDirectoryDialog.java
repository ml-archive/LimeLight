/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.recorder.widget.filedialog;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fuzz.android.limelight.LimeLight;
import com.fuzz.android.limelight.R;
import com.fuzz.android.limelight.model.Book;
import com.fuzz.android.limelight.recorder.RecorderWindow;
import com.fuzz.android.limelight.util.JSONTool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

/**
 * The custom file directory dialog used for saving and loading.
 *
 * @author William Xu (Fuzz)
 */
public class FileDirectoryDialog {

    public static final int FILE_SAVE = 1;
    public static final int FILE_LOAD = 2;
    private static final String TAG = "FILEDIRECTORY";
    private static final String NEW_FOLDER = "$&NEW_FOLDER&$";
    private Context mContext;
    private OnDirectorySelectListener mDirectorySelectListener;
    private EditText mCurrentFileNameEdit;
    private HorizontalScrollView mDirectoryScrollView;
    Animation.AnimationListener mAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (mDirectoryScrollView != null) {
                mDirectoryScrollView.fullScroll(View.FOCUS_RIGHT);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };
    private LinearLayout mDirectoryPathLayout;
    private int mSelectedState;
    private String mSDCardDirectory = "";
    private String mCurrentDir = "";
    private ArrayList<String> mCurrentSubDirs = null;
    private ArrayAdapter<String> mFileListAdapter;
    private Stack<String> paths;
    private String mSelectedFileName;
    private String mDefaultFileName = "mylimelight";
    private boolean mShowNewFolderPanel = false;

    public FileDirectoryDialog(Context context, int fileSelectType, OnDirectorySelectListener directorySelectListener) {
        mContext = context;
        mDirectorySelectListener = directorySelectListener;

        paths = new Stack<String>();

        //setup mSDCardDirectory to hold location of root directory
        mSDCardDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();

        if (fileSelectType == FILE_SAVE) {
            mSelectedState = FILE_SAVE;
        } else if (fileSelectType == FILE_LOAD) {
            mSelectedState = FILE_LOAD;
        } else {
            mSelectedState = FILE_LOAD;
        }

        //validate the file name
        try {
            mSDCardDirectory = new File(mSDCardDirectory).getCanonicalPath();
            paths.add("SD Card");
        } catch (IOException e) {
            //have some sort of indicator that initial path has invalid file name
            Toast.makeText(mContext, R.string.file_name_invalid, Toast.LENGTH_LONG).show();
            Log.d(TAG, e.getMessage());
        }
    }

    public void chooseFileOrDir() {
        if (mCurrentDir.equals("")) {
            chooseFileOrDir(mSDCardDirectory);
        } else {
            chooseFileOrDir(mCurrentDir);
        }
    }

    /**
     * sets the button settings of the file directory dialog, populates it with
     * the list of subdirectories, and creates it
     * @param dir the path of a particular directory or file
     */
    public void chooseFileOrDir(String dir) {
        final File dirFile = new File(dir);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            dir = mSDCardDirectory;
        }

        //check if file name is valid
        //need to check if getCanonicalPath() does work on Android, else have to use ViewUtils
        try {
            dir = new File(dir).getCanonicalPath();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
            return;
        }

        mCurrentDir = dir;
        mCurrentSubDirs = getDirectories(dir);

        AlertDialog.Builder dialogBuilder = createDirectoryDialog(mCurrentSubDirs, new FilePickerOnClickListener());

        String positiveButton = null;
        if (mSelectedState == FILE_SAVE) {
            positiveButton = mContext.getString(R.string.save);
            createDefaultDir();
        } else if (mSelectedState == FILE_LOAD) {
            positiveButton = mContext.getString(R.string.load);
        }
        if (mSelectedState == FILE_SAVE) {
            dialogBuilder.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mDirectorySelectListener != null) {
                        //pass through constructed file path to listener
                        //check what selectedstate is and then call respective method
                        mSelectedFileName = mCurrentFileNameEdit.getText().toString();
                        mDirectorySelectListener.onChosenDir(mCurrentDir + "/" + mSelectedFileName);
                        //create File with JSONTools Write methods

                        //check if user is trying to save an empty book
                        if (LimeLight.getCurrentBook().getChapterCount() == 0) {
                            Toast.makeText(LimeLight.getActivity(), R.string.empty_book_warning, Toast.LENGTH_LONG).show();
                            return;
                        }

                        //check if file with name already exists, if so delete and replace
                        for (String fileName : mCurrentSubDirs) {
                            String compareSelectedFile = mSelectedFileName + ".json";
                            if (fileName.equals(compareSelectedFile)) {
                                File deleteFile = new File(fileName);
                                deleteFile.delete();
                                Toast.makeText(LimeLight.getActivity(), R.string.overwrite, Toast.LENGTH_LONG).show();
                            }
                        }

                        File jsonFile = new File(mCurrentDir, mSelectedFileName + ".json");

                        try {
                            FileOutputStream outputStream = new FileOutputStream(jsonFile);
                            JSONTool.writeJSON(outputStream, LimeLight.getCurrentBook());
                        } catch (IOException e) {
                            Log.d(TAG, e.getMessage());
                        }
                    }
                }
            });
            dialogBuilder.setNegativeButton(R.string.cancel, null);
        } else {
            dialogBuilder.setCancelable(true);
        }

        AlertDialog fileDirDialog = dialogBuilder.create();
        fileDirDialog.show();
    }

    /**
     * @param newDir name of new directory to be created
     * @return true if new directory is successfully created
     */
    private boolean createSubDir(String newDir) {
        File newDirFile = new File(newDir);
        if (!newDirFile.exists()) {
            return newDirFile.mkdir();
        } else {
            return false;
        }
    }

    /**
     * @param dir specified path of a directory
     * @return the list of subdirectories and files under the specified path
     */
    private ArrayList<String> getDirectories(String dir) {
        ArrayList<String> dirs = new ArrayList<String>();

        try {
            File dirFile = new File(dir);

            //if current directory is not the root directory, add the '..' option to go up a level
            if (!mCurrentDir.equals(mSDCardDirectory)) {
                dirs.add("..");
            }

            if (!dirFile.exists() || !dirFile.isDirectory()) {
                return dirs;
            }

            //add respective file names to list to be populated in dialog
            String fileName;
            for (File file : dirFile.listFiles()) {
                fileName = file.getName();
                if (fileName.contains(".json") || file.isDirectory()) {
                    dirs.add(fileName);
                }
            }
        } catch (Exception e) {
        }

        //finally sort the dirs into ordered display and return the list
        Collections.sort(dirs, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {

                return lhs.compareTo(rhs);
            }
        });
        return dirs;
    }

    /**
     * @param listItems the list of files and directories in the current directory
     * @param onClickListener the OnClickListener to attach to the created dialog
     * @return the AlertDialog.Builder that generates the file directory dialog
     */
    private AlertDialog.Builder createDirectoryDialog(List<String> listItems, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder directoryDialog = new AlertDialog.Builder(mContext);

        View headerView = LayoutInflater.from(mContext).inflate(R.layout.file_dir_header, null);
        View footer = LayoutInflater.from(mContext).inflate(R.layout.file_dir_footer, null);

        ImageButton newFolderButton = (ImageButton) headerView.findViewById(R.id.newFolderButton);
        mDirectoryScrollView = ((HorizontalScrollView) headerView.findViewById(R.id.directoryPathScrollView));
        mDirectoryPathLayout = ((LinearLayout) headerView.findViewById(R.id.directoryPathLayout));

        newFolderButton.setVisibility(View.VISIBLE);
        newFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get name for new dir from edittext and create new dir on current level
                //call updateDirectory() to refresh view for user to see
                if (!mShowNewFolderPanel) {
                    mShowNewFolderPanel = true;
                    mCurrentSubDirs.add(0, NEW_FOLDER);
                }
                mFileListAdapter.notifyDataSetChanged();
            }
        });

        mFileListAdapter = createDirList(listItems);

        directoryDialog.setCustomTitle(headerView);
        directoryDialog.setSingleChoiceItems(mFileListAdapter, -1, onClickListener);

        //add an EditText that shows the current file path
        mCurrentFileNameEdit = ((EditText) footer.findViewById(R.id.fileNameEdit));
        mCurrentFileNameEdit.setText(mSelectedFileName);
        mCurrentFileNameEdit.setHint(mContext.getString(R.string.save_file_name));
        if (mSelectedState == FILE_SAVE) {
            directoryDialog.setView(footer);
        }
        directoryDialog.setCancelable(false);

        updateDirectoryPathLayout(true);

        return directoryDialog;
    }

    /**
     * updates dialog to display the name of the current file or directory
     */
    private void updateDirectory() {
        mCurrentSubDirs.clear();
        mCurrentSubDirs.addAll(getDirectories(mCurrentDir));
        mFileListAdapter.notifyDataSetChanged();

        //update filename edit text with the selected file name
        if (mSelectedFileName == null || mSelectedFileName.isEmpty()) {
            mCurrentFileNameEdit.setText(mDefaultFileName);
        } else {
            String filename = mSelectedFileName;
            if (filename.endsWith(".json")) {
                filename = filename.substring(0, filename.length() - 5);
            }
            mCurrentFileNameEdit.setText(filename);
        }
        updateDirectoryPathLayout(false);
    }

    //method that generates file/dir list to populate dialog

    /**
     * @param fileList the list of names of the files and directories in the current directory
     * @return adapter holding the customized current file list that will populate the dialog
     */
    private ArrayAdapter<String> createDirList(List<String> fileList) {
        return new ArrayAdapter<String>(mContext, R.layout.save_list_item, R.id.textView, fileList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                if (getItem(position).equalsIgnoreCase(NEW_FOLDER)) {
                    final View finalView = View.inflate(getContext(), R.layout.save_list_new_item, null);
                    view = finalView;
                    view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.abc_slide_in_top));

                    final EditText newFolderEditText = ((EditText) view.findViewById(R.id.textView));
                    ImageButton confirm = ((ImageButton) view.findViewById(R.id.confirmButton));
                    ImageButton cancel = ((ImageButton) view.findViewById(R.id.cancelButton));

                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String newDirName = newFolderEditText.getText().toString();
                            if (newDirName != null && !newDirName.isEmpty()) {
                                createNewDirectory(newDirName);
                            } else {
                                ObjectAnimator animator =
                                        ObjectAnimator.ofObject(newFolderEditText,
                                                "backgroundColor",
                                                new ArgbEvaluator(),
                                                Color.TRANSPARENT,
                                                Color.RED);
                                animator.setDuration(300)
                                        .setRepeatMode(Animation.REVERSE);
                                animator.setRepeatCount(1);
                                animator.start();
                            }
                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mCurrentSubDirs.get(0).equalsIgnoreCase(NEW_FOLDER)) {
                                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.abc_slide_out_top);
                                animation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        mCurrentSubDirs.remove(0);
                                        mShowNewFolderPanel = false;
                                        mFileListAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {
                                    }
                                });
                                finalView.startAnimation(animation);
                            }
                        }
                    });
                } else {
                    if (view.findViewById(R.id.imageView) == null) {
                        view = View.inflate(getContext(), R.layout.save_list_item, null);
                    }
                    TextView textView = ((TextView) view.findViewById(R.id.textView));
                    textView.setEllipsize(null);
                    textView.setText(getItem(position));

                    ImageView imageView = ((ImageView) view.findViewById(R.id.imageView));
                    imageView.setImageResource(R.drawable.folder);

                    String item = getItem(position);

                    if (item.contains(".json")) {
                        imageView.setImageResource(R.drawable.page_grey_multiple);
                    } else if (item.isEmpty()) {
                        imageView.setVisibility(View.INVISIBLE);
                    }
                }

                return view;
            }
        };
    }

    /**
     * creates a default LimeLight directory in the device storage for the user
     */
    private void createDefaultDir() {
        //check if the folder exists already
        String defaultFolder = mContext.getString(R.string.app_name);

        List<String> directories = getDirectories(mCurrentDir);
        for (String directory : directories) {
            if (directory.equals(defaultFolder)) {
                mCurrentDir = mCurrentDir + "/" + defaultFolder;
                paths.push(defaultFolder);
                updateDirectory();
                return;
            }
        }

        if (createSubDir(mCurrentDir + "/" + defaultFolder)) {
            mCurrentDir = mCurrentDir + "/" + defaultFolder;
            paths.push(defaultFolder);
            updateDirectory();
        } else {
            Log.d(TAG, "Problem creating default directory.");
        }
    }

    /**
     * creates new directory and navigates to it
     * @param newDirName name of the new directory to be created
     */
    private void createNewDirectory(String newDirName) {
        if (createSubDir(mCurrentDir + "/" + newDirName)) {
            //navigate to newly created directory
            mCurrentDir += "/" + newDirName;
            paths.push(newDirName);
            mShowNewFolderPanel = false;
            updateDirectory();
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.error_create_dir) + " " + newDirName, Toast.LENGTH_LONG).show();
        }
    }

    /**
     *
     * @param init true if
     */
    private void updateDirectoryPathLayout(boolean init) {
        mDirectoryScrollView.fullScroll(View.FOCUS_RIGHT);
        int pathNum = mDirectoryPathLayout.getChildCount();

        if (init) {
            paths.clear();
            String modifiedDirectory = mCurrentDir.replaceFirst(mSDCardDirectory, "SD Card");
            String[] pathArray = modifiedDirectory.split("//");
            for (String subPath : pathArray) {
                paths.push(subPath);
            }
        }

        if (pathNum < paths.size()) {
            for (int i = 0; i < (init ? paths.size() : 1); i++) {
                View view = View.inflate(LimeLight.getActivity(), R.layout.file_dir_header_item, null);
                TextView textView = ((TextView) view.findViewById(R.id.textView));
                textView.setText(paths.peek());
                view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));

                if (!init) {
                    Animation animation = AnimationUtils.loadAnimation(LimeLight.getActivity(), R.anim.abc_slide_in_bottom);
                    animation.setAnimationListener(mAnimationListener);
                    view.startAnimation(animation);
                }
                mDirectoryPathLayout.addView(view);
            }
        } else if (pathNum > paths.size() && pathNum > 0) {
            Animation animation = AnimationUtils.loadAnimation(LimeLight.getActivity(), R.anim.abc_slide_out_bottom);
            animation.setAnimationListener(mAnimationListener);
            mDirectoryPathLayout.getChildAt(pathNum - 1).startAnimation(animation);
            mDirectoryPathLayout.removeViewAt(pathNum - 1);
        }
    }

    /**
     * Custom OnClickListener that attaches to a file directory dialog. It allows the dialog to
     * navigate through the directories in the device storage and loads a json file right upon
     * selection.
     */
    class FilePickerOnClickListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int position) {
            String oldDir = mCurrentDir;

            String selectedName = (String) ((AlertDialog) dialog).getListView().getAdapter().getItem(position);

            //remove character '/' from file names
            if (selectedName.charAt(selectedName.length() - 1) == '/') {
                selectedName = selectedName.substring(0, selectedName.length() - 1);
            }

            //navigate into subdirectory
            if (selectedName.equals("..")) {
                mCurrentDir = mCurrentDir.substring(0, mCurrentDir.lastIndexOf('/'));
                paths.pop();
            } else {
                mCurrentDir += '/' + selectedName;
                if (!selectedName.contains(".json")) {
                    paths.push(selectedName);
                }
            }

            mSelectedFileName = mDefaultFileName;

            //if selection is a regular file
            if (new File(mCurrentDir).isFile()) {
                mCurrentDir = oldDir;
                mSelectedFileName = selectedName;

                if (mSelectedState == FILE_LOAD) {
                    //call JSONTools Read methods
                    try {
                        //retrieve the selected file from device memory
                        File jsonFile = new File(mCurrentDir + "/" + mSelectedFileName);
                        //validate and then get data objects
                        FileInputStream inputStream = new FileInputStream(jsonFile);
                        if (JSONTool.validateJSON(jsonFile, mSelectedFileName)) {
                            //retrieve book from Json file and set as current book
                            Book book = JSONTool.readJSON(inputStream);

                            if (book != null) {
                                book.initialize();
                                LimeLight.setCurrentBook(book);
                                RecorderWindow.showLoadedBook(book);
                            }
                        } else {
                            Toast.makeText(LimeLight.getActivity(), R.string.json_invalid, Toast.LENGTH_LONG).show();
                        }
                    } catch (Throwable e) {
                        Log.d(TAG, e.getMessage());
                        Toast.makeText(LimeLight.getActivity(), R.string.json_error, Toast.LENGTH_LONG).show();
                    }

                    dialog.dismiss();
                }
            }

            mShowNewFolderPanel = false;
            updateDirectory();
        }
    }
}