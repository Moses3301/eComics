package com.example.moses.eshopping;

import android.content.Intent;
import android.os.AsyncTask;
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

import com.example.moses.eshopping.adapters.BookAdapter;
import com.example.moses.eshopping.adapters.PageAdapter;
import com.flurry.android.FlurryAgent;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class PdfViewerActivity extends AppCompatActivity {
    public static final String TAG = "$PdfViewerActivity$";
    private Book mBook;

    FirebaseDatabase mDatabase;
    DatabaseReference mPageRef;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mPageAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<String> mPageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG,"onCreate() >>");
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance();
        setContentView(R.layout.activity_pdf_viewer);
        initView();
        recyclerInit();
        getAllPages();
        Log.e(TAG,"onCreate() <<");
    }

    private void initView() {
        Log.e(TAG,"initView() >>");
        Intent intent = getIntent();
        mBook = intent.getParcelableExtra(BookDetailsActivity.EXTRA_BOOK);
        mPageList = mBook.getFile();
        Log.e(TAG,"initView() <<");
    }

    private void recyclerInit() {
        Log.e(TAG,"recyclerInit() >>");
        mRecyclerView = (RecyclerView) findViewById(R.id.page_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        Log.e(TAG,"recyclerInit() <<");
    }

    private void getAllPages() {
        Log.e(TAG,"getAllBooks() >>");
        mPageAdapter = new PageAdapter(mPageList);
        mRecyclerView.setAdapter(mPageAdapter);
        Log.e(TAG,"getAllBooks() <<");
    }
}


