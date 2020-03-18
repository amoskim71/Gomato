package com.example.gomato.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.gomato.R;
import com.example.gomato.databinding.FragmentRestaurantDetailsBinding;
import com.example.gomato.model.Favourites;
import com.example.gomato.model.favourite.FavouriteData;
import com.example.gomato.viewadapter.ItemsAdapter;
import com.example.gomato.viewadapter.ReviewAdapter;
import com.example.gomato.viewmodel.RestaurantDetailsViewModel;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.LinkedList;

public class RestaurantDetails extends Fragment implements RestaurantDetailsViewModel.IDetailsAction,
        ItemsAdapter.IIteamAction {
//
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_ITEM_TRANSITION_ID = "item_transition_id";
    public static final String ARG_ITEM_COLOR_PALETTE = "image_color_palette";

    private FragmentRestaurantDetailsBinding binder;
    private RestaurantDetailsViewModel restaurantDetailsViewModel;
    private ItemsAdapter adapter;
    private ReviewAdapter reviewAdapter;
//    private  mItem;
//    private String mTransitionName;
    public static int mColorPalette;

    private RestaurantDetails context = this;
    private String restaurantName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
//        if (getArguments() != null && getArguments().containsKey(ARG_ITEM_ID)) {
////            mItem = getArguments().getParcelable(ARG_ITEM_ID);
////            mTransitionName = getArguments().getString(ARG_ITEM_TRANSITION_ID);
//            mColorPalette = getArguments().getInt(ARG_ITEM_COLOR_PALETTE);
//        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binder = DataBindingUtil.inflate(inflater, R.layout.fragment_restaurant_details,
                container, false);

        //Get the name of the restaurant from the caller
        restaurantName = getArguments().getString("rName");

        String cuisine = getArguments().getString("rCuisine");


        cuisine = cuisine.split(",")[0];
//        binder.ivRestDetailsHeader.setImageDrawable(ContextCompat.getDrawable(binder.ivRestDetailsHeader.getContext(),
//                DefaultCuisineImage.getCuisineImage(cuisine)));

        Glide.with(getActivity())
                .load(getArguments().getString("rFeatureImage"))
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .listener(new RequestListener<Drawable>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                        getActivity().supportPostponeEnterTransition();
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                        getActivity().supportStartPostponedEnterTransition();
//                        if (resource != null) {
//                            Drawable drawable = context.getResources().getDrawable(R.drawable.restaurant_menu);
//                            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
//
//                            Palette palette = Palette.from(bitmap).generate();
//                            // Use generated instance
//                            mColorPalette = palette.getMutedColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
//
////                            binder.ivRestDetailsHeader.setBackgroundColor(mColorPalette);
//                        }
//                        return false;
//                    }
//                })
                .into(binder.ivRestDetailsHeader);
        restaurantDetailsViewModel = new RestaurantDetailsViewModel(this);
        binder.cardLayoutRestDetailsHeader.setRestDetails(restaurantDetailsViewModel);

        ((AppCompatActivity) getActivity()).setSupportActionBar(binder.toolbarRestDetails);

        binder.ablRestDetails.addOnOffsetChangedListener(offsetChangeListener);

        //Restaurant Details
        binder.rvRestDetailsActions.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));
        binder.rvRestDetailsActions.addItemDecoration(new ItemsAdapter.ItemDecorate(getActivity(), R.dimen.card_margins));

        adapter = new ItemsAdapter(restaurantDetailsViewModel, this);
        binder.rvRestDetailsActions.setAdapter(adapter);

        //Restaurant Reviews
        binder.rvReviewList.setLayoutManager(new LinearLayoutManager(getActivity()));

        //This has option menu
        setHasOptionsMenu(true);

        return binder.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchRestaurantDetails();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        reviewAdapter.clean();
        restaurantDetailsViewModel.clean();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_rest_details, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Drawable drawable = item.getIcon();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
        switch (item.getItemId()) {

            case R.id.menu_share:
                showShareOptionMenu();
                break;

            case R.id.menu_fav:

                if(restaurantDetailsViewModel.isFavouriteRestaurant()) {

                    restaurantDetailsViewModel.removeFavourite(restaurantDetailsViewModel
                            .getRestaurantId(), callbackStatus);

                } else {

                    restaurantDetailsViewModel.addFavourite(restaurantDetailsViewModel.getRestaurantId(),
                            restaurantDetailsViewModel.getRestaurantName().get(),
                            restaurantDetailsViewModel.getRestaurantLocation().get(),
                            restaurantDetailsViewModel.getRestaurantCuisines().get(),
                            restaurantDetailsViewModel.getRestaurantImages().get(),
//                            restaurantDetailsViewModel.getRestaurantExpense().get(),
                            callbackStatus);
                }
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onSuccess() {

        //Display the information
        adapter.bindData(getItems());

        binder.mergePb.pbRestDetailsProgress.setVisibility(View.GONE);
        binder.ablRestDetails.setVisibility(View.VISIBLE);
        binder.nsvRestDetailsContainer.setVisibility(View.VISIBLE);

        //Now get and display the review information
        reviewAdapter = new ReviewAdapter(getArguments().getString("resid"));
        binder.rvReviewList.setAdapter(reviewAdapter);

        //Mark, if the restaurant is already a favourite
        if(restaurantDetailsViewModel.isFavouriteRestaurant()) {
            binder.toolbarRestDetails.getMenu().findItem(R.id.menu_fav).setIcon(R.drawable.ic_heart);
        }
    }

    @Override
    public void onError() {
        handleErrorResponse();
    }

    @Override
    public void onClick(int position) {

        Intent viewIntent = new Intent(Intent.ACTION_VIEW);

        switch(position) {
            case 0:
                viewIntent.setData(Uri.parse(restaurantDetailsViewModel.getMenuUrl()));
                break;

            case 1:
                viewIntent.setData(Uri.parse(restaurantDetailsViewModel.getPhotoUrl()));
                break;

            case 2:
                viewIntent.setData(Uri.parse(restaurantDetailsViewModel.getWebsiteUrl()));
                break;

            case 3:
                viewIntent.setData(Uri.parse("https://github.com/Ranawatt/Gomato"));
                break;
        }

        //Resolve and launch the intent
        if(null != viewIntent.resolveActivity(getActivity().getPackageManager())) {
            startActivity(viewIntent);
        }
    }

    private AppBarLayout.OnOffsetChangedListener offsetChangeListener = new AppBarLayout.OnOffsetChangedListener() {

        boolean isShow = false;
        int scrollRange = -1;
        int visibleRange = 0;

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

            if (scrollRange == -1) {
                scrollRange = appBarLayout.getTotalScrollRange();
                visibleRange = scrollRange / 4;
            }

            if(scrollRange + verticalOffset <= visibleRange && !isShow) {

                binder.ctRestDetails.setTitle(restaurantName);
                isShow = true;
                binder.toolbarRestDetails.getMenu().findItem(R.id.menu_share).setVisible(true);
                binder.toolbarRestDetails.getMenu().findItem(R.id.menu_fav).setVisible(true);

            } else if(scrollRange + verticalOffset > visibleRange && isShow) {

                binder.ctRestDetails.setTitle(" ");
                isShow = false;
                binder.toolbarRestDetails.getMenu().findItem(R.id.menu_share).setVisible(false);
                binder.toolbarRestDetails.getMenu().findItem(R.id.menu_fav).setVisible(false);
            }
        }
    };

    private void fetchRestaurantDetails() {

        String s = getArguments().getString("resid");
        restaurantDetailsViewModel.getRestaurantDetails(s);
    }

    private void handleErrorResponse() {

        Toast.makeText(getActivity(), R.string.could_not_fetch_generic,
                Toast.LENGTH_SHORT).show();
    }

    private ArrayList<ItemsAdapter.ItemInfo> getItems() {

        ArrayList<ItemsAdapter.ItemInfo> arrayList = new ArrayList<>(5);
        arrayList.add(new ItemsAdapter.ItemInfo(getString(R.string.menu), R.drawable.restaurant_menu));
        arrayList.add(new ItemsAdapter.ItemInfo(getString(R.string.food_image), R.drawable.food_icon));
        arrayList.add(new ItemsAdapter.ItemInfo(getString(R.string.website_url), R.drawable.web_icon));
        arrayList.add(new ItemsAdapter.ItemInfo(getString(R.string.project_link), R.drawable.link_icon));

        return arrayList;
    }

    private void showShareOptionMenu() {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, restaurantDetailsViewModel.getWebsiteUrl());
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_rest_info)));
    }

    private Favourites.ITransactionStatus callbackStatus = new Favourites.ITransactionStatus() {

        @Override
        public void onSuccess(Favourites.FavAction action) {

            if(Favourites.FavAction.ADD == action) {
                Toast.makeText(getActivity(), R.string.rest_fav_marked, Toast.LENGTH_SHORT).show();
                binder.toolbarRestDetails.getMenu().findItem(R.id.menu_fav).setIcon(R.drawable.ic_heart);
            } else {
                Toast.makeText(getActivity(), R.string.rest_fav_unmarked, Toast.LENGTH_SHORT).show();
                binder.toolbarRestDetails.getMenu().findItem(R.id.menu_fav).setIcon(R.drawable.ic_heart_outline);
            }
        }

        @Override
        public void onError(Favourites.FavAction action) {
            Toast.makeText(getActivity(), R.string.rest_fav_cannot_marked, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFavListRetrieved(LinkedList<FavouriteData> favouriteDataList) {
            //Not applicable for this screen
        }
    };
}
