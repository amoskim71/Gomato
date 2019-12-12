package com.example.gomato.viewmodel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.example.gomato.model.Favourites;

public class FavouritesViewModel extends BaseObservable {

    private ObservableField<String> headerTitle;
    private ObservableField<String> headerSubTitle;
    private ObservableField<String> restImages;

    public FavouritesViewModel() {

        headerTitle = new ObservableField<>();
        headerSubTitle = new ObservableField<>();
        restImages = new ObservableField<>();
    }

    @Bindable
    public ObservableField<String> getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle.set(headerTitle);
    }

    @Bindable
    public ObservableField<String> getHeaderSubTitle() {
        return headerSubTitle;
    }

    public void setHeaderSubTitle(String headerSubTitle) {
        this.headerSubTitle.set(headerSubTitle);
    }

    @Bindable
    public ObservableField<String> getRestImages() {
        return restImages;
    }

    public void setRestImages(String restImages) {
        this.restImages.set(restImages);
    }

    public void getFavouriteList(Favourites.ITransactionStatus callback) {
        Favourites.getFavouriteDataList(callback);
    }
}
