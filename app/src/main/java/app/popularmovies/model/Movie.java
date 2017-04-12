package app.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by n188851 on 12-04-2017.
 */

public class Movie implements Parcelable{
    protected Movie(Parcel in) {
        poster_path = in.readString();
        original_title = in.readString();
        overview = in.readString();
        vote_average = in.readFloat();
        popularity = in.readFloat();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getPoster_path() {
        return poster_path;
    }

    public Movie(){

    }

    public Movie(String poster_path, String original_title, String overview, float vote_average, float popularity) {
        this.poster_path = poster_path;
        this.original_title = original_title;
        this.overview = overview;
        this.vote_average = vote_average;
        this.popularity = popularity;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "poster_path='" + poster_path + '\'' +
                ", original_title='" + original_title + '\'' +
                ", overview='" + overview + '\'' +
                ", vote_average=" + vote_average +
                ", popularity=" + popularity +
                '}';
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public float getVote_average() {
        return vote_average;
    }

    public void setVote_average(float vote_average) {
        this.vote_average = vote_average;
    }

    public float getPopularity() {
        return popularity;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    private String poster_path;
    private String original_title;
    private String overview;
    private float vote_average;
    private float popularity;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(poster_path);
        dest.writeString(original_title);
        dest.writeString(overview);
        dest.writeFloat(vote_average);
        dest.writeFloat(popularity);
    }
}
