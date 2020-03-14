package com.example.gomato.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.gomato.BuildConfig;
import com.example.gomato.R;
import com.example.gomato.dagger.DaggerNetworkComponent;
import com.example.gomato.dagger.NetworkModule;
import com.example.gomato.model.LocationCoordinates;
import com.example.gomato.model.categories.CategoryResponse;
import com.example.gomato.network.ZomatoServiceApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.parceler.Parcels;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class SplashActivity extends AppCompatActivity {

    private static final int LOCATION_REQUEST_CODE = 1001;

    @Inject
    ZomatoServiceApi zomatoServiceApi;

    private DisposableObserver disposableObserver;
    private CategoryResponse categoryResponse;
    private Location location;
    private FusedLocationProviderClient locationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorSplash));
        getWindow().setFlags(WindowManager.LayoutParams.ANIMATION_CHANGED,
                WindowManager.LayoutParams.FLAG_LAYOUT_ATTACHED_IN_DECOR);
//
        DaggerNetworkComponent.builder()
                .networkModule(new NetworkModule(BuildConfig.ZOMATO_BASE_URL))
                .build()
                .inject(this);

        requestLocationAccess();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != disposableObserver && !disposableObserver.isDisposed()) {
            disposableObserver.dispose();
        }

        if(null != locationProviderClient) {
            locationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case LOCATION_REQUEST_CODE:
                if (PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                    fetchLocationInformation();
                } else {
                    location = null;
                    fetchDataFromZomatoToProceed();
                    launchNextScreen();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void requestLocationAccess() {

        //If the permission has been provided
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            fetchLocationInformation();
            return;
        }

        //Request the permission from the user
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_REQUEST_CODE);
    }

    private void fetchLocationInformation() {

        //permission check
        if (ActivityCompat.checkSelfPermission(SplashActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        //Listen for the last location from the provider
        locationProviderClient.getLastLocation()
                .addOnSuccessListener(loc -> {
                    //We did not get the last location, lets get it
                    if(null == loc) {

                        LocationRequest locationRequest = LocationRequest.create()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000);

                        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

                    } else {
                        location = loc;
                        fetchDataFromZomatoToProceed();
                        launchNextScreen();
                    }
                })
                .addOnFailureListener(e -> {
                    location = null;
                    fetchDataFromZomatoToProceed();
                    launchNextScreen();
                });
    }

    private LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            location = locationResult.getLastLocation();
            locationProviderClient.removeLocationUpdates(locationCallback);
            fetchDataFromZomatoToProceed();
            launchNextScreen();
        }
    };

    private void fetchDataFromZomatoToProceed() {

        disposableObserver = zomatoServiceApi.getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<CategoryResponse>() {
                    @Override
                    public void onNext(CategoryResponse catRes) {
                        categoryResponse = catRes;
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        String jsonStr = gson.toJson(categoryResponse.getCategories());
                        Log.d("Category Response",jsonStr);
                        Timber.d("The category has been received: %s", categoryResponse.getCategories().size());
                    }

                    @Override
                    public void onError(Throwable e) {
                        finish();
                    }

                    @Override
                    public void onComplete() {
                        launchNextScreen();
                    }
                });
    }

    private void launchNextScreen() {

        LocationCoordinates locationCoordinates = new LocationCoordinates();
        locationCoordinates.setLongitude(null == location ? Double.parseDouble(BuildConfig.DEFAULT_LONGITUDE)
                : location.getLongitude());

        locationCoordinates.setLatitude(null == location ? Double.parseDouble(BuildConfig.DEFAULT_LATITUDE)
                : location.getLatitude());

        Bundle bundle = new Bundle();
        bundle.putParcelable("categoryResponse", Parcels.wrap(categoryResponse));
        bundle.putParcelable("location", Parcels.wrap(locationCoordinates));

        Intent dashboardIntent = new Intent(this, DashboardActivity.class);
        dashboardIntent.putExtra("data", bundle);

        startActivity(dashboardIntent);
        finish();
    }

}
