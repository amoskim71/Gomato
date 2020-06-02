package com.example.gomato.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.gomato.R;
import com.example.gomato.common.PrefsData;
import com.example.gomato.viewadapter.IntroViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class IntroActivity extends AppCompatActivity {

    private ViewPager2 screenPager;
    IntroViewPagerAdapter viewPagerAdapter;
    private TabLayout tabIndicator;
    private PrefsData prefsData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Make the activity full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        prefsData = new PrefsData(this);
        if (!prefsData.isFirstTimeLaunch()){
            launchHomeScreen();
            finish();
        }
        
        setContentView(R.layout.activity_intro);
    }

    private void launchHomeScreen() {
    }
}
