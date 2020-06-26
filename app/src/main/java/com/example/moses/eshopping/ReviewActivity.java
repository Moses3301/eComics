package com.example.moses.eshopping;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReviewActivity extends AppCompatActivity {
    public static final String TAG = "$ReviewActivity$";

    RatingBar mRatingBar;
    EditText mText;
    Button mSubmit;
    Book mBook;
    String bookKey;

    Double mSumRating = 0.0;
    int mSumReviews = 0;

    DatabaseReference mReviewsRef;
    FirebaseDatabase mDatabase;

    private AnalyticsManager analyticsManager = AnalyticsManager.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        initViews();
    }

    private void initViews() {
        mRatingBar = findViewById(R.id.newReviewRatingBar);
        Intent intent = getIntent();
        analyticsManager.init(this);
        bookKey = intent.getStringExtra(BookDetailsActivity.EXTRA_KEY);

        mText =findViewById(R.id.newReviewEditText);
        mSubmit =findViewById(R.id.newReviewButton);
        mDatabase = FirebaseDatabase.getInstance();
        mReviewsRef = mDatabase.getReference("Reviews/"+bookKey+"/reviews");
        mReviewsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e(TAG,"onChildAdded(review)"+dataSnapshot.getKey());
                Review review = dataSnapshot.getValue(Review.class);
                if (review!=null){
                    addToSum(review.getRating());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e(TAG,"onChildChanged(review)"+dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference mBookRef = mDatabase.getReference("Books/" + bookKey);
        mBookRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mBook = dataSnapshot.getValue(Book.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Review review = new Review(mRatingBar.getRating(),FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),mText.getText());
                Double rate = Double.valueOf(mRatingBar.getRating());
                analyticsManager.trackBookRating(mBook,rate);
                String user = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                String text = mText.getText().toString();
                mReviewsRef.push().setValue(new Review(rate,user,text));

                mSumReviews++;
                mSumRating+=Double.valueOf(mRatingBar.getRating());
                mDatabase.getReference("Reviews/"+bookKey+"/rating").setValue(mSumRating/mSumReviews);
                finish();
            }
        });
    }

    void addToSum(Double iNum){
        mSumRating+=iNum;
        mSumReviews++;
        Log.e(TAG,"addToSum()"+mSumRating+ "("+mSumReviews+")");
    }
}
