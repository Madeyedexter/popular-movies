package app.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Madeyedexter on 22-04-2017.
 */

public class ReviewsList implements Parcelable {

    //The id of the movie
    String id;

    int page;

    protected ReviewsList(Parcel in) {
        id = in.readString();
        page = in.readInt();
        reviewList = in.createTypedArrayList(Review.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(page);
        dest.writeTypedList(reviewList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReviewsList> CREATOR = new Creator<ReviewsList>() {
        @Override
        public ReviewsList createFromParcel(Parcel in) {
            return new ReviewsList(in);
        }

        @Override
        public ReviewsList[] newArray(int size) {
            return new ReviewsList[size];
        }
    };

    @Override
    public String toString() {
        return "ReviewsList{" +
                "id='" + id + '\'' +
                ", page=" + page +
                ", reviewList=" + reviewList +
                '}';
    }

    public ReviewsList() {
    }

    @SerializedName("results")
    private ArrayList<Review> reviewList;

    public ArrayList<Review> getReviewList() {
        return reviewList;
    }

    public void setReviewList(ArrayList<Review> reviewList) {
        this.reviewList = reviewList;
    }
}
