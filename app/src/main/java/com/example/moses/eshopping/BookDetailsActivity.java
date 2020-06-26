package com.example.moses.eshopping;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.moses.eshopping.adapters.BookAdapter;
import com.example.moses.eshopping.adapters.ReviewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedHashMap;

public class BookDetailsActivity extends AppCompatActivity {
    public static final String TAG = "$BookDetailsActivity$";
    public static final String EXTRA_BOOK = "com.example.moses.eshopping.BookDetailsActivity.EXTRA_BOOK";
    public static final String EXTRA_KEY = "com.example.moses.eshopping.BookDetailsActivity.KEY";

    Book mBook;
    User mUser;
    String mBookKey;

    ImageView mCover;
    TextView mTitle;
    TextView mWriter;
    TextView mArtist;
    TextView mSummery;
    Button mPayRead;
    TextView mPublisher;
    TextView mRating;
    TextView mGenere;
    RatingBar mRatingBar;
    FloatingActionButton fab;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mReviewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    FirebaseDatabase mDatabase;
    DatabaseReference mReviewsRef;
    private LinkedHashMap<String,Review> mReviewList = new LinkedHashMap<>();
    private AnalyticsManager analyticsManager = AnalyticsManager.getInstance();

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate() >>");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookDetailsActivity.this,ReviewActivity.class);
                intent.putExtra(EXTRA_KEY,mBookKey);
                startActivity(intent);
            }
        });
        fab.setVisibility(View.GONE);
        initViews();
        updateUI();
        mDatabase = FirebaseDatabase.getInstance();
        mReviewsRef = mDatabase.getReference("Reviews/"+mBookKey+"/reviews");
        analyticsManager.init(this);
        getAllReviews();

        Log.e(TAG, "onCreate() <<");
    }

    @Override
    protected void onStart(){
        super.onStart();
        analyticsManager.trackBookEvent("book_view",mBook);
    }

    private  void initViews(){
        Log.e(TAG, "initViews() <<");
        mCover = findViewById(R.id.coverImageView);
        mTitle = findViewById(R.id.titleTextView);
        mWriter = findViewById(R.id.writerTextView);
        mArtist = findViewById(R.id.artistTextView);
        mSummery = findViewById(R.id.summeryTextView);
        mPayRead = findViewById(R.id.payReadButton);
        mPublisher = findViewById(R.id.publisherTextView);
        mRating = findViewById(R.id.rateTextView);
        mGenere = findViewById(R.id.genereTextView);
        mRatingBar = findViewById(R.id.ratingBar);

        recyclerInit();
        Log.e(TAG, "initViews() >>");
    }
    private void recyclerInit() {
        Log.e(TAG,"recyclerInit() >>");

        mRecyclerView = (RecyclerView) findViewById(R.id.books_list);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        Log.e(TAG,"recyclerInit() <<");
    }
    private void getAllReviews() {
        Log.e(TAG,"getAllReviews() >>");
        mReviewList.clear();
        mReviewAdapter = new ReviewAdapter(mReviewList);
        mRecyclerView.setAdapter(mReviewAdapter);

        getAllReviewsUsingChildListenrs();
        Log.e(TAG,"getAllReviews() <<");
    }

    private void getAllReviewsUsingChildListenrs() {
        Log.e(TAG,"getAllReviewsUsingChildListenrs() >>");
        mReviewsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName){

                Log.e(TAG, "onChildAdded(Books) >> " + snapshot.getKey());
                mReviewList.put(snapshot.getKey(),snapshot.getValue(Review.class));
                mRecyclerView.getAdapter().notifyDataSetChanged();
                Log.e(TAG, "onChildAdded(Books) <<");

            }
            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName){

                Log.e(TAG, "onChildChanged(review) >> " + snapshot.getKey());
                Review review =snapshot.getValue(Review.class);
                String key = snapshot.getKey();
                mReviewList.put(key,review);
                mRecyclerView.getAdapter().notifyDataSetChanged();
                Log.e(TAG, "onChildChanged(review) <<");

            }
            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName){

                Log.e(TAG, "onChildMoved(review) >> " );


                Log.e(TAG, "onChildMoved(review) << Doing nothing");

            }
            @Override
            public void onChildRemoved(DataSnapshot snapshot){
                Log.e(TAG, "onChildRemoved(Books) >> " + snapshot.getKey());
                String key = snapshot.getKey();
                mReviewList.remove(key);
                Log.e(TAG, "onChildRemoved(Books) <<");

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e(TAG, "onCancelled(Books) >>" + databaseError.getMessage());
            }
        });
        Log.e(TAG,"getAllReviewsUsingChildListenrs() <<");
    }


    private  void updateUI(){
        Log.e(TAG, "updateUI() >>");
        Intent intent = getIntent();
        mBook = intent.getParcelableExtra(BookAdapter.BookViewHolder.BOOK_EXTRA_MESSAGE);
        mUser = intent.getParcelableExtra(BookAdapter.BookViewHolder.USER_EXTRA_MESSAGE);
        mBookKey = intent.getStringExtra(BookAdapter.BookViewHolder.KEY_EXTRA_MESSAGE);
        Log.e(TAG, mBook.toString());
        mTitle.setText(mBook.getTitle());
        mWriter.setText(mBook.getWriter());
        mArtist.setText(mBook.getArtist());
        mSummery.setText(mBook.getSummary());
        mPublisher.setText(mBook.getPublisher());
        mGenere.setText(mBook.getGenere());
        mPayRead.setText(mBook.getPrice().toString());
        Log.e(TAG, mBook.getCover());

        RequestOptions options = new RequestOptions().optionalFitCenter()
                .placeholder(R.drawable.cover)
                .error(R.drawable.cover);

        Glide.with(this).load(mBook.getCover()).apply(options).into(mCover);

        if (mUser != null) {
            if (mUser.getBookList().contains(mBookKey)) {
                setToReadable();
            }
        }

        mPayRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUser==null){
                    FirebaseAuth.getInstance().signOut();
                    Intent intent1 = new Intent(BookDetailsActivity.this, SignInActivity.class);
                    startActivity(intent1);
                    }
                    else {
                    mUser.getBookList().add(mBookKey);
                    mUser.addPurchase(mBook.getPrice());
                    analyticsManager.setUserProperty("book_count", String.valueOf(mUser.getBookList().size()));
                    analyticsManager.trackPurchase(mBook);
                    analyticsManager.setUserProperty("total_purchase", mUser.getTotalPurchases().toString());
                    mPayRead.setOnClickListener(null);
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
                    userRef.child(mUser.getId()).setValue(mUser);
                    setToReadable();
                    } }
                });

        if (mUser!=null){
            Log.e(TAG, "mUser"+mUser.getId());
            if (mUser.getBookList().contains(mBookKey)){
                Log.e(TAG, "mUser.getBookList().contains(mBookKey)"+mBookKey);
                fab.setVisibility(View.VISIBLE);
            }
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Reviews/"+mBookKey+"/rating");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot!=null){
                    Double newRate = dataSnapshot.getValue(Double.class);
                    if (newRate!=null){
                        mRatingBar.setRating(newRate.floatValue());
                        mRating.setText(String.format ("%.2f", newRate.floatValue()));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        Log.e(TAG, "updateUI() >>");
    }

    private void setToReadable() {
        mPayRead.setOnClickListener(null);
        mPayRead.setText("Read");
        mPayRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                analyticsManager.trackRead(mBook);
                Intent intent = new Intent(BookDetailsActivity.this, PdfViewerActivity.class);
                intent.putExtra(EXTRA_BOOK,mBook);
                startActivity(intent);
            }
        });
        fab.setVisibility(View.VISIBLE);
    }


}
