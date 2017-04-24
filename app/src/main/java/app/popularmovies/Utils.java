package app.popularmovies;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by n188851 on 12-04-2017.
 */

public class Utils {

    public static final String TMDB_BASE_URL = "http://api.themoviedb.org/3/movie/";
    public static final String TMDB_IMAGE_URL = "http://image.tmdb.org/t/p/w185/";
    public static final String TMDB_API_KEY = "32a93ee893638a800ff6648a8d1a290b";

    public static final String END_POINT_POPULAR = "popular";
    public static final String END_POINT_TOP_RATED = "top_rated";

    public static final String END_POINT_VIDEOS = "{movie_id}/videos";
    public static final String END_POINT_REVIEWS = "{movie_id}/reviews";

    public static final String VIDEO_THUMB_BASE_URL = "http://img.youtube.com/vi/";

    public static final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch?v=";


    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 180;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        return noOfColumns;
    }
}
