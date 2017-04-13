package app.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by n188851 on 12-04-2017.
 */

public class Movie implements Parcelable{


    public Movie(){

    }

    public Movie(String posterPath, String originalTitle, String overview, float averageVote, float popularity, String releaseDate) {
        this.posterPath = posterPath;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.averageVote = averageVote;
        this.popularity = popularity;
        this.releaseDate = releaseDate;
    }

    protected Movie(Parcel in) {
        posterPath = in.readString();
        originalTitle = in.readString();
        overview = in.readString();
        averageVote = in.readFloat();
        popularity = in.readFloat();
        releaseDate=in.readString();
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

    @Override

    public String toString() {
        return "Movie{" +
                "posterPath='" + posterPath + '\'' +
                ", originalTitle='" + originalTitle + '\'' +
                ", overview='" + overview + '\'' +
                ", averageVote=" + averageVote +
                ", popularity=" + popularity +
                ", releaseDate=" + releaseDate +
                '}';
    }

    @SerializedName("poster_path")
    private String posterPath;

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public float getAverageVote() {
        return averageVote;
    }

    public void setAverageVote(float averageVote) {
        this.averageVote = averageVote;
    }

    public float getPopularity() {
        return popularity;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    @SerializedName("original_title")
    private String originalTitle;

    private String overview;
    @SerializedName("vote_average")
    private float averageVote;
    private float popularity;

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @SerializedName("release_date")
    private String releaseDate;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterPath);
        dest.writeString(originalTitle);
        dest.writeString(overview);
        dest.writeFloat(averageVote);
        dest.writeFloat(popularity);
        dest.writeString(releaseDate);
    }
}
