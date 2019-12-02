package com.example.gomato.viewadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gomato.BuildConfig;
import com.example.gomato.R;
import com.example.gomato.databinding.ItemSearchBinding;
import com.example.gomato.model.LocationCoordinates;
import com.example.gomato.model.SearchModel;
import com.example.gomato.model.search.Restaurant;
import com.example.gomato.model.search.SearchResponse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchItemViewHolder> {

    public interface ISearchAction {
        void onClick(Restaurant restaurant);
        void onSearchComplete(int count);
        void onSearchError();
    }

    private SearchModel searchModel;
    private DisposableObserver<SearchResponse> disposableObserver;
    private SearchResponse searchResponse;
    private ISearchAction searchAction;

    public SearchAdapter(LocationCoordinates locationCoordinates, ISearchAction searchAction) {

        this.searchAction = searchAction;
        searchModel = new SearchModel(BuildConfig.ZOMATO_BASE_URL, locationCoordinates);
    }

    @Override
    public SearchItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new SearchItemViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_search, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(SearchItemViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return null == searchResponse ? 0 : searchResponse.getRestaurants().size();
    }

    public void search(String searchString) {

        if(null != disposableObserver && !disposableObserver.isDisposed()) {
            disposableObserver.dispose();
        }

        disposableObserver = searchModel.search(searchString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<SearchResponse>() {

                    @Override
                    public void onNext(SearchResponse response) {
                        searchResponse = response;
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("Could not fetch search result for user. %s", e.getMessage());
                        searchAction.onSearchError();
                    }

                    @Override
                    public void onComplete() {

                        searchAction.onSearchComplete(getItemCount());
                    }
                });
    }

    public void clearSearch() {
        searchResponse = null;
        notifyDataSetChanged();
    }

    class SearchItemViewHolder extends RecyclerView.ViewHolder {

        private ItemSearchBinding binder;

        SearchItemViewHolder(ItemSearchBinding binder) {
            super(binder.getRoot());
            this.binder = binder;

            this.binder.getRoot().setOnClickListener(clickListener);
        }

        void onBind(int position) {

            binder.tvSearchItemName.setText(searchResponse.getRestaurants().get(position)
                    .getRestaurant().getName());

            binder.tvSearchItemAddress.setText(searchResponse.getRestaurants().get(position)
                    .getRestaurant().getLocation().getAddress());
        }

        private View.OnClickListener clickListener = view -> {

            if(null != searchAction) {
                searchAction.onClick(searchResponse.getRestaurants()
                        .get(getAdapterPosition()).getRestaurant());
            }
        };
    }
}
