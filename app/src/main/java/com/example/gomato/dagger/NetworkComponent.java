package com.example.gomato.dagger;

import com.example.gomato.activity.DashboardActivity;
import com.example.gomato.activity.SplashActivity;
import com.example.gomato.model.RestaurantDetailsModel;
import com.example.gomato.model.RestaurantListModel;
import com.example.gomato.model.ReviewModel;
import com.example.gomato.model.SearchModel;

import javax.inject.Singleton;

import dagger.Component;


@Component(modules = NetworkModule.class)
@Singleton
public interface NetworkComponent {
    void inject(SplashActivity splashActivity);
    void inject(DashboardActivity dashboardActivity);

    void inject(RestaurantListModel restaurantListModel);
    void inject(RestaurantDetailsModel restaurantDetailsModel);
    void inject(ReviewModel reviewModel);
    void inject(SearchModel searchModel);
}
