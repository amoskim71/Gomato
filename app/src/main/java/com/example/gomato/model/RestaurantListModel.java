package com.example.gomato.model;

import android.util.Log;

import com.example.gomato.BuildConfig;
import com.example.gomato.common.SearchTypes;
import com.example.gomato.dagger.DaggerNetworkComponent;
import com.example.gomato.dagger.NetworkModule;
import com.example.gomato.database.CacheDB;
import com.example.gomato.model.search.SearchResponse;
import com.example.gomato.network.ZomatoServiceApi;
import com.google.gson.Gson;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class RestaurantListModel {
    @Inject
    ZomatoServiceApi zomatoServiceApi;

    private @SearchTypes.SearchType String searchType;
    private DisposableObserver disposableObserver;

    public RestaurantListModel(@SearchTypes.SearchType String searchType) {

        this.searchType = searchType;

        DaggerNetworkComponent.builder()
                .networkModule(new NetworkModule(BuildConfig.ZOMATO_BASE_URL))
                .build()
                .inject(this);
    }

    /**
     * Performs a search on the location
     * @param locationCoordinates Provides the coordinate of the location
     * @return Observable which will be notified when the data is available
     */
    public Observable<SearchResponse> search(LocationCoordinates locationCoordinates) {
        //This returns the observable which will fire once an observer is subscribed with it
        return Observable.create(emitter ->

                disposableObserver = Observable.defer(() ->
                        Observable.create((ObservableOnSubscribe<String>) cacheEmitter ->
                        {

                    String data = CacheDB.getInstance().getFromCache(searchType);
                    cacheEmitter.onNext(data);

                })).flatMap(responseCache -> {

//                    if(!"".equals(responseCache.trim())) {
//                        return Observable.just(new Gson().fromJson(responseCache, SearchResponse.class));
//                    }
                    return zomatoServiceApi.getCategorySearch(Double.toString(locationCoordinates.getLatitude()),
                            Double.toString(locationCoordinates.getLongitude()), searchType);

                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<SearchResponse>() {
                            @Override
                            public void onNext(SearchResponse searchResponse) {

                                Log.d("RestaurantList",searchResponse.toString());
                                cacheSearchResponse(searchResponse);
                                emitter.onNext(searchResponse);
                            }

                            @Override
                            public void onError(Throwable e) {
                                emitter.onError(e);
                            }

                            @Override
                            public void onComplete() {
                                emitter.onComplete();
                                clean();
                            }
                        }));
    }

    /**
     * Must call this method when the DataModel is no longer required.
     */
    public void clean() {

        if(null != disposableObserver && !disposableObserver.isDisposed()) {
            disposableObserver.dispose();
        }
    }

    private void cacheSearchResponse(SearchResponse searchResponse) {

        Observable.just(searchResponse)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<SearchResponse>() {
                    @Override
                    public void onNext(SearchResponse response) {
                        Log.d("Search Response", response.toString());
//                        CacheDB.getInstance().cache(searchType, new Gson().toJson(searchResponse));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("Could not store the information in the cache for %s", searchType);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
