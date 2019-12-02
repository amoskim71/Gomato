package com.example.gomato.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.gomato.R;
import com.example.gomato.common.SearchTypes;
import com.example.gomato.databinding.FragmentFavlistBinding;
import com.example.gomato.model.search.Restaurant;
import com.example.gomato.viewadapter.FavouritesRecyclerAdapter;
import com.example.gomato.viewmodel.FavouritesViewModel;

public class FavouriteList extends Fragment implements IRestaurantAction {

    public static final String KEY_HEADER_TITLE = "headerTitle";
    public static final String KEY_HEADER_SUB_TITLE = "headerSubTitle";

    //This will always be favourite and is not dynamic
    private static final @SearchTypes.SearchType String KEY_SEARCH_TYPE = SearchTypes.SEARCH_FAVOURITE;

    private FragmentFavlistBinding binder;
    private FavouritesRecyclerAdapter adapter;
    private IRestaurantAction favRestaurantImpl;

    private FavouritesViewModel favouritesViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        favouritesViewModel = new FavouritesViewModel();

        binder = DataBindingUtil.inflate(inflater, R.layout.fragment_favlist,
                container, false);

        adapter = new FavouritesRecyclerAdapter();
        binder.rvFavRestaurants.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        binder.rvFavRestaurants.addItemDecoration(
                new FavouritesRecyclerAdapter.CardDecorator(getActivity(), R.dimen.card_margins));

        //Set the view model for the view binding
        binder.setFavViewModel(favouritesViewModel);

        //Set the header titles
        favouritesViewModel.setHeaderTitle(getArguments().getString(KEY_HEADER_TITLE));
        favouritesViewModel.setHeaderSubTitle(getArguments().getString(KEY_HEADER_SUB_TITLE));

        return binder.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binder.rvFavRestaurants.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        //Set the view model for the adapter
        adapter.setFavouriteDataList(favouritesViewModel);
        adapter.setRestaurantAction(this);
    }

    @Override
    public void onClick(Restaurant restaurant) {

        if(null == favRestaurantImpl) {
            return;
        }

        favRestaurantImpl.onClick(restaurant);
    }

    public void setRestaurantAction(IRestaurantAction action) {
        this.favRestaurantImpl = action;
    }
}
