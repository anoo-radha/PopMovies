package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/* This class has the variables for storing the various details for each movie. This class is parcelable */
public class Movies implements Parcelable {
    int mPosterId;
    String mTitle;
    String mSynopsis;
    String mReleaseDate;
    String mPosterPath;
    float mUserRating;

    public Movies(int posterId, String title, String synopsis, String releaseDate, String posterPath, float rating) {
        this.mPosterId = posterId;
        this.mTitle = title;
        this.mSynopsis = synopsis;
        this.mReleaseDate = releaseDate;
        this.mPosterPath = posterPath;
        this.mUserRating = rating;
    }

    // to deparcel object
    public Movies(Parcel in) {
        mPosterId = in.readInt();
        mTitle = in.readString();
        mSynopsis = in.readString();
        mReleaseDate = in.readString();
        mPosterPath = in.readString();
        mUserRating = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mPosterId);
        dest.writeString(mTitle);
        dest.writeString(mSynopsis);
        dest.writeString(mReleaseDate);
        dest.writeString(mPosterPath);
        dest.writeFloat(mUserRating);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movies> CREATOR = new Parcelable.Creator<Movies>() {
        @Override
        public Movies createFromParcel(Parcel in) {
            return new Movies(in);
        }

        @Override
        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };

    public String getActualPosterPath() {
        final String BasePath = "http://image.tmdb.org/t/p/w185//";
        return BasePath + mPosterPath;
    }
}