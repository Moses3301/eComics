package com.example.moses.eshopping.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.moses.eshopping.Book;
import com.example.moses.eshopping.BookDetailsActivity;
import com.example.moses.eshopping.R;
import com.example.moses.eshopping.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.PageViewHolder> {
    public static final String TAG = "$PageAdapter$";
    private List<String> mPageList = new ArrayList<>();

    public class PageViewHolder extends RecyclerView.ViewHolder {
        public static final String TAG = "$PageViewHolder$";
        private ImageView pageImage;
        private CardView mPageCardView;
        private Context mContext;
        private String mPageFile;

        public PageViewHolder(final Context mContext, View view) {
            super(view);
            Log.e(TAG,"PageViewHolder() >>");
            mPageCardView = (CardView) view.findViewById(R.id.card_view_page);
            this.mContext = mContext;
            pageImage = view.findViewById(R.id.pageImageView);
            mPageCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Show();
                }
            });
            Log.e(TAG,"PageViewHolder() <<");
        }
        public void Show(){
            RequestOptions options = new RequestOptions().optionalFitCenter()
                    .placeholder(R.drawable.sand_clock)
                    .error(R.drawable.error_404);

            Glide.with(mContext).load(mPageFile).apply(options).into(pageImage);
        }
        public void setImg(String img){
            mPageFile = img;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PageAdapter(List<String> iBookList) {
        Log.e(TAG,"PageAdapter()>>");
        mPageList = iBookList;
        Log.e(TAG,"PageAdapter()<<");
    }

    @Override
    public PageAdapter.PageViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.page_item, parent, false);
        PageAdapter.PageViewHolder vh = new PageAdapter.PageViewHolder(parent.getContext(),v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(PageAdapter.PageViewHolder holder, int position) {
        holder.setImg(mPageList.get(position));
        holder.Show();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mPageList.size();
    }
}
