package app.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import java.util.Date;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import app.popularmovies.model.Movie;
import app.popularmovies.tasks.SetAsFavoriteIntentService;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener{

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

    private Movie movie;

    //This flag is used to check if the movie is being cached to the SQLite DB. If it is non-zero
    //we will not process



    @Override
    public void onSaveInstanceState(Bundle outState) {
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
        //click listeners
        tvVideos.setOnClickListener(this);
        tvReviews.setOnClickListener(this);
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
            case R.id.tv_videos: launchVideoActivity();
                break;
            case R.id.tv_reviews: launchReviewActivity();
                break;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
            default:return super.onOptionsItemSelected(item);
        }
    }

    private void launchReviewActivity() {
        Intent intent = new Intent(this, ReviewActivity.class);
        intent.putExtra(getString(R.string.key_movie_id),movie.getId());
        intent.putExtra(getString(R.string.key_is_favorite),movie.getFavorite());
        startActivity(intent);
    }

    private void launchVideoActivity() {
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra(getString(R.string.key_movie_id),movie.getId());
        intent.putExtra(getString(R.string.key_is_favorite),movie.getFavorite());
        startActivity(intent);
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
}
