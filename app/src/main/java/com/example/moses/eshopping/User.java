package com.example.moses.eshopping;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable {
    private String id;
    private ArrayList<String> bookList = new ArrayList<>();

    public Double getTotalPurchases() {
        return totalPurchases;
    }

    public void addPurchase(Double iPrice) {
        totalPurchases += iPrice;
    }

    Double totalPurchases = 0.0;

    public User(String iId, ArrayList<String> iBookList) {
        this.id = iId;
        if (iBookList!=null){
            this.bookList = iBookList;
        }
    }

    public User() {
    }

    public String getId() {
        return id;
    }
    public ArrayList<String> getBookList() {
        return bookList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeList(bookList);
        dest.writeDouble(totalPurchases);
    }

    public User(Parcel in) {
        this.id = in.readString();
        in.readList(bookList,String.class.getClassLoader());
        this.totalPurchases = in.readDouble();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };


}
