package app.popularmovies.retrofit_services;

import java.util.List;

import app.popularmovies.Utils;
import app.popularmovies.model.Movie;
import app.popularmovies.model.MovieList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by n188851 on 12-04-2017.
 */

public interface MovieAPI {
    @GET("{endpoint}")
    Call<MovieList> listMovies(@Path("endpoint") String emdPoint, @Query("api_key") String key);
}
