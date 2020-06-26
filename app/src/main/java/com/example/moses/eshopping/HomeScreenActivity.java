package com.example.moses.eshopping;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.moses.eshopping.adapters.BookAdapter;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.LinkedHashMap;

public class HomeScreenActivity extends AppCompatActivity {
    public static final String TAG = "$HomeScreenActivity$";

    private FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mBooksRef;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mBooksAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private LinkedHashMap<String,Book> mBookList = new LinkedHashMap<>();
    private User mUser;
    private AnalyticsManager analyticsManager = AnalyticsManager.getInstance();

    EditText mSearchText;
    Button mfind;
    CheckBox mMyBooks;
    Spinner mSortingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG,"onCreate() >>");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        initView();
        getAllBooks();
        analyticsManager.init(this);
        flurryInit();
        Log.e(TAG,"onCreate() <<");
    }

    @Override
    public void onStart() {
        Log.e(TAG,"onStart() >>");
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        Log.e(TAG,"onStart() <<");

    }

    private void initView(){
        Log.e(TAG,"initView() >>");
        mAuth = FirebaseAuth.getInstance();

        mSearchText = findViewById(R.id.searchBarEditText);
        mfind = findViewById(R.id.findButton);
        mMyBooks = findViewById(R.id.myBookscheckBox);
        mMyBooks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                search();
            }
            }
        );
        mSortingSpinner = findViewById(R.id.sortingSpinner);
        mSortingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                search();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
        mfind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSortingSpinner.setSelection(0);
                search();
            }
        });
        mDatabase = FirebaseDatabase.getInstance();
        mBooksRef = mDatabase.getReference("Books");

        readUserFromData();
        recyclerInit();
        // Read from the database


        Log.e(TAG,"initView() <<");
    }

    private void readUserFromData() {
        Log.e(TAG,"readUserFromData() >>");
        Intent intent = getIntent();
        mUser = intent.getParcelableExtra(SignInActivity.EXTRA_USER);
        if (mUser!=null) {
            mUser = intent.getParcelableExtra(SignInActivity.EXTRA_USER);
            DatabaseReference userRef = mDatabase.getReference("Users/" + mUser.getId());
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mUser = dataSnapshot.getValue(User.class);
                    ((BookAdapter) mBooksAdapter).SetUser(mUser);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Log.e(TAG, mUser.getId());
        }
        else{
            mMyBooks.setEnabled(false);
        }
        Log.e(TAG,"readUserFromData() <<");
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

    private void getAllBooks() {
        Log.e(TAG,"getAllBooks() >>");
        mBookList.clear();
        mBooksAdapter = new BookAdapter(mBookList,mUser);
        mRecyclerView.setAdapter(mBooksAdapter);

        getAllSongsUsingChildListenrs();
        Log.e(TAG,"getAllBooks() <<");
    }

    private void flurryInit(){
        new FlurryAgent.Builder()
                .withLogEnabled(true)
                .build(this, "BRYBDTHT2WYRH76D4TGY");
    }

    private void getAllSongsUsingChildListenrs() {
        Log.e(TAG,"getAllSongsUsingChildListenrs() >>");
        mBooksRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName){

                Log.e(TAG, "onChildAdded(Books) >> " + snapshot.getKey());
                mBookList.put(snapshot.getKey(),snapshot.getValue(Book.class));
                mRecyclerView.getAdapter().notifyDataSetChanged();
                Log.e(TAG, "onChildAdded(Books) <<");

            }
            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName){

                Log.e(TAG, "onChildChanged(Books) >> " + snapshot.getKey());
                Book book =snapshot.getValue(Book.class);
                String key = snapshot.getKey();
                mBookList.put(key,book);
                mRecyclerView.getAdapter().notifyDataSetChanged();
                Log.e(TAG, "onChildChanged(Books) <<");

            }
            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName){

                Log.e(TAG, "onChildMoved(Books) >> " + snapshot.getKey());


                Log.e(TAG, "onChildMoved(Books) << Doing nothing");

            }
            @Override
            public void onChildRemoved(DataSnapshot snapshot){
                Log.e(TAG, "onChildRemoved(Books) >> " + snapshot.getKey());
                String key = snapshot.getKey();
                mBookList.remove(key);
                Log.e(TAG, "onChildRemoved(Books) <<");

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e(TAG, "onCancelled(Books) >>" + databaseError.getMessage());
            }
        });
        Log.e(TAG,"getAllSongsUsingChildListenrs() <<");
    }

    private void search() {
        Log.e(TAG,"search() >>");
        String searchString = null;
        if (mSearchText.getText()!=null) {
            searchString = mSearchText.getText().toString();
        }
        String orderBy = mSortingSpinner.getSelectedItem().toString();

        Query query = mBooksRef;

        mBookList.clear();

        if (searchString != null && !searchString.isEmpty()) {
            Log.e(TAG,"search title >>"+searchString);
            analyticsManager.trackSearchEvent(searchString);
            query = query.orderByChild("title").startAt(searchString).endAt(searchString + "\uf8ff");
        }
        else{
            if (!orderBy.equals("all")){
                analyticsManager.trackGenereSearchEvent(orderBy);
                Log.e(TAG,"order by "+orderBy);
                query = query.orderByChild(orderBy);
            }
        }

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Log.e(TAG, "onDataChange(Query) >> " + snapshot.getKey());

                updateBooksList(snapshot, mMyBooks.isChecked());

                Log.e(TAG, "onDataChange(Query) <<");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e(TAG, "onCancelled() >>" + databaseError.getMessage());
            }

        });
        Log.e(TAG,"search() <<");
    }

    private void updateBooksList(DataSnapshot snapshot, boolean iOnlyOwn) {
        Log.e(TAG,"updateBooksList() >>");
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            Book book = dataSnapshot.getValue(Book.class);
            Log.e(TAG, "updateSongList() >> adding book: " + book.getTitle());
            String key = dataSnapshot.getKey();

            if (iOnlyOwn){
                if (mUser!=null){
                    if (mUser.getBookList().contains(key)){
                        mBookList.put(key,book);
                    }
                }
            }
            else {
                mBookList.put(key,book);
            }
        }
        mRecyclerView.getAdapter().notifyDataSetChanged();
        Log.e(TAG,"updateBooksList() <<");

    }


}
