package com.example.gomato.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.gomato.R;

import com.example.gomato.animator.ZoomOutPageTransformer;
import com.example.gomato.common.Analytics;
import com.example.gomato.common.SearchTypes;
import com.example.gomato.database.CacheDB;
import com.example.gomato.databinding.ActivityDashboardBinding;
import com.example.gomato.fragment.IRestaurantAction;
import com.example.gomato.model.LocationCoordinates;
import com.example.gomato.model.categories.CategoryResponse;
import com.example.gomato.model.search.Restaurant;
import com.example.gomato.viewadapter.DashboardPageAdapter;
import com.google.android.material.tabs.TabLayout;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;

import timber.log.Timber;

public class DashboardActivity extends AppCompatActivity implements IRestaurantAction {

    private static final int MAX_TABS = 4;

    private ActivityDashboardBinding dashboardBinder;
    private LocationCoordinates locationCoordinates;
    private ShortcutManager shortcutManager;
    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashboardBinder = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        //Fetch the data received from the splash screen
        locationCoordinates = Parcels.unwrap(getIntent().getBundleExtra("data").getParcelable("location"));
        CategoryResponse categoryResponse = Parcels.unwrap(getIntent().getBundleExtra("data").getParcelable("categoryResponse"));

        //Set the toolbar
        setSupportActionBar(dashboardBinder.toolbar);

        //Create the state pager adapter
        FragmentStatePagerAdapter fragmentStatePagerAdapter = new DashboardPageAdapter(getSupportFragmentManager(),
                locationCoordinates, MAX_TABS, getTabHeaderInfos(), this);

        // Set up the ViewPager with the state pager adapter.
        dashboardBinder.container.setAdapter(fragmentStatePagerAdapter);

        dashboardBinder.container.addOnPageChangeListener(
                new TabLayout.TabLayoutOnPageChangeListener(dashboardBinder.tabs));
        dashboardBinder.tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(dashboardBinder.container));
        dashboardBinder.tabs.setSelectedTabIndicatorColor(Color.TRANSPARENT);
        dashboardBinder.container.setPageTransformer(true, new ZoomOutPageTransformer());
        //Analytics information
        Analytics.setAnalyiticsFor("GomatoUser");
        Analytics.setCurrentScreen(this, "Dashboard");
        Analytics.setUserAction(Analytics.EVENT_APP_START, Analytics.PARAM_APP_START, "GomatoUser");

        shortcutManager = getSystemService(ShortcutManager.class);

        Intent dynamicIntent = new Intent(this,SearchActivity.class);
        dynamicIntent.setAction(Intent.ACTION_VIEW);
        ShortcutInfo dynamicShortcut = new ShortcutInfo.Builder(this, "dynamic_shortcut")
                //Define all the shortcut’s characteristics//
                .setShortLabel(getString(R.string.shortcut_short_label))
                .setLongLabel(getString(R.string.shortcut_long_label))
                .setIcon(Icon.createWithResource(this, R.drawable.ic_delivery))
                .setIntent(dynamicIntent)
                .build();
        shortcutManager.setDynamicShortcuts(Collections.singletonList(dynamicShortcut));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_source_search:
                launchSearchActivity();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        CacheDB.getInstance().purgeCache();
        super.onBackPressed();
    }

    @Override
    public void onClick(Restaurant restaurant) {

        launchNextActivity(restaurant);
    }

    private void launchNextActivity(Restaurant restaurant) {
        Timber.d(restaurant.getName());
        Intent launchIntent = new Intent(this, RestaurantDetailsActivity.class);
        launchIntent.putExtra("resid", restaurant.getId());
        launchIntent.putExtra("rName", restaurant.getName());
        launchIntent.putExtra("rCuisine", restaurant.getCuisines());
        launchIntent.putExtra("rFeatureImage",restaurant.getThumb());
        startActivity(launchIntent);
    }

    private void launchSearchActivity() {

        Timber.d("Launching search activity");
        Intent searchIntent = new Intent(this, SearchActivity.class);
        searchIntent.putExtra("location", Parcels.wrap(locationCoordinates));
        startActivity(searchIntent);
    }

    private ArrayList<DashboardPageAdapter.PageInfo> getTabHeaderInfos() {

        ArrayList<DashboardPageAdapter.PageInfo> pageInfos = new ArrayList<>(MAX_TABS);

        pageInfos.add(new DashboardPageAdapter.PageInfo(
                "Go out for lunch or dinner",
                "Dine-out restaurants",
                SearchTypes.SEARCH_DINE_OUT
        ));

        pageInfos.add(new DashboardPageAdapter.PageInfo(
                "Get food delivered",
                "Delivery restaurants",
                SearchTypes.SEARCH_DELIVERY
        ));

        pageInfos.add(new DashboardPageAdapter.PageInfo(
                "Grab food to-go",
                "Delivery restaurants",
                SearchTypes.SEARCH_TAKE_AWAY
        ));

        pageInfos.add(new DashboardPageAdapter.PageInfo(
                "Favourites",
                "Favourite restaurants",
                SearchTypes.SEARCH_FAVOURITE
        ));
        return pageInfos;
    }
}
