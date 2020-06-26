package com.example.moses.eshopping.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.example.moses.eshopping.Review;
import com.example.moses.eshopping.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedHashMap;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder>{
    public static final String TAG = "$BookAdapter$";
    private LinkedHashMap<String,Book> mBookList = new LinkedHashMap<>();
    private User mUser;

    public class BookViewHolder extends RecyclerView.ViewHolder {
        public static final String TAG = "$BookViewHolder$";
        public static final String BOOK_EXTRA_MESSAGE = "com.example.moses.eshopping.adapters.BookViewHolder.BOOK";
        public static final String USER_EXTRA_MESSAGE = "com.example.moses.eshopping.adapters.BookViewHolder.USER";
        public static final String KEY_EXTRA_MESSAGE = "com.example.moses.eshopping.adapters.BookViewHolder.KEY";

        private CardView mBookCardView;
        private Context mContext;
        private Book mBook;
        private String mBookKey;

        private ImageView coverImage;
        private TextView name;
        private TextView genre;
        private TextView writer;
        private TextView publisher;
        private TextView price;
        private TextView reviewsCount;
        private String bookFile;

        private RatingBar rating;

        public BookViewHolder(final Context mContext, View view) {
            super(view);

            Log.e(TAG,"BookViewHolder() >>");

            mBookCardView = (CardView) view.findViewById(R.id.card_view_book);
            coverImage = (ImageView) view.findViewById(R.id.coverImageView);
            name = (TextView) view.findViewById(R.id.nameTextView);
            rating = (RatingBar) view.findViewById(R.id.book_rating);
            genre =  (TextView) view.findViewById(R.id.genereTextView);
            price =  (TextView) view.findViewById(R.id.priceTextView);
            writer = (TextView) view.findViewById(R.id.writerTextView);
            publisher = (TextView) view.findViewById(R.id.publisherTextView);
            this.mContext = mContext;

            mBookCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e(TAG, "CardView.onClick() >> name=" + mBook.getTitle());

                    Context context = view.getContext();
                    Intent intent = new Intent(context, BookDetailsActivity.class);
                    intent.putExtra(BOOK_EXTRA_MESSAGE, mBook);
                    intent.putExtra(USER_EXTRA_MESSAGE, mUser);
                    intent.putExtra(KEY_EXTRA_MESSAGE, mBookKey);

                    context.startActivity(intent);
                }
            });
            Log.e(TAG,"BookViewHolder() <<");
        }


        public void setBook(String ikey, Book iBook) {
            Log.e(TAG,"setBook: "+ ikey);
            mBook = iBook;
            mBookKey = ikey;
            name.setText(mBook.getTitle());
            genre.setText(mBook.getGenere());
            price.setText(mBook.getPrice().toString());
            writer.setText(mBook.getWriter());
            publisher.setText(mBook.getPublisher());
            RequestOptions options = new RequestOptions().optionalFitCenter()
                        .placeholder(R.drawable.cover)
                        .error(R.drawable.cover);

            Glide.with(mBookCardView.getContext()).load(mBook.getCover()).apply(options).into(coverImage);
            DatabaseReference reviewRef = FirebaseDatabase.getInstance().getReference("Reviews/"+mBookKey+"/rating");
            reviewRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Double rate = dataSnapshot.getValue(Double.class);
                    if (rate!=null){
                        rating.setRating(rate.floatValue());
                    }
                    else{
                        rating.setRating(0);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Log.e(TAG,"setBook() <<");
        }

        public void setToReadable() {
            Log.e(TAG,"setToReadable: "+ mBook.getTitle());
            price.setVisibility(View.GONE);
        }

        public void setToUnReadable() {
            Log.e(TAG,"setToReadable: "+ mBook.getTitle());
            price.setVisibility(View.VISIBLE);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public BookAdapter(LinkedHashMap<String,Book> iBookList, User iUser) {
        Log.e(TAG,"BookAdapter()>>");
        mBookList = iBookList;
        mUser = iUser;

        Log.e(TAG,"BookAdapter()<<");
    }

    public void SetUser(User iUser){
        Log.e(TAG,"SetUser() >> "+iUser.getId());
        mUser = iUser;
        Log.e(TAG,"SetUser() <<");
    }

    @Override
    public BookAdapter.BookViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_item, parent, false);
        BookAdapter.BookViewHolder vh = new BookAdapter.BookViewHolder(parent.getContext(),v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(BookAdapter.BookViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        String key = (String)mBookList.keySet().toArray()[position];
        Book book = mBookList.get(key);


        holder.setBook(key,book);

        if (mUser!=null){
            if (mUser.getBookList().contains(key)){
                holder.setToReadable();
            }
            else{
                holder.setToUnReadable();
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mBookList.size();
    }
}
