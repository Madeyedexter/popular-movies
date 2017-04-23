package app.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by n188851 on 12-04-2017.
 */

public class Movie implements Parcelable{

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
    private List<Review> reviews;
    private List<Video> videos;
    private int favorite=0;
    private String id;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("original_title")
    private String originalTitle;
    private String overview;
    @SerializedName("vote_average")
    private float averageVote;
    private float popularity;
    @SerializedName("release_date")
    private String releaseDate;
    public Movie(){

    }







    protected Movie(Parcel in) {
        reviews = in.createTypedArrayList(Review.CREATOR);
        videos = in.createTypedArrayList(Video.CREATOR);
        favorite = in.readInt();
        id = in.readString();
        posterPath = in.readString();
        originalTitle = in.readString();
        overview = in.readString();
        averageVote = in.readFloat();
        popularity = in.readFloat();
        releaseDate = in.readString();
    }

    @Override
    public String toString() {
        return "Movie{" +
                "reviews=" + reviews +
                ", videos=" + videos +
                ", favorite=" + favorite +
                ", id='" + id + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", originalTitle='" + originalTitle + '\'' +
                ", overview='" + overview + '\'' +
                ", averageVote=" + averageVote +
                ", popularity=" + popularity +
                ", releaseDate='" + releaseDate + '\'' +
                '}';
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(reviews);
        dest.writeTypedList(videos);
        dest.writeInt(favorite);
        dest.writeString(id);
        dest.writeString(posterPath);
        dest.writeString(originalTitle);
        dest.writeString(overview);
        dest.writeFloat(averageVote);
        dest.writeFloat(popularity);
        dest.writeString(releaseDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite==0?0:1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }



}
