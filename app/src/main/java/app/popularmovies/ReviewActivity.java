package app.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.PersistableBundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.popularmovies.adapters.ReviewAdapter;
import app.popularmovies.adapters.VideoAdapter;
import app.popularmovies.data.MovieContract;
import app.popularmovies.model.Movie;
import app.popularmovies.model.Review;
import app.popularmovies.model.ReviewsList;
import app.popularmovies.model.VideosList;
import app.popularmovies.retrofit_services.ReviewAPI;
import app.popularmovies.retrofit_services.VideoAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReviewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Review>> {

    private static final String TAG = ReviewActivity.class.getSimpleName();

    private String movieId;
    private int favorite;

    @BindView(R.id.rv_reviews)
    RecyclerView rvReviews;
    @BindView(R.id.pb_loading)
    ProgressBar progressBar;
    @BindView(R.id.tv_error)
    TextView errorText;
    @BindView(R.id.tv_empty)
    TextView emptyText;



    private static final int REVIEWS_LOADER_ID=2;

    private ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        setTitle(getString(R.string.title_review_activity));
        ButterKnife.bind(this);


        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if(intent!=null){
            //The 0 default value means the movie is not favorite by default and a network request must be done to fetch the videos
            favorite = intent.getIntExtra(getString(R.string.key_is_favorite),0);
            movieId = intent.getStringExtra(getString(R.string.key_movie_id));
        }

        reviewAdapter=new ReviewAdapter();
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(reviewAdapter);
        //we have the required details, so fetch the videos
        if(savedInstanceState!=null){
            ArrayList<Review> reviews = savedInstanceState.getParcelableArrayList(getString(R.string.key_reviews_list));
            reviewAdapter.setReviews(reviews);
        }else
        fetchReviews();
    }

    private void fetchReviews() {
        showLoading();
        if(favorite==0){
            loadReviewsFromNetwork();
        }
        else {
            getSupportLoaderManager().restartLoader(REVIEWS_LOADER_ID,null,this).forceLoad();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(getString(R.string.key_reviews_list),reviewAdapter.getReviews());
        super.onSaveInstanceState(outState);
    }

    private void loadReviewsFromNetwork() {
        Retrofit retrofit = new Retrofit.Builder().
                baseUrl(Utils.TMDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ReviewAPI reviewAPI = retrofit.create(ReviewAPI.class);
        Call<ReviewsList> reviews = reviewAPI.listMovieReviews(movieId, Utils.TMDB_API_KEY);
        reviews.enqueue(new Callback<ReviewsList>() {
            @Override
            public void onResponse(Call<ReviewsList> call, Response<ReviewsList> response) {
                showData();
                reviewAdapter.setReviews(response.body().getReviewList());
                if(reviewAdapter.getItemCount()==0){
                    showEmpty();
                }
            }
            @Override
            public void onFailure(Call<ReviewsList> call, Throwable t) {
                showError();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
            default:return super.onOptionsItemSelected(item);
        }
    }

    //DONE: implement loader callbacks to load reviews

    @Override
    public Loader<ArrayList<Review>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<ArrayList<Review>>(this) {

            @Override
            public ArrayList<Review> loadInBackground() {
                Cursor cursor = getContext().getContentResolver().query(MovieContract.ReviewEntry.CONTENT_URI.buildUpon().appendPath(movieId).build(),null,null,null,null);
                ArrayList<Review> reviews = new ArrayList<>();

                while(cursor.moveToNext()){
                    Review review = new Review();
                    review.setId(cursor.getString(cursor.getColumnIndex(MovieContract.ReviewEntry._ID)));
                    review.setAuthor(cursor.getString(cursor.getColumnIndex(MovieContract.ReviewEntry.COL_AUTHOR)));
                    review.setContent(cursor.getString(cursor.getColumnIndex(MovieContract.ReviewEntry.COL_CONTENT)));
                    reviews.add(review);
                }
                return reviews;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Review>> loader, ArrayList<Review> data) {
        if(data.size()==0)
            showEmpty();
        else{
            showData();
            reviewAdapter.setReviews(data);
        }

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Review>> loader) {
    }

    private void showLoading(){
        progressBar.setVisibility(View.VISIBLE);
        rvReviews.setVisibility(View.INVISIBLE);
        errorText.setVisibility(View.INVISIBLE);
        emptyText.setVisibility(View.INVISIBLE);
    }

    private void showData(){
        progressBar.setVisibility(View.INVISIBLE);
        rvReviews.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.INVISIBLE);
        emptyText.setVisibility(View.INVISIBLE);
    }

    private void showError(){
        rvReviews.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        errorText.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.INVISIBLE);
    }

    private void showEmpty(){
        rvReviews.setVisibility(View.INVISIBLE);
        emptyText.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        errorText.setVisibility(View.INVISIBLE);
    }
}
