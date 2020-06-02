package com.example.gomato.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefsData {

    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    Context mContext;
    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "gomato-welcome-u";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public PrefsData(Context mContext) {
        this.mContext = mContext;
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = sharedPrefs.edit();
    }


    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return sharedPrefs.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }
}
