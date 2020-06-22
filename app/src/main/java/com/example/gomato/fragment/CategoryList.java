package com.example.gomato.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.GridLayoutAnimationController;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.gomato.R;
import com.example.gomato.common.SearchTypes;
import com.example.gomato.databinding.FragmentCategorylistBinding;
import com.example.gomato.model.search.Restaurant;
import com.example.gomato.model.search.SearchResponse;
import com.example.gomato.viewadapter.CategoryRecyclerAdapter;
import com.example.gomato.viewmodel.CategoryViewModel;

import org.parceler.Parcels;

public class CategoryList extends Fragment implements CategoryViewModel.ISearchResult,
        IRestaurantAction {

    public static final String KEY_HEADER_TITLE = "headerTitle";
    public static final String KEY_HEADER_SUB_TITLE = "headerSubTitle";
    public static final String KEY_LOCATION_COORDINATE = "locationCoordinate";
    public static final @SearchTypes.SearchType String KEY_SEARCH_TYPE = "searchType";

    private FragmentCategorylistBinding categorylistBinder;
    private CategoryRecyclerAdapter categoryRecyclerAdapter;
    private IRestaurantAction restaurantActionImpl;

    private CategoryViewModel categoryViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        categoryViewModel = new CategoryViewModel(getArguments().getString(KEY_SEARCH_TYPE),
                Parcels.unwrap(getArguments().getParcelable(KEY_LOCATION_COORDINATE)), this);

        categorylistBinder = DataBindingUtil.inflate(inflater, R.layout.fragment_categorylist,
                container, false);

        //Set the header titles
        categoryViewModel.setHeaderTitle(getArguments().getString(KEY_HEADER_TITLE));
        categoryViewModel.setHeaderSubTitle(getArguments().getString(KEY_HEADER_SUB_TITLE));

        //Setup the recycler view
        categoryRecyclerAdapter = new CategoryRecyclerAdapter(this);
        categoryRecyclerAdapter.setHasStableIds(true);
        categorylistBinder.rvCategoryRestaurants.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        categorylistBinder.rvCategoryRestaurants.addItemDecoration(
                new CategoryRecyclerAdapter.CardDecorator(getActivity(), R.dimen.card_margins));
        categorylistBinder.rvCategoryRestaurants.setAdapter(categoryRecyclerAdapter);
        return categorylistBinder.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSearchComplete(SearchResponse searchResponse) {
        categorylistBinder.setCatViewModel(categoryViewModel);
        categoryRecyclerAdapter.setSearchResponse(searchResponse);
    }

    public void setRestaurantAction(IRestaurantAction action) {
        this.restaurantActionImpl = action;
    }

    @Override
    public void onPause() {
        super.onPause();
//        categorylistBinder.shimmerFrameLayout.stopShimmer();
    }

    @Override
    public void onResume() {
        super.onResume();
//        categorylistBinder.shimmerFrameLayout.startShimmer();
    }

    @Override
    public void onClick(Restaurant restaurant) {
        if(null == restaurantActionImpl) {
            return;
        }
        restaurantActionImpl.onClick(restaurant);
    }



}
