package app.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import app.popularmovies.adapters.ReviewAdapter;
import app.popularmovies.adapters.VideoAdapter;
import app.popularmovies.data.MovieContract;
import app.popularmovies.model.Movie;
import app.popularmovies.model.Review;
import app.popularmovies.model.ReviewsList;
import app.popularmovies.model.Video;
import app.popularmovies.model.VideosList;
import app.popularmovies.retrofit_services.ReviewAPI;
import app.popularmovies.retrofit_services.VideoAPI;
import app.popularmovies.tasks.SetAsFavoriteIntentService;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener, VideoAdapter.VideoClickListener, SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String TAG = DetailActivity.class.getSimpleName();


    @BindView(R.id.iv_thumbnail)
    ImageView poster;
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.tv_rating)
    TextView rating;
    @BindView(R.id.tv_release_date)
    TextView releaseDate;
    @BindView(R.id.tv_synopsis)
    TextView synopsis;
    @BindView(R.id.tv_videos)
    TextView tvVideos;
    @BindView(R.id.tv_reviews)
    TextView tvReviews;
    @BindView(R.id.tv_favorite)
    TextView tvFavorite;
    @BindView(R.id.rv_videos)
    RecyclerView rvVideos;
    @BindView(R.id.rv_reviews)
    RecyclerView rvReviews;



    private Movie movie;

    //Loader ids
    private static final int VIDEO_LOADER_ID = 1;
    private static final int REVIEW_LOADER_ID = 2;

    //Video loader callbacks
    private LoaderManager.LoaderCallbacks<ArrayList<Video>> videoLoaderCallbacks=new LoaderManager.LoaderCallbacks<ArrayList<Video>>() {
        @Override
        public Loader<ArrayList<Video>> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<ArrayList<Video>>(DetailActivity.this) {

                @Override
                public ArrayList<Video> loadInBackground() {
                    Cursor cursor = getContext().getContentResolver().query(MovieContract.VideoEntry.CONTENT_URI.buildUpon().appendPath(movie.getId()).build(),null,null,null,null);
                    ArrayList<Video> videos = new ArrayList<>();

                    while(cursor!=null && cursor.moveToNext()){
                        Video video = new Video();
                        video.setId(cursor.getString(cursor.getColumnIndex(MovieContract.VideoEntry._ID)));
                        video.setName(cursor.getString(cursor.getColumnIndex(MovieContract.VideoEntry.COL_NAME)));
                        video.setSite(cursor.getString(cursor.getColumnIndex(MovieContract.VideoEntry.COL_SITE)));
                        video.setUrlKey(cursor.getString(cursor.getColumnIndex(MovieContract.VideoEntry.COL_URL_KEY)));
                        videos.add(video);
                    }
                    return videos;
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Video>> loader, ArrayList<Video> data) {
            if(data.size()==0){}
            else {
                ((VideoAdapter)rvVideos.getAdapter()).setVideos(data);
            }
        }
        @Override
        public void onLoaderReset(Loader<ArrayList<Video>> loader) {

        }
    };
    //Review Loader callbacks
    private LoaderManager.LoaderCallbacks<ArrayList<Review>> reviewLoaderCallbacks = new LoaderManager.LoaderCallbacks<ArrayList<Review>>() {
        @Override
        public Loader<ArrayList<Review>> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<ArrayList<Review>>(DetailActivity.this) {

                @Override
                public ArrayList<Review> loadInBackground() {
                    Cursor cursor = getContext().getContentResolver().query(MovieContract.ReviewEntry.CONTENT_URI.buildUpon().appendPath(movie.getId()).build(),null,null,null,null);
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
            if(data.size()==0){}
            else{
                ((ReviewAdapter)rvReviews.getAdapter()).setReviews(data);
            }
        }
        @Override
        public void onLoaderReset(Loader<ArrayList<Review>> loader) {
        }
    };



    @Override
    public void onSaveInstanceState(Bundle outState) {
        //put videos and reviews
        outState.putParcelableArrayList(getString(R.string.key_videos_list),((VideoAdapter)rvVideos.getAdapter()).getVideos());
        outState.putParcelableArrayList(getString(R.string.key_reviews_list),((ReviewAdapter)rvReviews.getAdapter()).getReviews());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setTitle(getString(R.string.title_movie_details));
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        //get reference to views
        ButterKnife.bind(this);
        //click listener
        tvFavorite.setOnClickListener(this);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }



        title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        title.setSelected(true);
        title.setSingleLine(true);


        releaseDate.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        releaseDate.setSingleLine(true);
        releaseDate.setSelected(true);



        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras!=null && extras.containsKey(getString(R.string.key_movie))){
            Movie movie = extras.getParcelable(getString(R.string.key_movie));
            //save a reference to the movie object
            this.movie=movie;
        }
        //determine if movie was marked as favorite by querying shared preferences
        setMovieFavoriteStatus();
        //update the ui with data from this movie
        bindMovie(movie);
        setupVideos(savedInstanceState);
        setupReviews(savedInstanceState);

    }

    private void setupReviews(Bundle savedInstanceState) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        rvReviews.setLayoutManager(linearLayoutManager);
        ReviewAdapter reviewAdapter = new ReviewAdapter();
        rvReviews.setAdapter(reviewAdapter);
        if(savedInstanceState!= null){
            ArrayList<Review> reviews = savedInstanceState.getParcelableArrayList(getString(R.string.key_reviews_list));
            reviewAdapter.setReviews(reviews);
        }
        //if movie is a favorite movie, fetch its videos from ContentProvider, using a loader
        else if(movie.getFavorite()!= 0){
            getSupportLoaderManager().restartLoader(REVIEW_LOADER_ID,null,reviewLoaderCallbacks).forceLoad();
        }
        else
            loadReviewsFromNetwork();

    }

    private void setupVideos(Bundle savedInstanceState) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        rvVideos.setLayoutManager(linearLayoutManager);
        VideoAdapter videoAdapter = new VideoAdapter();
        rvVideos.setAdapter(videoAdapter);
        videoAdapter.setVideoClickListener(this);
        if(savedInstanceState!= null){
            ArrayList<Video> videos = savedInstanceState.getParcelableArrayList(getString(R.string.key_videos_list));
            videoAdapter.setVideos(videos);
        }
        //if movie is a favorite movie, fetch its videos from ContentProvider, using a loader
        else if(movie.getFavorite()!= 0){
            getSupportLoaderManager().restartLoader(VIDEO_LOADER_ID,null,videoLoaderCallbacks).forceLoad();
        }
        else //do a network load
            loadVideosFromNetwork();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //destroy all loaders
        getSupportLoaderManager().destroyLoader(VIDEO_LOADER_ID);
        getSupportLoaderManager().destroyLoader(REVIEW_LOADER_ID);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tvReviews.setOnClickListener(null);
        tvVideos.setOnClickListener(null);
        tvFavorite.setOnClickListener(null);
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    private void bindMovie(Movie movie) {
        Picasso.with(this).load(Utils.TMDB_IMAGE_URL+movie.getPosterPath()).placeholder(R.drawable.placeholder_poster_load).error(R.drawable.placeholder_video_load_error).into(poster);
        title.setText(movie.getOriginalTitle());
        rating.setText(String.valueOf(movie.getAverageVote()));

        SimpleDateFormat inputFormat = new SimpleDateFormat(getString(R.string.date_format_tmdb));
        SimpleDateFormat outputFormat = new SimpleDateFormat(getString(R.string.date_format_view));
        Date date=null;
        try {
             date = inputFormat.parse(movie.getReleaseDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        releaseDate.setText(String.format(getString(R.string.release_date),outputFormat.format(date)));
        synopsis.setText(movie.getOverview());
        toggleFavoriteTextView();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.tv_favorite: toggleFavorite();
                //Remove click listener to prevent user from tapping multiple times
                tvFavorite.setOnClickListener(null);
                break;
            default: break;
        }
    }

    /**
     * This method starts a service which will toggle a movie as favorite/not favorite based on the flag "is_favorite"
     * The Service Updates the database using calls to content provider, and also fetches additional items(Reviews/Videos) using network request
     * The service finally updates the SharedPreferences, which will trigger the preferences change listener callback,
     * where we can update the UI(Updating the favorite text view)
     * */
    private void toggleFavorite() {
        Intent intent = new Intent(this, SetAsFavoriteIntentService.class);
        intent.putExtra(getString(R.string.key_movie),movie);
        intent.putExtra(getString(R.string.key_is_favorite),movie.getFavorite());
        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_video,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
            case R.id.mi_video_share: shareVideoUrl();
                return true;
            default:return super.onOptionsItemSelected(item);
        }
    }

    private void shareVideoUrl(){
        if(rvVideos.getAdapter().getItemCount()!=0){
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, Utils.YOUTUBE_BASE_URL+((VideoAdapter)rvVideos.getAdapter()).getVideos().get(0).getUrlKey());
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
        else
            Toast.makeText(this, R.string.message_nothing_to_share,Toast.LENGTH_SHORT).show();

    }
    private void setMovieFavoriteStatus(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        movie.setFavorite(sharedPreferences.getBoolean(movie.getId(),false)?1:0);
    }


    private void toggleFavoriteTextView(){
        if(movie.getFavorite()==0){
            tvFavorite.setText(R.string.set_as_favorite);
            tvFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_grey_24dp,0,0,0);
        }else{
            tvFavorite.setText(R.string.favorite_movie);
            tvFavorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_pink_24dp,0,0,0);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(movie.getId())){
            //This movie is marked/unmarked as favorite
            boolean isFavorite=sharedPreferences.getBoolean(key,false);
            movie.setFavorite(isFavorite?1:0);
            toggleFavoriteTextView();
            //re attach click listener
            tvFavorite.setOnClickListener(this);
        }
    }



    private void loadVideosFromNetwork() {
        Retrofit retrofit = new Retrofit.Builder().
                baseUrl(Utils.TMDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        VideoAPI videoAPI = retrofit.create(VideoAPI.class);
        Call<VideosList> videos = videoAPI.listMovieVideos(movie.getId(), Utils.TMDB_API_KEY);
        videos.enqueue(new Callback<VideosList>() {
            @Override
            public void onResponse(Call<VideosList> call, Response<VideosList> response) {
                ArrayList<Video> videoList = response.body().getVideoList();
                ((VideoAdapter)rvVideos.getAdapter()).setVideos(videoList);
                if(videoList.size()==0){}
            }
            @Override
            public void onFailure(Call<VideosList> call, Throwable t) {

            }
        });
    }

    private void loadReviewsFromNetwork() {
        Retrofit retrofit = new Retrofit.Builder().
                baseUrl(Utils.TMDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ReviewAPI reviewAPI = retrofit.create(ReviewAPI.class);
        Call<ReviewsList> reviews = reviewAPI.listMovieReviews(movie.getId(), Utils.TMDB_API_KEY);
        reviews.enqueue(new Callback<ReviewsList>() {
            @Override
            public void onResponse(Call<ReviewsList> call, Response<ReviewsList> response) {
                ReviewAdapter reviewAdapter = (ReviewAdapter) rvReviews.getAdapter();
                reviewAdapter.setReviews(response.body().getReviewList());
                if(reviewAdapter.getItemCount()==0){
                }
            }
            @Override
            public void onFailure(Call<ReviewsList> call, Throwable t) {

            }
        });

    }

    @Override
    public void onVideoClicked(String urlKey) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.YOUTUBE_BASE_URL+urlKey)));
    }
}
