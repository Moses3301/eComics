package com.example.moses.eshopping.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.moses.eshopping.R;
import com.example.moses.eshopping.Review;

import java.util.LinkedHashMap;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{
    public static final String TAG="$ReviewAdapter$";
    private LinkedHashMap<String, Review> mReviewList=new LinkedHashMap<>();

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        TextView userName;
        RatingBar ratingBar;

        public ReviewViewHolder(final Context mContext, View view) {
            super(view);

            Log.e(TAG,"ReviewViewHolder() >>");

            text = (TextView) view.findViewById(R.id.reviewTextView);
            userName = (TextView) view.findViewById(R.id.usernameTextView);
            ratingBar = (RatingBar) view.findViewById(R.id.reviewRatingBar);
            Log.e(TAG,"ReviewViewHolder() <<");
        }

        public void setReview(Review review) {
            ratingBar.setRating(review.getRating().floatValue());
            text.setText(review.getText());
            userName.setText(review.getUserName());
        }
    }

    public ReviewAdapter(LinkedHashMap<String,Review> iReviewList) {
        Log.e(TAG,"ReviewAdapter()>>");
        mReviewList = iReviewList;
        Log.e(TAG,"ReviewAdapter()<<");
    }

    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        ReviewAdapter.ReviewViewHolder vh = new ReviewAdapter.ReviewViewHolder(parent.getContext(),v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ReviewAdapter.ReviewViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        String key = (String)mReviewList.keySet().toArray()[position];
        Review review = mReviewList.get(key);


        holder.setReview(review);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mReviewList.size();
    }

}