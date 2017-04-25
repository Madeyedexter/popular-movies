package app.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Madeyedexter on 25-04-2017.
 */

public class End implements Parcelable {
    protected End(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<End> CREATOR = new Creator<End>() {
        @Override
        public End createFromParcel(Parcel in) {
            return new End(in);
        }

        @Override
        public End[] newArray(int size) {
            return new End[size];
        }
    };
}
