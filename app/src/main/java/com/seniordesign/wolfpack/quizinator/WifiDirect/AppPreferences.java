package com.seniordesign.wolfpack.quizinator.WifiDirect;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

class AppPreferences {

    private static final String TAG = "PTP_Pref";
    static final String PREF_NAME = MessageCodes.PACKAGE_NAME;
    static final String P2P_ENABLED = "p2pEnabled";

    static String getStringFromPref(Context ctx,
                                    String preferenceFileName,
                                    String key) {
        Log.d(TAG, "getStringFromPref");
        String value = null;
        SharedPreferences pref = ctx.getSharedPreferences(
                                            preferenceFileName, 0);
        if (pref != null) {
            value = pref.getString(key, null);
        }
        return value;
    }

    static void setStringToPref(Context ctx,
                                String preferenceFileName,
                                String key,
                                String value) {
        Log.d(TAG, "setStringToPref");
        SharedPreferences pref = ctx.getSharedPreferences(
                preferenceFileName, 0);
        if (pref != null) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(key, value);
            editor.apply();
        }
    }
}
