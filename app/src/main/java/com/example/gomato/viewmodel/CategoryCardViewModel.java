package com.example.gomato.viewmodel;

import android.text.TextUtils;
import android.widget.ImageView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;

import com.bumptech.glide.Glide;
import com.example.gomato.R;
import com.example.gomato.common.DefaultCuisineImage;


public class CategoryCardViewModel extends BaseObservable {

        private ObservableField<String> name;
        private ObservableField<String> location;
        private ObservableField<String> cuisine;
        private ObservableField<String> featureImage;

    public CategoryCardViewModel() {
            super();

            name = new ObservableField<>();
            location = new ObservableField<>();
            cuisine = new ObservableField<>();
            featureImage = new ObservableField<>();
        }

        @Bindable
        public ObservableField<String> getName() {
            return name;
        }

        public void setName(String name) {
            this.name.set(name);
        }

        @Bindable
        public ObservableField<String> getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location.set(location);
        }

        @Bindable
        public ObservableField<String> getCuisine() {
            return cuisine;
        }

        public void setCuisine(String cuisine) {
            this.cuisine.set(cuisine);
        }

        @Bindable
        public ObservableField<String> getFeatureImage() {
            return featureImage;
        }

        public void setFeatureImage(String featureImage) {
            this.featureImage.set(featureImage);
        }

        @BindingAdapter({"featureImage", "cuisines"})
        public static void featureImage(ImageView imageView, String url, String cuisines) {

            //If URL has been provided, then load using Picasso
            if(!TextUtils.isEmpty(url)) {
                Glide.with(imageView.getContext())
                        .load(url)
                        .placeholder(R.drawable.default_card_bg)
                        .error(R.drawable.default_card_bg)
                        .into(imageView);

                return;
            }

            //If the URL is invalid, then we can use local images based on the cuisine
            String cuisine = cuisines.split(",")[0];
            imageView.setImageDrawable(imageView.getContext().getResources().getDrawable(
                    DefaultCuisineImage.getCuisineImage(cuisine)));
        }
}
