package com.example.android.movieinfo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Singhal on 10/31/2015.
 */
public class MovieParse implements Parcelable {

    private String title;
    private String poster;
    private String overview;
    private String voteAverage;
    private String releaseDate;

    public MovieParse(String title, String poster, String overview,
                 String voteAverage, String releaseDate){
        this.title = title;
        this.poster = poster;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster() {
        return poster;
    }

    public String getOverview() {
        return overview;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeString(poster);
        out.writeString(overview);
        out.writeString(voteAverage);
        out.writeString(releaseDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private MovieParse(Parcel in) {
        title = in.readString();
        poster = in.readString();
        overview = in.readString();
        voteAverage = in.readString();
        releaseDate = in.readString();
    }

    public static final Parcelable.Creator<MovieParse> CREATOR = new Parcelable.Creator<MovieParse>() {
        public MovieParse createFromParcel(Parcel in) {
            return new MovieParse(in);
        }

        public MovieParse[] newArray(int size) {
            return new MovieParse[size];
        }
    };
}
