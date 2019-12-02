package com.example.gomato.viewadapter;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DimenRes;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gomato.R;
import com.example.gomato.common.DefaultCuisineImage;
import com.example.gomato.databinding.CardFavouriteInfoBinding;
import com.example.gomato.fragment.IRestaurantAction;
import com.example.gomato.model.Favourites;
import com.example.gomato.model.favourite.FavouriteData;
import com.example.gomato.model.search.Restaurant;
import com.example.gomato.viewmodel.FavouritesViewModel;

import java.util.LinkedList;

import timber.log.Timber;

public class FavouritesRecyclerAdapter extends RecyclerView.Adapter<FavouritesRecyclerAdapter.FavouritesViewHolder> {

    private LinkedList<FavouriteData> favouriteDataList;
    private IRestaurantAction restaurantAction;

    @Override
    public FavouritesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        CardFavouriteInfoBinding binder = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.card_favourite_info, parent, false);

        return new FavouritesViewHolder(binder);
    }

    @Override
    public void onBindViewHolder(FavouritesViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return null == favouriteDataList ? 0 : favouriteDataList.size();
    }

    public void setRestaurantAction(IRestaurantAction restaurantAction) {
        this.restaurantAction = restaurantAction;
    }

    public void setFavouriteDataList(FavouritesViewModel viewModel) {

        viewModel.getFavouriteList(new Favourites.ITransactionStatus() {
            @Override
            public void onSuccess(Favourites.FavAction action) {
                //Nothing to do
            }

            @Override
            public void onError(Favourites.FavAction action) {
                Timber.e("Could not fetch data from favourites.");
            }

            @Override
            public void onFavListRetrieved(LinkedList<FavouriteData> dataList) {
                favouriteDataList = dataList;
                notifyDataSetChanged();
            }
        });
    }

    class FavouritesViewHolder extends RecyclerView.ViewHolder {

        private CardFavouriteInfoBinding binder;

        FavouritesViewHolder(CardFavouriteInfoBinding binder) {

            super(binder.getRoot());
            this.binder = binder;

            this.binder.getRoot().setOnClickListener(clickListener);
        }

        void onBind(int position) {

            FavouriteData data = favouriteDataList.get(position);

            binder.tvFavName.setText(data.getResName());
            binder.tvFavLocation.setText(data.getResLocation());
            binder.tvFavCuisine.setText(data.getResCuisines());

            String cuisine = data.getResCuisines().split(",")[0];
            binder.ivFavImage.setImageDrawable(ContextCompat.getDrawable(binder.ivFavImage.getContext(),
                    DefaultCuisineImage.getCuisineImage(cuisine)));
        }

        private View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(null != restaurantAction) {

                    FavouriteData data = favouriteDataList.get(getAdapterPosition());

                    Restaurant restaurant = new Restaurant();
                    restaurant.setName(data.getResName());
                    restaurant.setId(data.getResId());
                    restaurant.setCuisines(data.getResCuisines());

                    restaurantAction.onClick(restaurant);
                }
            }
        };
    }

    public static class CardDecorator extends RecyclerView.ItemDecoration {

        private int margins;

        public CardDecorator(Context context, @DimenRes int offset) {
            margins = context.getResources().getDimensionPixelSize(offset);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(margins, margins, margins, margins);
        }
    }
}
