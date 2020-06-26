package com.example.moses.eshopping;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Book implements Parcelable {
    public static final String TAG = "$Book$";

    private String title;
    private String publisher;
    private String writer;
    private String artist;
    private String genere;
    private String summary;
    private String cover;
    private List<String> file = new ArrayList<>();
    private Double price;

    //Getters
    public String getTitle() {
        return title;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getWriter() {
        return writer;
    }

    public String getArtist() {
        return artist;
    }

    public String getGenere() {
        return genere;
    }

    public String getSummary() {
        return summary;
    }

    public String getCover() {
        return cover;
    }

    public List<String> getFile() {
        return file;
    }

    public Double getPrice() {
        return price;
    }
    //End Getters

    public Book() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(publisher);
        dest.writeString(writer);
        dest.writeList(file);
        dest.writeString(artist);
        dest.writeString(summary);
        dest.writeString(genere);
        dest.writeString(cover);
        dest.writeDouble(price);
    }

    public Book(Parcel in) {
        this.title = in.readString();
        this.publisher = in.readString();
        this.writer = in.readString();
        in.readList(file,String.class.getClassLoader());
        this.artist = in.readString();
        this.summary = in.readString();
        this.genere = in.readString();
        this.cover = in.readString();
        this.price = in.readDouble();
    }

    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };


}
