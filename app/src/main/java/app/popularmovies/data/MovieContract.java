package app.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

import app.popularmovies.model.Video;

/**
 * Created by Madeyedexter on 23-04-2017.
 */

public class MovieContract {

    private MovieContract(){}

    public static final String AUTHORITY = "app.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+AUTHORITY);

    public static final String PATH_MOVIE = MovieEntry.TABLE_NAME;
    public static final String PATH_REVIEW = ReviewEntry.TABLE_NAME;
    public static final String PATH_VIDEO = VideoEntry.TABLE_NAME;


    public static final class MovieEntry implements BaseColumns{
        public static final String TABLE_NAME = "movie";
        public static final String COL_POSTER = "poster_path";
        public static final String COL_TITLE = "original_title";
        public static final String COL_OVERVIEW = "overview";
        public static final String COL_AVG_VOTE = "vote_average";
        public static final String COL_POPULARITY = "popularity";
        public static final String COL_REL_DATE = "release_date";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
    }

    public static final class ReviewEntry implements BaseColumns{
        public static final String TABLE_NAME = "reviews";
        public static final String COL_MOV_ID = "movie_id";
        public static final String COL_CONTENT = "content";
        public static final String COL_AUTHOR = "author";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();
    }

    public static final class VideoEntry implements BaseColumns{
        public static final String TABLE_NAME = "videos";
        public static final String COL_MOV_ID = "movie_id";
        public static final String COL_URL_KEY = "key";
        public static final String COL_SITE = "site";
        public static final String COL_NAME = "name";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEO).build();
    }

}
