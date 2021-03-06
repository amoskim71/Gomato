package com.example.gomato.activity;


import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gomato.R;
import com.example.gomato.common.Analytics;
import com.example.gomato.fragment.RestaurantDetails;

public class RestaurantDetailsActivity extends AppCompatActivity {

    private static final String FTAG_DETAILS = "detailsFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        Bundle bundle = new Bundle();
        bundle.putString("resid", getIntent().getStringExtra("resid"));
        bundle.putString("rName", getIntent().getStringExtra("rName"));
        bundle.putString("rCuisine", getIntent().getStringExtra("rCuisine"));
        bundle.putString("rFeatureImage",getIntent().getStringExtra("rFeatureImage"));

        RestaurantDetails fragment = new RestaurantDetails();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_rest_details_activity_container, fragment, FTAG_DETAILS)
                .commit();

        //Analytics information
        Analytics.setCurrentScreen(this, "Restaurant Details");
        Analytics.setUserAction(Analytics.EVENT_RESTAURANT_DETAILS, Analytics.PARAM_RESTAURANT_SELECTED,
                getIntent().getStringExtra("rName"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
