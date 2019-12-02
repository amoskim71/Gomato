package com.example.gomato.viewmodel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.example.gomato.model.Favourites;

public class FavouritesViewModel extends BaseObservable {

    private ObservableField<String> headerTitle;
    private ObservableField<String> headerSubTitle;

    public FavouritesViewModel() {

        headerTitle = new ObservableField<>();
        headerSubTitle = new ObservableField<>();
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

    public void getFavouriteList(Favourites.ITransactionStatus callback) {
        Favourites.getFavouriteDataList(callback);
    }
}
