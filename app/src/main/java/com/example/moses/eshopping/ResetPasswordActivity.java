package com.example.moses.eshopping;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetPasswordActivity extends AppCompatActivity {
    public static final String TAG = "$ResetPasswordActivity$";

    TextView m_email;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG,"onCreate() >>");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initViews();
        Log.e(TAG,"onCreate() <<");
    }

    private void initViews(){
        Log.e(TAG,"initViews() >>");
        m_email = findViewById(R.id.emailEditText);
        mAuth = FirebaseAuth.getInstance();
        Log.e(TAG,"initViews() <<");
    }

    public void SetResetPasswordOnClick(View v){
        Log.e(TAG,"SetResetPasswordOnClick() >>");
        if (m_email.getText().toString().isEmpty()){
            Toast.makeText(ResetPasswordActivity.this, R.string.please_enter_email,
                    Toast.LENGTH_SHORT).show();
        }
        else {
            mAuth.sendPasswordResetEmail(m_email.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.e(TAG, "Email sent.");
                            } else {
                                Toast.makeText(ResetPasswordActivity.this, task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        Log.e(TAG,"SetResetPasswordOnClick() <<");
    }

    public void SetDoneOnClick(View v){
        Log.e(TAG,"SetDoneOnClick() >>");
        finish();
        Log.e(TAG,"SetDoneOnClick() <<");
    }
}
