package app.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
    private int currentMovieListType;
    private ArrayList<Movie> movieList;
    private Parcelable layoutManagerState;

    //spinner item selected listener
    //The spinner shows 3 items - Top Rated/Most Popular/favorite
    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //keep track of the current movie list
            currentMovieListType = position;
            Log.d(TAG, "CP is: "+recyclerViewScrollListener.getCurrentPage());
            switch (position) {
                case 0:
                    fetchMovies(Utils.END_POINT_POPULAR);
                    break;
                case 1:
                    fetchMovies(Utils.END_POINT_TOP_RATED);
                    break;
                case 2:
                    recyclerViewScrollListener.disable();
                    if(movieList!=null){
                        //showData();
                        movieAdapter.setLoading(true);
                        movieAdapter.getMovies().addAll(movieList);
                        movieRecyclerView.getLayoutManager().onRestoreInstanceState(layoutManagerState);
                        layoutManagerState=null;
                        movieList=null;
                    }
                    getSupportLoaderManager().restartLoader(LOADER_FAVORITE_ID, null, MainActivity.this).forceLoad();
                    break;
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    //recyclerView Scroll Listener, for endless scrolling
    private EndlessRecyclerViewScrollListener recyclerViewScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        final int noOfColumns = Utils.calculateNoOfColumns(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this,noOfColumns, LinearLayoutManager.VERTICAL, false);
        movieRecyclerView.setLayoutManager(layoutManager);
        movieAdapter = new MovieAdapter(this);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (movieAdapter.getItemViewType(position)){
                    case MovieAdapter.ITEM_TYPE_MOVIE: return 1;
                    default: return noOfColumns;
                }
            }
        });

        recyclerViewScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(TAG, "CP is: "+recyclerViewScrollListener.getCurrentPage());
                Log.d(TAG, "CP in CB is: "+page);
                // Load next page of movies
                loadMovieData(page, currentMovieListType);
            }
        };
        movieRecyclerView.setAdapter(movieAdapter);
        //Device configuration changed and activity was recreated, so we need to get the previous
        //selected state of the spinner
        if (savedInstanceState != null) {
            this.currentMovieListType = savedInstanceState.getInt(getString(R.string.key_current_movie_list_position), 0);
            movieList = savedInstanceState.getParcelableArrayList(getString(R.string.key_movie_list));
            layoutManagerState = savedInstanceState.getParcelable(getString(R.string.key_rv_lom_state));
            int currentPage = savedInstanceState.getInt(getString(R.string.key_current_page));
            recyclerViewScrollListener.setCurrentPage(currentPage);
        }

        movieRecyclerView.addOnScrollListener(recyclerViewScrollListener);
    }

    private void loadMovieData(int page, int currentMovieListType) {
        switch (currentMovieListType){
            case 0:
                fetchMovies(Utils.END_POINT_POPULAR, String.valueOf(page));
                break;
            case 1:
                fetchMovies(Utils.END_POINT_TOP_RATED, String.valueOf(page));
                break;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        //destroy loader if the current screen is not the favorites screen
        if (currentMovieListType != 2)
            getSupportLoaderManager().destroyLoader(LOADER_FAVORITE_ID);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentMovieListType == 2)
            getSupportLoaderManager().restartLoader(LOADER_FAVORITE_ID, null, this).forceLoad();
    }


    private void fetchMovies(String endPoint) {
        //showLoading();
        movieAdapter.clear();
        movieAdapter.setLoading(true);

        //if configuration change occurred or app was sent to background, we will try to
        //retrieve movie data that was restored from savedInstanceState, else we will do a network request
        if(movieList!=null){
            //showData();
            movieAdapter.setMovies(movieList);
            movieAdapter.setLoading(false);
            movieRecyclerView.getLayoutManager().onRestoreInstanceState(layoutManagerState);
            recyclerViewScrollListener.enable();
            layoutManagerState=null;
            movieList=null;
        }
        else /*do a network fetch*/{
            //when doing a network fetch, reset scroll listener
            recyclerViewScrollListener.resetState();
            movieRecyclerView.scrollToPosition(0);
            Retrofit retrofit = new Retrofit.Builder().
                    baseUrl(Utils.TMDB_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            MovieAPI movieAPI = retrofit.create(MovieAPI.class);
            Call<MovieList> movies = movieAPI.listMovies(endPoint, Utils.TMDB_API_KEY, String.valueOf(1));
            movies.enqueue(new Callback<MovieList>() {
                @Override
                public void onResponse(Call<MovieList> call, Response<MovieList> response) {
                    //TODO: we will only add the movies if the current movieList type is the same as the one when we
                    //made the request. It might be possible that the user navigated to a different movie type
                    //while the network request was in progress. This will cause the data to be mixed up between
                    //different movie types.
                    movieAdapter.setMovies(response.body().getMovieList());
                    movieAdapter.setLoading(false);
                    movieAdapter.notifyDataSetChanged();
                    recyclerViewScrollListener.enable();
                }
                @Override
                public void onFailure(Call<MovieList> call, Throwable t) {
                    //showError();
                    movieAdapter.setError(true);
                }
            });
        }

    }

    private void fetchMovies(String endPoint, String page) {
        //if configuration change ocurred or app was sent to background, we will try to
        //retrieve movie data that was restored from savedInstanceState, else we will do a network request
        movieRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                movieAdapter.setLoading(true);
            }
        });
        Retrofit retrofit = new Retrofit.Builder().
                    baseUrl(Utils.TMDB_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            MovieAPI movieAPI = retrofit.create(MovieAPI.class);
            Call<MovieList> movies = movieAPI.listMovies(endPoint, Utils.TMDB_API_KEY, page);
            movies.enqueue(new Callback<MovieList>() {
                @Override
                public void onResponse(Call<MovieList> call, Response<MovieList> response) {
                    int itemCount = movieAdapter.getItemCount();
                    movieAdapter.getMovies().addAll(itemCount-1,response.body().getMovieList());
                    movieAdapter.notifyItemRangeInserted(itemCount-1,response.body().getMovieList().size());
                    movieAdapter.setLoading(false);
                    movieAdapter.notifyItemChanged(movieAdapter.getItemCount()-1);

                }
                @Override
                public void onFailure(Call<MovieList> call, Throwable t) {
                    movieAdapter.setError(true);
                }
            });
        }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(getString(R.string.key_current_movie_list_position), currentMovieListType);
        //save current data of movie adapter
        outState.putParcelableArrayList(getString(R.string.key_movie_list),movieAdapter.getMovies());
        //save recycler view layout manager state
        outState.putParcelable(getString(R.string.key_rv_lom_state),movieRecyclerView.getLayoutManager().onSaveInstanceState());
        //current page
        outState.putInt(getString(R.string.key_current_page),recyclerViewScrollListener.getCurrentPage());
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
        spinner.setSelection(currentMovieListType);
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
                movieAdapter.setLoading(true);
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
        movieAdapter.clear();
        movieAdapter.setMovies(data);
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
