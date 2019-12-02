package com.example.gomato;

import android.app.Application;

import com.example.gomato.common.Analytics;
import com.example.gomato.database.CacheDB;

import io.realm.Realm;
import timber.log.Timber;

public class GomatoApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        //Init the CacheDB
        CacheDB.init(this);

        //Init analytics
        Analytics.init(this);

        //Realm init
        Realm.init(this);

        //Timber init with release tree
        Timber.plant(new GomatoReleaseTree());
    }
}
