package com.example.gomato.common;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsData {

    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;
    private Context mContext;
    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "gomato-welcome-u";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public PrefsData(Context mContext) {
        this.mContext = mContext;
        editor = sharedPrefs.edit();
        sharedPrefs = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }


    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return sharedPrefs.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }
}
