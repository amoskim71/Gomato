package com.example.gomato.viewadapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gomato.BuildConfig;
import com.example.gomato.R;
import com.example.gomato.databinding.CardReviewBinding;
import com.example.gomato.model.ReviewModel;
import com.example.gomato.model.reviews.Review;
import com.example.gomato.model.reviews.ReviewResponse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

        private ReviewModel reviewModel;
        private DisposableObserver<ReviewResponse> disposableObserver;
        private ReviewResponse reviewResponse;

    public ReviewAdapter(final String resId) {

            reviewModel = new ReviewModel(BuildConfig.ZOMATO_BASE_URL);

            disposableObserver = reviewModel.getReviews(resId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<ReviewResponse>() {
                        @Override
                        public void onNext(ReviewResponse response) {
                            reviewResponse = response;
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }

        @Override
        public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new ReviewViewHolder(DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()), R.layout.card_review,
                    parent, false));
        }

        @Override
        public void onBindViewHolder(ReviewViewHolder holder, int position) {
            holder.onBind(position);
        }

        @Override
        public int getItemCount() {
            return null == reviewResponse ? 0 : reviewResponse.getUserReviews().size();
        }

        public void clean() {

            if(null != disposableObserver && !disposableObserver.isDisposed()) {
                disposableObserver.dispose();
            }
        }

        class ReviewViewHolder extends RecyclerView.ViewHolder {

            private CardReviewBinding binder;

            ReviewViewHolder(CardReviewBinding binder) {
                super(binder.getRoot());
                this.binder = binder;
            }

            void onBind(int position) {

                Review review = reviewResponse.getUserReviews().get(position).getReview();

                binder.rbRating.setRating(((float) review.getRating()));
                binder.tvRatingText.setText(review.getRatingText());
                binder.tvReviewerName.setText(review.getUser().getName());
                binder.tvReviewDate.setText(review.getReviewTimeFriendly());
                binder.tvReviewContent.setText(review.getReviewText());
            }
        }
}
