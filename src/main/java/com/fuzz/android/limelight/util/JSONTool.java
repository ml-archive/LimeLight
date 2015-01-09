/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.util;

import android.util.JsonReader;
import android.util.JsonWriter;
import android.widget.Toast;

import com.fuzz.android.limelight.LimeLight;
import com.fuzz.android.limelight.R;
import com.fuzz.android.limelight.model.Act;
import com.fuzz.android.limelight.model.BaseChapter;
import com.fuzz.android.limelight.model.Book;
import com.fuzz.android.limelight.model.Chapter;
import com.fuzz.android.limelight.model.ChapterTransition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Holds methods needed to write Book, BaseChapter, ChapterTransition, and Act data objects into
 * JSON files. Also holds the methods needed to read JSON files and generate the respective data
 * objects.
 *
 * @author William Xu (Fuzz)
 */
public class JSONTool {

    /**
     * Entry point writer method that starts the call of other writer methods.
     *
     * @param outputStream
     * @param book
     * @throws IOException
     */
    public static void writeJSON(OutputStream outputStream, Book book) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8"));
        writer.setIndent("  ");
        writeBook(writer, book);
        writer.close();
    }

    /**
     * @param writer
     * @param book the Book object to be written in JSON
     * @throws IOException
     */
    public static void writeBook(JsonWriter writer, Book book) throws IOException {
        writer.beginObject();
        writer.name("title").value(book.getTitle());
        writer.name("font").value(book.getFontName());
        writer.name("package").value(book.getPackage());
        if (book.getChapters() != null) {
            writer.name("chapters");
            writeChapterArray(writer, book.getChapters());
        } else {
            writer.name("chapters").nullValue();
        }
        writer.endObject();
    }

    /**
     * @param writer
     * @param chapters the list of BaseChapter objects and ChapterTransition objects to be written
     *                 in JSON
     * @throws IOException
     */
    public static void writeChapterArray(JsonWriter writer, ArrayList<Chapter> chapters) throws IOException {
        writer.beginArray();
        for (Chapter chapter : chapters) {
            if (chapter.getType() == 0) {
                ChapterTransition transition = (ChapterTransition) chapter;
                writeTransition(writer, transition);
            } else if (chapter.getType() == 1) {
                BaseChapter baseChapter = (BaseChapter) chapter;
                writeChapter(writer, baseChapter);
            }
        }
        writer.endArray();
    }

    /**
     * @param writer
     * @param transition the ChapterTransition object to be written in JSON
     * @throws IOException
     */
    public static void writeTransition(JsonWriter writer, ChapterTransition transition) throws IOException {
        writer.beginObject();
        writer.name("type").value("transition");
        writer.name("time").value(transition.getTime());
        writer.name("item_position").value(transition.getItemPosition());
        writer.name("child_id").value(transition.getChildID());
        writer.name("id").value(transition.getAnchorViewID());
        writer.name("message").value(transition.getMessage());
        writer.name("message_res_id").value(transition.getMessageResID());
        writer.name("graphic_res_id").value(transition.getGraphicResID());
        writer.name("is_action_bar_item").value(transition.isInActionBar());
        writer.name("x_offset").value(transition.getWidthRatio());
        writer.name("y_offset").value(transition.getHeightRatio());
        writer.name("text_color").value(transition.getTextColor());
        writer.name("text_background_color").value(transition.getTextBackgroundColor());
        writer.name("text_size").value(transition.getTextSize());
        writer.name("text_background_transparent").value(transition.hasTransparentBackground());
        writer.name("animation").value(transition.getAnimation());
        writer.endObject();
    }

    /**
     * @param writer
     * @param chapter the BaseChapter object to be written in JSON
     * @throws IOException
     */
    public static void writeChapter(JsonWriter writer, BaseChapter chapter) throws IOException {
        writer.beginObject();
        writer.name("type").value("chapter");
        writer.name("duration").value(chapter.getTime());
        writer.name("has_act_view").value(chapter.hasActView());
        writer.name("act");
        writeAct(writer, chapter.getAct());
        writer.endObject();
    }

    /**
     * @param writer
     * @param act the act object to be written into JSON
     * @throws IOException
     */
    public static void writeAct(JsonWriter writer, Act act) throws IOException {
        writer.beginObject();
        writer.name("id").value(act.getAnchorViewID());
        writer.name("message").value(act.getMessage());
        writer.name("message_res_id").value(act.getMessageResID());
        writer.name("graphic_res_id").value(act.getGraphicResID());
        writer.name("is_action_bar_item").value(act.isInActionBar());
        writer.name("x_offset").value(act.getWidthRatio());
        writer.name("y_offset").value(act.getHeightRatio());
        writer.name("text_color").value(act.getTextColor());
        writer.name("text_background_color").value(act.getTextBackgroundColor());
        writer.name("text_size").value(act.getTextSize());
        writer.name("text_background_transparent").value(act.hasTransparentBackground());
        writer.name("animation").value(act.getAnimation());
        writer.name("activity_name").value(act.getActivityName());
        writer.endObject();
    }

    /**
     * @param file JSON file
     * @param fileName name of the JSON file
     * @return true if file is a JSON file
     */
    public static boolean validateJSON(File file, String fileName) {
        boolean isValidated = true;

        if (!fileName.contains(".json")) {
            isValidated = false;
        }

        //retrieve the file and decode into string
        String jsonString = null;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            StringBuilder stringBuilder = new StringBuilder();
            String testJSON = null;

            while ((testJSON = bufferedReader.readLine()) != null) {
                stringBuilder.append(testJSON);
            }

            bufferedReader.close();
            jsonString = stringBuilder.toString();
        } catch (Exception e) {
            isValidated = false;
        }

        //try parsing it to validate the jsonString
        try {
            new JSONObject(jsonString);
        } catch (Exception e) {
            try {
                new JSONArray(jsonString);
            } catch (JSONException e1) {
                isValidated = false;
            }
        }

        return isValidated;
    }

    /**
     * Entry point reader method that starts the call of other reader methods.
     *
     * @param inputStream
     * @return Book object after reading JSON is complete
     * @throws IOException
     */
    public static Book readJSON(InputStream inputStream) throws IOException {
        JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
        try {
            return readBook(jsonReader);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            jsonReader.close();
        }
    }

    /**
     * @param reader
     * @return the generated Book object from JSON
     * @throws IOException
     */
    public static Book readBook(JsonReader reader) throws IOException {
        String title = null;
        String fontName = null;
        String packageName = null;
        ArrayList<Chapter> chapters = null;

        reader.beginObject();
        //input code for taking name data from reader and creating Book object\
        int count = 0;
        while (reader.hasNext()) {
            if (count >= 4) {
                break;
            }
            String name = reader.nextName();
            if (name.equals("title")) {
                count++;
                title = reader.nextString();
            } else if (name.equals("font")) {
                count++;
                fontName = reader.nextString();
            } else if (name.equals("package")) {
                count++;
                packageName = reader.nextString();
            } else if (name.equals("chapters")) {
                count++;
                chapters = readChapterArray(reader);
            }
        }
        reader.endObject();

        String currentPackage = LimeLight.getActivity().getPackageName();

        if (!currentPackage.equals(packageName)) {
            Toast.makeText(LimeLight.getActivity(), LimeLight.getActivity().getString(R.string.wrong_application) + " " + packageName, Toast.LENGTH_LONG).show();
            return null;
        }

        Book book = new Book();
        book.setTitle(title);
        book.setFont(fontName);
        book.setPackage(packageName);

        for (Chapter chapter : chapters) {
            book.addChapter(chapter);
        }
        return book;
    }

    /**
     * @param reader
     * @return list of BaseChapter objects and ChapterTransition objects from JSON
     * @throws IOException
     */
    public static ArrayList<Chapter> readChapterArray(JsonReader reader) throws IOException {
        ArrayList<Chapter> chapters = new ArrayList<Chapter>();
        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();
            String name = reader.nextName();
            if (name.equals("type")) {
                String type = reader.nextString();

                if (type.equals("transition")) {
                    chapters.add(readTransition(reader));
                } else if (type.equals("chapter")) {
                    chapters.add(readChapter(reader));
                }
            }
        }
        reader.endArray();

        return chapters;
    }

    /**
     * @param reader
     * @return the generated ChapterTransition object from JSON
     * @throws IOException
     */
    public static ChapterTransition readTransition(JsonReader reader) throws IOException {
        long time = -1;
        int itemPosition = -1;
        int childId = -1;
        int anchorId = -1;
        String message = null;
        int messageResId = -1;
        int grapicResID = -1;
        boolean isActionBarItem = false;
        double xOffset = -1;
        double yOffset = -1;
        int textColor = -1;
        int textBackgroundColor = -1;
        float textSize = -1;
        boolean textBackgroundTransparent = false;
        String animation = null;

        while (reader.hasNext()) {
            try {
                String name = reader.nextName();
                if (name.equals("time"))
                    time = reader.nextLong();
                else if (name.equals("item_position"))
                    itemPosition = reader.nextInt();
                else if (name.equals("child_id"))
                    childId = reader.nextInt();
                else if (name.equals("id"))
                    anchorId = reader.nextInt();
                else if (name.equals("message"))
                    message = reader.nextString();
                else if (name.equals("message_res_id"))
                    messageResId = reader.nextInt();
                else if (name.equals("graphic_res_id"))
                    grapicResID = reader.nextInt();
                else if (name.equals("is_action_bar_item"))
                    isActionBarItem = reader.nextBoolean();
                else if (name.equals("x_offset"))
                    xOffset = reader.nextDouble();
                else if (name.equals("y_offset"))
                    yOffset = reader.nextDouble();
                else if (name.equals("text_color"))
                    textColor = reader.nextInt();
                else if (name.equals("text_background_color"))
                    textBackgroundColor = reader.nextInt();
                else if (name.equals("text_size"))
                    textSize = reader.nextLong();
                else if (name.equals("text_background_transparent"))
                    textBackgroundTransparent = reader.nextBoolean();
                else if (name.equals("animation"))
                    animation = reader.nextString();
            } catch (IllegalStateException e) {
                reader.nextNull();
                e.printStackTrace();
            }
        }

        reader.endObject();

        ChapterTransition transition = new ChapterTransition();
        transition.setTime(time);
        transition.setItemPosition(itemPosition);
        transition.setChildID(childId);
        transition.setId(anchorId);
        transition.setMessage(message);
        transition.setMessageResID(messageResId);
        transition.setGraphicResID(grapicResID);
        transition.setIsActionBarItem(isActionBarItem);
        transition.setDisplacement(xOffset, yOffset);
        transition.setTextColor(textColor);
        transition.setTextBackgroundColor(textBackgroundColor);
        transition.setTextSize(textSize);
        transition.setTransparentBackground(textBackgroundTransparent);
        transition.setAnimation(animation);

        return transition;
    }

    /**
     * @param reader
     * @return the generated BaseChapter object from the JSON
     * @throws IOException
     */
    public static Chapter readChapter(JsonReader reader) throws IOException {
        long time = -1;
        boolean hasActView = false;
        BaseChapter chapter = new BaseChapter();

        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("duration"))
                time = reader.nextLong();
            else if (name.equals("has_act_view"))
                hasActView = reader.nextBoolean();
            else if (name.equals("act"))
                chapter.setAct(readAct(reader));
        }
        reader.endObject();

        chapter.setTime(time);
        chapter.setHasActView(hasActView);
        return chapter;
    }

    /**
     * @param reader
     * @return the generated Act object from the JSON
     * @throws IOException
     */
    public static Act readAct(JsonReader reader) throws IOException {
        int id = -1;
        String message = null;
        int messageResId = -1;
        int graphResId = -1;
        boolean isActionBarItem = false;
        double xOffset = -1;
        double yOffset = -1;
        int textColor = -1;
        int textBackgroundColor = -1;
        float textSize = -1;
        boolean textBackgroundTransparent = false;
        String animation = null;
        String activityName = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id"))
                id = reader.nextInt();
            else if (name.equals("message"))
                message = reader.nextString();
            else if (name.equals("message_res_id"))
                messageResId = reader.nextInt();
            else if (name.equals("graphic_res_id"))
                graphResId = reader.nextInt();
            else if (name.equals("is_action_bar_item"))
                isActionBarItem = reader.nextBoolean();
            else if (name.equals("x_offset"))
                xOffset = reader.nextDouble();
            else if (name.equals("y_offset"))
                yOffset = reader.nextDouble();
            else if (name.equals("text_color"))
                textColor = reader.nextInt();
            else if (name.equals("text_background_color"))
                textBackgroundColor = reader.nextInt();
            else if (name.equals("text_size"))
                textSize = reader.nextLong();
            else if (name.equals("text_background_transparent"))
                textBackgroundTransparent = reader.nextBoolean();
            else if (name.equals("animation"))
                animation = reader.nextString();
            else if (name.equals("activity_name"))
                activityName = reader.nextString();
            else
                reader.skipValue();
        }
        reader.endObject();

        Act act = new Act();
        act.setId(id);
        act.setMessage(message);
        act.setMessageResID(messageResId);
        act.setGraphicResID(graphResId);
        act.setIsActionBarItem(isActionBarItem);
        act.setDisplacement(xOffset, yOffset);
        act.setTextColor(textColor);
        act.setTextBackgroundColor(textBackgroundColor);
        act.setTextSize(textSize);
        act.setTransparentBackground(textBackgroundTransparent);
        act.setAnimation(animation);
        act.setActivityName(activityName);
        act.getLayout();

        return act;
    }
}