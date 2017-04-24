package app.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;

import app.popularmovies.adapters.MovieAdapter;
import app.popularmovies.data.MovieContract;
import app.popularmovies.model.Movie;
import app.popularmovies.model.MovieList;
import app.popularmovies.retrofit_services.MovieAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ThumbClickListener, LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int LOADER_FAVORITE_ID = 1;
    @BindView(R.id.rv_movies)
    RecyclerView movieRecyclerView;
    MovieAdapter movieAdapter;
    @BindView(R.id.pb_loading)
    ProgressBar progressBar;
    @BindView(R.id.tv_error)
    TextView errorText;
    @BindView(R.id.tv_empty)
    TextView emptyText;


    //The spinner which shows movie sort criteria
    private Spinner spinner;

    //state variables
    private int currentMovieListPosition;
    private ArrayList<Movie> movieList;
    private Parcelable layoutManagerState;

    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //keep track of the current movie list
            currentMovieListPosition = position;
            Log.d(TAG,"Movie List is: "+ movieList);
            switch (position) {
                case 0:
                    fetchMovies(Utils.END_POINT_POPULAR);
                    Toast.makeText(MainActivity.this, R.string.retrieving_popular, Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    fetchMovies(Utils.END_POINT_TOP_RATED);
                    Toast.makeText(MainActivity.this, R.string.retrieving_top, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    getSupportLoaderManager().restartLoader(LOADER_FAVORITE_ID, null, MainActivity.this).forceLoad();
                    Toast.makeText(MainActivity.this, R.string.retrieving_favorite, Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, Utils.calculateNoOfColumns(this), LinearLayoutManager.VERTICAL, false);
        movieRecyclerView.setLayoutManager(layoutManager);

        movieAdapter = new MovieAdapter(this);

        movieRecyclerView.setAdapter(movieAdapter);
        //Device configuration changed and activity was recreated, so we need to get the previous
        //selected state of the spinner
        if (savedInstanceState != null) {
            this.currentMovieListPosition = savedInstanceState.getInt(getString(R.string.key_current_movie_list_position), 0);
            movieList = savedInstanceState.getParcelableArrayList(getString(R.string.key_movie_list));
            layoutManagerState = savedInstanceState.getParcelable(getString(R.string.key_rv_lom_state));
        }
        Log.d(TAG,"Exiting onCreate");
    }


    @Override
    protected void onStop() {
        super.onStop();
        //destroy loader if the current screen is not the favorites screen
        if (currentMovieListPosition != 2)
            getSupportLoaderManager().destroyLoader(LOADER_FAVORITE_ID);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentMovieListPosition == 2)
            getSupportLoaderManager().restartLoader(LOADER_FAVORITE_ID, null, this).forceLoad();
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        movieRecyclerView.setVisibility(View.INVISIBLE);
        errorText.setVisibility(View.INVISIBLE);
        emptyText.setVisibility(View.INVISIBLE);
    }

    private void showData() {
        progressBar.setVisibility(View.INVISIBLE);
        movieRecyclerView.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.INVISIBLE);
        emptyText.setVisibility(View.INVISIBLE);
    }

    private void showError() {
        movieRecyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        errorText.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.INVISIBLE);
    }

    private void fetchMovies(String endPoint) {
        showLoading();
        //if configuration change ocurred or app was sent to background, we will try to
        //retrieve movie data that was restored from savedInstanceState, else we will do a network request
        Log.d(TAG,"Inside fetchMovies");
        if(movieList!=null){
            showData();
            movieAdapter.setMovies(movieList);
            movieRecyclerView.getLayoutManager().onRestoreInstanceState(layoutManagerState);
        }
        else /*do a network fetch*/{
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
                    if(layoutManagerState!=null)
                        movieRecyclerView.getLayoutManager().onRestoreInstanceState(layoutManagerState);
                }
                @Override
                public void onFailure(Call<MovieList> call, Throwable t) {
                    showError();
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(getString(R.string.key_current_movie_list_position), currentMovieListPosition);
        //save current data of movie adapter
        outState.putParcelableArrayList(getString(R.string.key_movie_list),movieAdapter.getMovies());
        //save recycler view layout manager state
        outState.putParcelable(getString(R.string.key_rv_lom_state),movieRecyclerView.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.spinner_sort_order);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.movie_sort_order, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //save a reference to the spinner
        //set the restored view for the spinner
        spinner.setSelection(currentMovieListPosition);
        //attach listener, the listener will also trigger it's callback on attach
        spinner.setOnItemSelectedListener(onItemSelectedListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onThumbClicked(Movie movie) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<ArrayList<Movie>>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                showLoading();
            }

            @Override
            public ArrayList<Movie> loadInBackground() {
                Cursor cursor = getContext().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
                ArrayList<Movie> movies = new ArrayList<>();

                while (cursor.moveToNext()) {
                    Movie movie = new Movie();
                    movie.setId(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry._ID)));
                    movie.setFavorite(1);
                    movie.setAverageVote(cursor.getFloat(cursor.getColumnIndex(MovieContract.MovieEntry.COL_AVG_VOTE)));
                    movie.setOriginalTitle(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COL_TITLE)));
                    movie.setOverview(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COL_OVERVIEW)));
                    movie.setPosterPath(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COL_POSTER)));
                    movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COL_REL_DATE)));
                    movie.setPopularity(cursor.getFloat(cursor.getColumnIndex(MovieContract.MovieEntry.COL_POPULARITY)));
                    movies.add(movie);
                }
                return movies;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        if (data.size() == 0) {
            showEmpty();
        } else {
            showData();
            movieAdapter.setMovies(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
    }

    private void showEmpty() {
        movieRecyclerView.setVisibility(View.INVISIBLE);
        emptyText.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        errorText.setVisibility(View.INVISIBLE);
    }
}
