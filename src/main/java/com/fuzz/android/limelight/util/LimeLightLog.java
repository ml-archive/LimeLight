/**
 This Source Code Form is subject to the terms of the Mozilla Public
 License, v. 2.0, as well as to the Additional Term regarding proper
 attribution. The latter is located in Term 11 of the License.
 If a copy of the MPL with the Additional Term was not distributed
 with this file, You can obtain one at http://static.fuzzhq.com/licenses/MPL
 */

package com.fuzz.android.limelight.util;

import android.util.Log;
import android.util.Pair;

/**
 * @author James Davis (Fuzz)
 */
public class LimeLightLog {
    private static boolean mAlwaysShowFullStackTrace = false;
    public static void alwaysShowFullStackTrace(boolean enable){
        mAlwaysShowFullStackTrace = enable;
    }

    private static Pair<String, String> getPreparedTagAndMessage(String message){
        return getPreparedTagAndMessage(message, false);
    }

    private static Pair<String, String> getPreparedTagAndMessage(String message, boolean printFullStackTrace){
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        int index = 0;
        for (int i = 0; i < ste.length; i++) {
            if (ste[i].getMethodName().compareTo("getPreparedTagAndMessage") == 0)
            {
                index = i + 2;
                if ( ste.length != i + 1){
                    if (ste[i + 1].getMethodName().compareTo("getPreparedTagAndMessage") != 0){
                        break;
                    }
                }
            }
        }

        String messageTrace = message;

        for (int i = index; i < (printFullStackTrace || mAlwaysShowFullStackTrace? ste.length : index + 1); i++) {
            messageTrace
                    += "\nat "
                    +  ste[i].getClassName()
                    +  "."
                    +  ste[i].getMethodName()
                    + "("
                    +  ste[i].getClassName().substring(ste[i].getClassName().lastIndexOf(".") + 1)
                    +  ".java:"
                    +  String.valueOf(ste[i].getLineNumber())
                    +  ")";
        }

        return new Pair<String, String>(
                "LimeLight | " + ste[index].getMethodName() + "()", messageTrace);
    }

    public static int d(String message, Throwable thr) {
        Pair<String, String> output = getPreparedTagAndMessage(message);
        return Log.d(output.first, output.second, thr);
    }

    public static int d(String message) {
        Pair<String, String> output = getPreparedTagAndMessage(message);
        return Log.d(output.first, output.second);
    }

    public static int i(String message, Throwable thr) {
        Pair<String, String> output = getPreparedTagAndMessage(message);
        return Log.i(output.first, output.second, thr);
    }

    public static int i(String message) {
        Pair<String, String> output = getPreparedTagAndMessage(message);
        return Log.i(output.first, output.second);
    }

    public static int w(String message, Throwable thr) {
        Pair<String, String> output = getPreparedTagAndMessage(message);
        return Log.w(output.first, output.second, thr);
    }

    public static int w(String message) {
        Pair<String, String> output = getPreparedTagAndMessage(message);
        return Log.w(output.first, output.second);
    }

    public static int e(String message, Throwable thr) {
        Pair<String, String> output = getPreparedTagAndMessage(message, true);
        return Log.e(output.first, output.second, thr);
    }

    public static int e(String message) {
        Pair<String, String> output = getPreparedTagAndMessage(message, true);
        return Log.e(output.first, output.second);
    }

    public static int wtf(String message, Throwable thr) {
        Pair<String, String> output = getPreparedTagAndMessage(message, true);
        return Log.wtf(output.first, output.second, thr);
    }

    public static int wtf(String message) {
        Pair<String, String> output = getPreparedTagAndMessage(message, true);
        return Log.wtf(output.first, output.second);
    }
}