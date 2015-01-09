/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.util;

import com.fuzz.android.limelight.LimeLight;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Holds methods needed to access and use fonts in LimeLight
 *
 * @author William Xu (Fuzz)
 */
public class FontUtils {

    static private ArrayList<String> fontList;

    /**
     * @return the list of fonts from the assets folder
     */
    public static ArrayList<String> getFonts() {
        if (fontList == null) {
            String[] fontNames = null;
            try {
                fontNames = LimeLight.getActivity().getAssets().list("");
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (fontNames != null) {
                fontList = new ArrayList<String>(fontNames.length);
                String font;
                String fontEnding;
                for (int i = 0; i < fontNames.length; i++) {
                    font = fontNames[i];
                    fontEnding = font.toLowerCase();
                    if (fontEnding.endsWith(".ttf") || fontEnding.endsWith(".otf")) {
                        fontList.add(font);
                    }
                }

            }
        }
        return fontList;
    }

    /**
     * @param fontName name to be formatted and cleaned up
     * @return the cleaned up font name
     */
    public static String sanitizeFontName(String fontName) {

        String sanitized = "";

        String[] capitals = fontName.split("(?=\\p{Upper})");

        for (String s : capitals) {
            sanitized += s + (s.length() > 1 ? " " : "");
        }

        if (sanitized.lastIndexOf(".") > 0) {
            sanitized = sanitized.substring(0, sanitized.lastIndexOf(".")).trim();
        }

        if (sanitized.contains("-")) {
            sanitized = sanitized.replace("-", " - ");
        }

        sanitized = sanitized.replaceAll(" +", " ");

        return sanitized;
    }
}
