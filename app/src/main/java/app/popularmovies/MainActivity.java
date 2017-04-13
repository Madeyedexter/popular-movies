package app.popularmovies;

import android.content.Intent;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import app.popularmovies.model.Movie;
import app.popularmovies.model.MovieList;
import app.popularmovies.retrofit_services.MovieAPI;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ThumbClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    RecyclerView movieRecyclerView;
    MovieAdapter movieAdapter;

    private ProgressBar progressBar;
    private TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);

        progressBar = (ProgressBar) findViewById(R.id.pb_loading);
        errorText = (TextView) findViewById(R.id.tv_error);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2, LinearLayoutManager.VERTICAL, false);
        movieRecyclerView.setLayoutManager(layoutManager);

        movieAdapter = new MovieAdapter(this);

        movieRecyclerView.setAdapter(movieAdapter);


    }

    private void showLoading(){
        progressBar.setVisibility(View.VISIBLE);
        movieRecyclerView.setVisibility(View.INVISIBLE);
        errorText.setVisibility(View.INVISIBLE);
    }

    private void showData(){
        progressBar.setVisibility(View.INVISIBLE);
        movieRecyclerView.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.INVISIBLE);
    }

    private void showError(){
        movieRecyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        errorText.setVisibility(View.VISIBLE);
    }

    private void fetchMovies(String endPoint){
        showLoading();

        Retrofit retrofit = new Retrofit.Builder().
                baseUrl(Utils.TMDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MovieAPI movieAPI = retrofit.create(MovieAPI.class);
        Call<MovieList> movies = movieAPI.listMovies(endPoint, Utils.TMDB_API_KEY);
        movies.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response) {
                showData();
                movieAdapter.setMovies(response.body().getMovieList());
            }

            @Override
            public void onFailure(Call<MovieList> call, Throwable t) {
                showError();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        MenuItem item = menu.findItem(R.id.spinner_sort_order);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.movie_sort_order, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        fetchMovies(Utils.END_POINT_POPULAR);
                        Toast.makeText(MainActivity.this, R.string.retrieveing_popular,Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        fetchMovies(Utils.END_POINT_TOP_RATED);
                        Toast.makeText(MainActivity.this, R.string.retrieving_top,Toast.LENGTH_SHORT).show();
                        break;
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            default:return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onThumbClicked(Movie movie) {
        Intent intent = new Intent(MainActivity.this,DetailActivity.class);
        intent.putExtra("movie",movie);
        startActivity(intent);
    }
}
