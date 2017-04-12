package app.popularmovies.retrofit_services;

import java.util.List;

import app.popularmovies.model.Movie;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by n188851 on 12-04-2017.
 */

public interface MovieService {
    @GET("")
    Call<List<Movie>> listMovies();
}
