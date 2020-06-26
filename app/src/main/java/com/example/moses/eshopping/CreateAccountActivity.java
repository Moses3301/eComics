package com.example.moses.eshopping;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateAccountActivity extends AppCompatActivity {
    public static final String TAG = "$CreateAccountActivity$";
    private FirebaseAuth mAuth;

    private TextView m_Email;
    private TextView m_Password;
    private Button m_CreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate() >>");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        initViews();
        Log.e(TAG, "onCreate() <<");
    }

    private void initViews() {
        Log.e(TAG, "initViews() >>");
        m_Email = findViewById(R.id.emailEditText);
        m_Password = findViewById(R.id.passwordEditText);
        m_CreateAccount = findViewById(R.id.createAccountButton);
        mAuth = FirebaseAuth.getInstance();
        Log.e(TAG, "initViews() <<");
    }

    public void setCreateAccountOnClick(View v) {
        Log.e(TAG, "setCreateAccountOnClick() >>");
        String email = m_Email.getText().toString();
        String password = m_Password.getText().toString();
        if (m_Email.getText().toString().isEmpty()){
            Toast.makeText(CreateAccountActivity.this, R.string.email_validation_message,
                    Toast.LENGTH_SHORT).show();
        }
        else if (!verifyString("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{6,}$", password) ){
            Toast.makeText(CreateAccountActivity.this, R.string.password_validation_message,
                    Toast.LENGTH_SHORT).show();
        }
        else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.e(TAG, "createUserWithEmail:success");
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users/"+mAuth.getUid());
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.e(TAG, "createUserWithEmail:failure", task.getException());
                                FirebaseAuthException error = (FirebaseAuthException)task.getException();
                                String errorCode = error.getErrorCode();
                                String message = getResources().getString(R.string.authentication_failed);
                                switch (errorCode){
                                    case "ERROR_INVALID_EMAIL":
                                        message = getResources().getString(R.string.email_validation_message);
                                        break;
                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                        message = getResources().getString(R.string.email_in_use);
                                        break;
                                    case "ERROR_WEAK_PASSWORD":
                                        message = getResources().getString(R.string.password_validation_message);
                                        break;
                                }
                                Toast.makeText(CreateAccountActivity.this, message,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        Log.e(TAG, "setCreateAccountOnClick() <<");
    }
    private boolean verifyString(String regExpn, String str){
        Log.e(TAG,"verifyString(string*,string*) >>");
        boolean isVerify = false;

        if (!str.isEmpty()) {
            Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(str);
            isVerify = matcher.matches();
        }
        Log.e(TAG, "verifyString(string*,string*) return: " + isVerify);
        Log.e(TAG, "verifyString(string*,string*) <<");
        return (isVerify);
    }
}
