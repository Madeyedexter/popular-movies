package app.popularmovies.retrofit_services;

import app.popularmovies.Utils;
import app.popularmovies.model.MovieList;
import app.popularmovies.model.ReviewsList;
import app.popularmovies.model.VideosList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by n188851 on 12-04-2017.
 */

public interface VideoAPI {
    @GET(Utils.END_POINT_VIDEOS)
    Call<VideosList> listMovieVideos(@Path("movie_id") String movieId, @Query("api_key") String key);


}
