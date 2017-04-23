package app.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Madeyedexter on 22-04-2017.
 */

public class VideosList implements Parcelable {

    //The id of the movie
    private String id;

    protected VideosList(Parcel in) {
        id = in.readString();
        videoList = in.createTypedArrayList(Video.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeTypedList(videoList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VideosList> CREATOR = new Creator<VideosList>() {
        @Override
        public VideosList createFromParcel(Parcel in) {
            return new VideosList(in);
        }

        @Override
        public VideosList[] newArray(int size) {
            return new VideosList[size];
        }
    };

    @Override
    public String toString() {
        return "VideosList{" +
                "id='" + id + '\'' +
                ", videoList=" + videoList +
                '}';
    }

    public ArrayList<Video> getVideoList() {
        return videoList;
    }


    public void setVideoList(ArrayList<Video> videoList) {
        this.videoList = videoList;
    }

    public VideosList() {
    }

    @SerializedName("results")
    private ArrayList<Video> videoList;
}
