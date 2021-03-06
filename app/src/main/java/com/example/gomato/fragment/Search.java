package com.example.gomato.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.gomato.R;
import com.example.gomato.databinding.FragmentSearchBinding;
import com.example.gomato.model.search.Restaurant;
import com.example.gomato.viewadapter.SearchAdapter;

import org.parceler.Parcels;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class Search extends Fragment implements SearchAdapter.ISearchAction {

    public interface ISearchAcion {
        void onRestaurantSelected(Restaurant restaurant);
    }

    private FragmentSearchBinding binder;
    private SearchAdapter searchAdapter;
    public DisposableObserver<String> disposableObserver;
    private ISearchAcion searchAcion;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        searchAdapter = new SearchAdapter(Parcels.unwrap(getArguments().getParcelable("location")), this);

        binder = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);

        binder.rvSearchResult.setLayoutManager(new LinearLayoutManager(getActivity()));
        binder.rvSearchResult.setAdapter(searchAdapter);

        disposableObserver = new ReactSearch().getReactiveSearcher(binder.svSearchQuery)
                .debounce(3000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String searchString) {

                        binder.tvSearchError.setVisibility(View.GONE);
                        binder.rvSearchResult.setVisibility(View.GONE);

                        if(!TextUtils.isEmpty(searchString)) {
                            binder.pbSearch.setVisibility(View.VISIBLE);
                            searchAdapter.search(searchString);
                        } else {
                            binder.pbSearch.setVisibility(View.GONE);
                            searchAdapter.clearSearch();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onComplete() {

                    }
                });

        return binder.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(null != disposableObserver && !disposableObserver.isDisposed()) {
            disposableObserver.dispose();
        }
    }

    @Override
    public void onClick(Restaurant restaurant) {

        if(null != searchAcion) {
            searchAcion.onRestaurantSelected(restaurant);
        }
    }

    @Override
    public void onSearchComplete(int count) {

        if(0 == count) {
            binder.pbSearch.setVisibility(View.GONE);
            binder.tvSearchError.setVisibility(View.VISIBLE);
            binder.rvSearchResult.setVisibility(View.GONE);
        } else {
            binder.pbSearch.setVisibility(View.GONE);
            binder.tvSearchError.setVisibility(View.GONE);
            binder.rvSearchResult.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSearchError() {

        binder.pbSearch.setVisibility(View.GONE);
        binder.tvSearchError.setVisibility(View.VISIBLE);
        binder.rvSearchResult.setVisibility(View.GONE);
    }

    public void setSearchAction(ISearchAcion searchAction) {
        this.searchAcion = searchAction;
    }

    private class ReactSearch {

        private PublishSubject<String> subject;

        ReactSearch() {
            subject = PublishSubject.create();
        }

        PublishSubject<String> getReactiveSearcher(final SearchView searchView) {

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    subject.onNext(newText);
                    return true;
                }
            });

            return subject;
        }
    }
}
