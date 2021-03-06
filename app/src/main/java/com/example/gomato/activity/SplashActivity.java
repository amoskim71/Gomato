package com.example.gomato.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gomato.BuildConfig;
import com.example.gomato.R;
import com.example.gomato.activity.login.LoginActivity;
import com.example.gomato.common.PrefsData;
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

import java.util.Collections;
import javax.inject.Inject;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class SplashActivity extends AppCompatActivity {

    private static final int LOCATION_REQUEST_CODE = 1001;

//    @Inject
//    ZomatoServiceApi zomatoServiceApi;

    private DisposableObserver disposableObserver;
    private CategoryResponse categoryResponse;
    private Location location;
    private FusedLocationProviderClient locationProviderClient;
    private ImageView iconImage;
//    private AnimatedVectorDrawableCompat animatedVectorDrawableCompat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        getWindow().setStatusBarColor(getResources().getColor(R.color.colorSplash,getTheme()));
//        getWindow().setFlags(WindowManager.LayoutParams.ANIMATION_CHANGED,
//                WindowManager.LayoutParams.FLAG_LAYOUT_ATTACHED_IN_DECOR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        getSupportActionBar().hide();
        iconImage = findViewById(R.id.iconImage);
//        animatedVectorDrawableCompat = AnimatedVectorDrawableCompat.create(this,R.drawable.ic_animate);
//        iconImage.setImageDrawable(animatedVectorDrawableCompat);
//        animatedVectorDrawableCompat.start();
//        new Thread(()-> {
//                try {
//                    Thread.sleep(20000);
//                    requestLocationAccess();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//        }).start();
//        DaggerNetworkComponent.builder()
//                .networkModule(new NetworkModule(BuildConfig.ZOMATO_BASE_URL))
//                .build()
//                .inject(this);
        requestLocationAccess();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestLocationAccess();
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
                if (PackageManager.PERMISSION_GRANTED == grantResults[requestCode]) {
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
       if (PrefsData.isLocationEnabled(getApplicationContext())){
           //If the permission has been provided
           if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this,
                   Manifest.permission.ACCESS_FINE_LOCATION)) {

               fetchLocationInformation();
               return;
           }else{
               //I will skip it for this demo
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                   if (PrefsData.neverAskAgainSelected(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                       displayNeverAskAgainDialog();
                   } else {
                       ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                               100);
                   }
               }
           }
           //Request the permission from the user
           ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                   LOCATION_REQUEST_CODE);
       }else{
           Toast.makeText(this, "GPS IS DISABLED",Toast.LENGTH_SHORT).show();
           Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
           startActivity(intent);
       }
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

    private void displayNeverAskAgainDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("We need to send SMS for performing necessary task. Please permit the permission through "
                + "Settings screen.\n\nSelect Permissions -> Enable permission");
        builder.setCancelable(false);
        builder.setPositiveButton("Permit Manually", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            location = locationResult.getLastLocation();
            locationProviderClient.removeLocationUpdates(locationCallback);
//            fetchDataFromZomatoToProceed();
            launchNextScreen();
        }
    };

    private void fetchDataFromZomatoToProceed() {

//        disposableObserver = zomatoServiceApi.getCategories()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableObserver<CategoryResponse>() {
//                    @Override
//                    public void onNext(CategoryResponse catRes) {
//                        categoryResponse = catRes;
//                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                        String jsonStr = gson.toJson(categoryResponse.getCategories());
//                        Log.d("Category Response",jsonStr);
//                        Timber.d("The category has been received: %s", categoryResponse.getCategories().size());
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        finish();
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        launchNextScreen();
//                    }
//                });
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

        if(PrefsData.isFirstTimeLaunch(this)){
            startActivity(new Intent(this,IntroActivity.class));
            PrefsData.setFirstTimeLaunch(this,false);
            finish();
        }else if (PrefsData.isLoggedIn(this)){
            startActivity(new Intent(this, LoginActivity.class));
        }else{
            Intent dashboardIntent = new Intent(this, DashboardActivity.class);
            dashboardIntent.putExtra("data", bundle);
            startActivity(dashboardIntent);
            finish();
        }

    }

}
