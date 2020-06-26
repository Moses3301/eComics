package com.example.moses.eshopping;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;


public class SignInActivity extends AppCompatActivity {
    public static final String TAG = "$SignInActivity$";
    static final int NEW_USER_REQUEST = 1;
    public static final String EXTRA_USER = "com.example.moses.eshopping.SignInActivity.USER";
    private static final int RC_SIGN_IN = 1001;
    private FirebaseAuth mAuth;

    private AnalyticsManager analyticsManager = AnalyticsManager.getInstance();
    private GoogleSignInClient mGoogleSignInClient;

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    FirebaseDatabase mDatabase;

    private TextView m_Email;
    private TextView m_Password;
    private Button m_resetPassword;
    private User mUser;
    DatabaseReference mUserRef;
    ValueEventListener userListner;

    @Override
    public void onStart() {
        Log.e(TAG, "onStart() >>");
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null){
            userDatabase();
        }
        Log.e(TAG, "onStart() <<");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate() >>");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initViews();
        analyticsManager.init(this);
        Log.e(TAG, "onCreate() <<");

    }

    private void initViews() {
        Log.e(TAG, "initViews() >>");

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        mAuth = FirebaseAuth.getInstance();
        m_Email = findViewById(R.id.emailEditText);
        m_Password = findViewById(R.id.passwordEditText);
        m_resetPassword = findViewById(R.id.forgotPasswordButton);
        mDatabase = FirebaseDatabase.getInstance();
        asAGustInit();
        googleSigninInit();
        faceBookInit();
        Log.e(TAG, "initViews() <<");
    }

    private void googleSigninInit() {
        Log.e(TAG, "googleSigninInit() >>");
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestProfile()
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.googleSignInButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onGooglesignIn();
            }
        });
        Log.e(TAG, "googleSigninInit() <<");
    }

    private void faceBookInit() {
        Log.e(TAG, "faceBookInit() >>");
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(TAG, "facebook:onSuccess () >>" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
                Log.e(TAG, "facebook:onSuccess () <<");
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e(TAG, "facebook:onError" + exception.getMessage());
            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    mAuth.signOut();
                    Log.e(TAG, "facebook signOut");
                }
                Log.e(TAG, "onCurrentAccessTokenChanged() >> currentAccessToken=" +
                        (currentAccessToken != null ? currentAccessToken.getToken() : "Null") +
                        " ,oldAccessToken=" +
                        (oldAccessToken != null ? oldAccessToken.getToken() : "Null"));
            }
        };
        Log.e(TAG, "faceBookInit() <<");
    }

    public void onGooglesignIn() {
        Log.e(TAG, "onGooglesignIn() >>");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        Log.e(TAG, "onGooglesignIn() <<");
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        Log.e(TAG, "handleSignInResult() >>");
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            Log.e(TAG, account.getEmail().toString());
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
        Log.e(TAG, "handleSignInResult() <<");
    }

    public void SetSignInOnClick(View v) {
        Log.e(TAG, "SetSignInOnClick() >>");
        String email = m_Email.getText().toString();
        String password = m_Password.getText().toString();
        if (m_Email.getText().toString().isEmpty()) {
            Toast.makeText(SignInActivity.this, R.string.please_enter_email,
                    Toast.LENGTH_SHORT).show();
        } else if (m_Password.getText().toString().isEmpty()) {
            Toast.makeText(SignInActivity.this, R.string.please_enter_password,
                    Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.e(TAG, "signInWithEmail:success");
                                userDatabase();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.e(TAG, "signInWithEmail:failure", task.getException());
                                FirebaseAuthException error = (FirebaseAuthException) task.getException();
                                String errorCode = error.getErrorCode();
                                String message = getResources().getString(R.string.authentication_failed);
                                switch (errorCode) {
                                    case "ERROR_WRONG_PASSWORD":
                                        message = getResources().getString(R.string.wrong_password);
                                        break;
                                    case "ERROR_USER_NOT_FOUND":
                                        message = getResources().getString(R.string.user_not_found);
                                        break;
                                }
                                Toast.makeText(SignInActivity.this, message,
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
        Log.e(TAG, "SetSignInOnClick() <<");
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.e(TAG, "firebaseAuthWithGoogle() >>");
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithCredential:success");
                            analyticsManager.trackLoginEvent("google_login");
                            userDatabase();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
        Log.e(TAG, "firebaseAuthWithGoogle() <<");
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.e(TAG, "handleFacebookAccessToken () >>" + token.getToken());

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            analyticsManager.trackLoginEvent("facebook_login");
                            userDatabase();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        Log.e(TAG, "handleFacebookAccessToken () <<");
    }

    public void SetCreateAccountOnClick(View v) {
        Log.e(TAG, "SetCreateAccountOnClick() >>");
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
        mUser = new User(mAuth.getUid(), null);
        Log.e(TAG, "SetCreateAccountOnClick() <<");
    }

    public void SetResetPasswordOnClick(View v) {
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        startActivity(intent);
    }

    private void userDatabase(){
            mUserRef = mDatabase.getReference("Users/" + mAuth.getCurrentUser().getUid());
            userListner = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!mAuth.getCurrentUser().isAnonymous()) {
                        if (!dataSnapshot.exists()) {
                            Log.e(TAG, "new user: " + mAuth.getUid());
                            Intent newUserIntent = new Intent(SignInActivity.this, NewUserDetailsActivity.class);
                            startActivityForResult(newUserIntent, NEW_USER_REQUEST);
                        } else {
                            mUser = dataSnapshot.getValue(User.class);
                            Log.e(TAG, "user : "+mUser.getId());
                            updateUI();
                        }
                    } else {
                        Log.e(TAG, "user is Anonymous");
                        mUser = null;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
            mUserRef.addValueEventListener(userListner);

        Log.e(TAG, "userDatabase() <<");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == NEW_USER_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                mUser = data.getParcelableExtra(NewUserDetailsActivity.EXTRA_USER);
                updateUI();
            }
        }
    }

    private void updateUI() {
        Log.e(TAG, "updateUI() >>");
        Intent intent = new Intent(this, HomeScreenActivity.class);
        intent.putExtra(EXTRA_USER,mUser);
        startActivity(intent);
        finish();
    }

    @Override
    public  void onStop(){
        super.onStop();
        if(userListner!=null){
            mUserRef.removeEventListener(userListner);
        }
    }

    public void onGustClick(View v) {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void asAGustInit(){
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
        Button m_asAGust = findViewById(R.id.gustbutton);
        if (mFirebaseRemoteConfig.getBoolean("allow_annoymous_user")){
            m_asAGust.setVisibility(View.VISIBLE);
        }
    }

}