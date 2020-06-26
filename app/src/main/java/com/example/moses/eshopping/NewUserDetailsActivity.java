package com.example.moses.eshopping;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class NewUserDetailsActivity extends AppCompatActivity {
    public static final String TAG = "$NewUserDetailsActivity$";
    public static final String EXTRA_USER = "com.example.moses.eshopping.NewUserDetailsActivity.EXTRA_USER";

    private TextView mName;
    private TextView mBirthYear;
    private Spinner mGender;
    private Button mContinue;
    private Spinner mGenre;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private AnalyticsManager analyticsManager = AnalyticsManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG,"onCreate() >>");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_details);
        mAuth = FirebaseAuth.getInstance();
        initViews();
        analyticsManager.init(this);
        Log.e(TAG,"onCreate() <<");
    }

    @Override
    public void onResume(){
        Log.e(TAG,"onResume() >>");
        super.onResume();
        //mAuth.addAuthStateListener(mAuthListener);
        Log.e(TAG,"onResume() <<");
    }

    @Override
    public void onStop(){
        Log.e(TAG,"onStop() >>");
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
        Log.e(TAG,"onStop() <<");
    }

    private void initViews() {
        Log.e(TAG,"initViews() >>");
        mName = findViewById(R.id.nameEditText);
        mBirthYear = findViewById(R.id.birthYearEditText);
        mGender = findViewById(R.id.genderSpinner);
        mContinue = findViewById(R.id.continueButton);
        mGenre = findViewById(R.id.genereSpinner);
        Log.e(TAG,"initViews() <<");
    }

    private void changeNameListener(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(mName.getText().toString()).build();
                    user.updateProfile(profileUpdates);
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void onContinueClick(View v){
        Log.e(TAG,"onContinueClick() >>");
        if(mName.getText()==null || mName.getText().toString().isEmpty()){

        }
        else if (mBirthYear.getText()==null || mBirthYear.getText().toString().isEmpty()){

        }
        else if (mGender.getSelectedItemPosition()==0){

        }
        else if (mGenre.getSelectedItemPosition()==0){

        }
        else{
            Intent intent = getIntent();
            setResult(Activity.RESULT_OK,intent);
            changeNameListener();
            analyticsManager.setUserID(mAuth.getUid());
            analyticsManager.setUserProperty("birth_year",mBirthYear.getText().toString());
            analyticsManager.setUserProperty("gender",mGender.getSelectedItem().toString());
            analyticsManager.setUserProperty("favorite_genere",mGender.getSelectedItem().toString());
            String locale = this.getResources().getConfiguration().locale.getCountry();
            analyticsManager.setUserProperty("location",locale);
            analyticsManager.setUserProperty("book_count","0");
            analyticsManager.setUserProperty("total_purchase","0");
            User user = new User(mAuth.getUid(),null);
            FirebaseDatabase.getInstance().getReference("Users/"+mAuth.getUid()).setValue(user);
            intent.putExtra(EXTRA_USER,user);
            finish();
        }
        Log.e(TAG,"onContinueClick() <<");
        }
    }