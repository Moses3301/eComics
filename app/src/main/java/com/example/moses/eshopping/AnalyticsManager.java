package com.example.moses.eshopping;

import android.content.Context;
import android.os.Bundle;

import com.flurry.android.FlurryAgent;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;
import java.util.Map;

public class AnalyticsManager {
    private static String TAG = "AnalyticsManager";
    private static AnalyticsManager mInstance = null;
    private FirebaseAnalytics mFirebaseAnalytics;

    private AnalyticsManager() {

    }

    public static AnalyticsManager getInstance() {

        if (mInstance == null) {
            mInstance = new AnalyticsManager();
        }
        return (mInstance);
    }

    public void init(Context context) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        flurryInit(context);
    }

    public void trackSearchEvent(String searchString) {

        String eventName = "search";

        //Firebase
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.SEARCH_TERM, searchString);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH,params);

        //Flurry
        Map<String, String> eventParams = new HashMap<String, String>();
        eventParams.put("search_term", searchString);
        FlurryAgent.logEvent(eventName, eventParams);
    }

    public void trackGenereSearchEvent(String genere) {

        String eventName = "genere_search";

        //Firebase
        Bundle params = new Bundle();
        params.putString("genere", genere);
        mFirebaseAnalytics.logEvent(eventName,params);

        //Flurry
        Map<String, String> eventParams = new HashMap<String, String>();
        eventParams.put("genere", genere);
        FlurryAgent.logEvent(eventName, eventParams);
    }

    private void flurryInit(Context context){
        new FlurryAgent.Builder()
                .withLogEnabled(true)
                .build(context, "BRYBDTHT2WYRH76D4TGY");
    }

    public void trackLoginEvent(String loginMethod) {

        String eventName = "login";
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.SIGN_UP_METHOD, loginMethod);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN,params);

        //Flurry
        Map<String, String> eventParams = new HashMap<String, String>();
        eventParams.put(loginMethod, loginMethod);
        FlurryAgent.logEvent(eventName,eventParams);
    }

    public void trackBookEvent(String event , Book book) {
        Bundle params = new Bundle();

        getBookParams(params,book);
        mFirebaseAnalytics.logEvent(event,params);
        //Flurry
        Map<String, String> eventParams = new HashMap<String, String>();
        getBookParams(eventParams,book);
        FlurryAgent.logEvent(event,eventParams);
    }

    public void trackPurchase(Book book) {

        String eventName = "purchase";
        Bundle params = new Bundle();
        getBookParams(params,book);
        params.putDouble(FirebaseAnalytics.Param.PRICE,book.getPrice());
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE,params);
        //Flurry
        Map<String, String> eventParams = new HashMap<String, String>();
        getBookParams(eventParams,book);
        FlurryAgent.logEvent(eventName,eventParams);
    }

    public void trackRead(Book book) {

        String eventName = "read";
        Bundle params = new Bundle();
        getBookParams(params,book);
        mFirebaseAnalytics.logEvent(eventName,params);

        //Flurry
        Map<String, String> eventParams = new HashMap<String, String>();
        getBookParams(eventParams,book);
        FlurryAgent.logEvent(eventName,eventParams);
    }

    private void getBookParams(Bundle params, Book book){
        params.putString("book_genre", book.getGenere());
        params.putString("book_title", book.getTitle());
        params.putString("book_artist", book.getArtist());
        params.putString("book_writer", book.getWriter());
        params.putDouble("book_price",book.getPrice());
        params.putString("book_publisher",book.getPublisher());
    }

    private void getBookParams(Map<String, String> params, Book book){
        params.put("book_genre", book.getGenere());
        params.put("book_title", book.getTitle());
        params.put("book_artist", book.getArtist());
        params.put("book_writer", book.getWriter());
        params.put("book_price",book.getPrice().toString());
        params.put("book_publisher",book.getPublisher());
    }

    public void trackBookRating(Book book ,Double userRating) {

        String eventName = "book_rating";
        Bundle params = new Bundle();

        getBookParams(params,book);
        params.putDouble("book_user_rating",userRating);

        mFirebaseAnalytics.logEvent(eventName,params);

        //Flurry
        Map<String, String> eventParams = new HashMap<String, String>();
        getBookParams(eventParams,book);
        eventParams.put("book_user_rating",userRating.toString());
        FlurryAgent.logEvent(eventName,eventParams);
    }

    public void setUserID(String id) {
        mFirebaseAnalytics.setUserId(id);
        //Flurry
        FlurryAgent.setUserId(id);
    }

    public void setUserProperty(String name , String value) {
        mFirebaseAnalytics.setUserProperty(name,value);
    }

}
