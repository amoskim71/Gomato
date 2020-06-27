package com.example.gomato.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.preference.PreferenceManager;

import androidx.core.location.LocationManagerCompat;

public class PrefsData {
    // Shared preferences file name
    private static final String PREF_NAME = "gomato-welcome-u";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_LOGGED_IN = "User_Login";

    public static void setFirstTimeLaunch(final Context context, boolean isFirstTime) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(IS_FIRST_TIME_LAUNCH, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }
    public static boolean isFirstTimeLaunch(final Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(IS_FIRST_TIME_LAUNCH, Context.MODE_PRIVATE);
        return sharedPrefs.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public static void setUserLogin(final Context context, boolean isLogin){
        SharedPreferences sharedPrefs = context.getSharedPreferences(IS_LOGGED_IN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(IS_LOGGED_IN, isLogin);
        editor.commit();
    }
    public static boolean isLoggedIn(final Context context){
        SharedPreferences sharedPrefs = context.getSharedPreferences(IS_FIRST_TIME_LAUNCH, Context.MODE_PRIVATE);
        return sharedPrefs.getBoolean(IS_LOGGED_IN,false); }

    public static boolean neverAskAgainSelected(final Activity activity, final String permission){
        final boolean prevShouldShowStatus = getRationaleDisplayStatus(activity,permission);
        final boolean currShouldShowStatus = activity.shouldShowRequestPermissionRationale(permission);
        return prevShouldShowStatus != currShouldShowStatus;
    }

    public static void setShouldShowStatus(final Context context, final String permission){
        SharedPreferences getPrefs = context.getSharedPreferences("GENERIC_PREFERENCES",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = getPrefs.edit();
        editor.putBoolean(permission,true);
        editor.commit();
    }
    public static boolean getRationaleDisplayStatus(final Context context, String permission) {
        SharedPreferences genPrefs = context.getSharedPreferences("GENERIC_PREFERENCES", Context.MODE_PRIVATE);
        return genPrefs.getBoolean(permission,false);
    }

    public static boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return LocationManagerCompat.isLocationEnabled(locationManager);
    }
}
