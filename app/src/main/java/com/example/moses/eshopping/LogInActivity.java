package com.example.moses.eshopping;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class LogInActivity extends AppCompatActivity {
    public static final String TAG = "$LogInActivity$";
    private FirebaseAuth mAuth;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private Button m_asAGust;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG,"onCreate() >>");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        initViews();
        Log.e(TAG,"onCreate() <<");
    }

    public void SetAsAGustOnClick(View v){
        Intent intent = new Intent(this,HomeScreenActivity.class);
        startActivity(intent);
        finish();
    }

    private void initViews(){
        Log.e(TAG,"initViews() >>");
        mAuth = FirebaseAuth.getInstance();
        asAGustInit();
        Log.e(TAG,"initViews() <<");
    }

    public void asAGustInit(){
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(LogInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.fetch(3600)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "Fetch Succeeded: "+task.isSuccessful());
                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            Log.e(TAG, "Fetch Failed");
                        }
                    }
                });
        m_asAGust = findViewById(R.id.asAGustButton);
        if (mFirebaseRemoteConfig.getBoolean("allow_annoymous_user")){
            m_asAGust.setVisibility(View.VISIBLE);
        }
    }
/*
    public void SetCreateAccountOnClick(View v){
        Log.e(TAG,"SetCreateAccountOnClick() >>");
        Intent intent = new Intent(this,CreateAccountActivity.class);
        startActivity(intent);
        Log.e(TAG,"SetCreateAccountOnClick() <<");
    }
*/
    public void SetSignInOnClick(View v){
        Log.e(TAG,"SetSignInOnClick() >>");
        Intent intent = new Intent(this,SignInActivity.class);
        startActivity(intent);
        Log.e(TAG,"SetSignInOnClick() <<");
    }

    @Override
    public void onStart() {
        Log.e(TAG,"onStart() >>");
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.e(TAG,"UserLogin: " + currentUser);
        if ( currentUser != null){
            Intent intent = new Intent(this,HomeScreenActivity.class);
            startActivity(intent);
            finish();
        }
        Log.e(TAG,"onStart() <<");
    }
}
