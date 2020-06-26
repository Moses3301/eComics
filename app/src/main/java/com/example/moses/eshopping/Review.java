package com.example.moses.eshopping;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {
    public static final String TAG = "$Review$";

    Double rating;
    String text;
    String userName;

    //Getters
    public Double getRating() {
        return rating;
    }

    public String getText() {
        return text;
    }

    public String getUserName() {
        return userName;
    }
    //End Getters

    public Review() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Review(Double iRating, String iUsername, String iText) {
        rating = iRating;
        userName = iUsername;
        text = iText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(rating);
        dest.writeString(text);
        dest.writeString(userName);
    }

    public Review(Parcel in) {
        this.rating = in.readDouble() ;
        this.text = in.readString();
        this.userName = in.readString();
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

}
