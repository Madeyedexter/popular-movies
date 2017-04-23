package app.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.popularmovies.adapters.VideoAdapter;
import app.popularmovies.data.MovieContract;
import app.popularmovies.model.MovieList;
import app.popularmovies.model.Review;
import app.popularmovies.model.Video;
import app.popularmovies.model.VideosList;
import app.popularmovies.retrofit_services.MovieAPI;
import app.popularmovies.retrofit_services.VideoAPI;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
The Activity first checks if the movie is a favorite movie. If the movie is favorite it loads data from sqlite database for this movie.
If the movie is not a favorite movie, it loads data from the network. The favorite flag is passed with the intent to this activity.
 */
public class VideoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Video>>, VideoAdapter.VideoClickListener{

    private static final String TAG = VideoActivity.class.getSimpleName();

    private String movieId;
    private int favorite;

    @BindView(R.id.rv_videos)
    RecyclerView rvVideos;
    @BindView(R.id.pb_loading)
    ProgressBar progressBar;
    @BindView(R.id.tv_error)
    TextView errorText;
    @BindView(R.id.tv_empty)
    TextView emptyText;

    private static final int VIDEOS_LOADER_ID=1;

    private VideoAdapter videoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        setTitle(getString(R.string.title_videos_activity));
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

        videoAdapter=new VideoAdapter();
        videoAdapter.setVideoClickListener(this);
        rvVideos.setLayoutManager(new LinearLayoutManager(this));
        rvVideos.setAdapter(videoAdapter);
        //we have the required details, so fetch the videos
        if(savedInstanceState!=null){
            ArrayList<Video> videos = savedInstanceState.getParcelableArrayList(getString(R.string.key_videos_list));
            videoAdapter.setVideos(videos);
        }else
        fetchVideos();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(getString(R.string.key_videos_list),videoAdapter.getVideos());
        super.onSaveInstanceState(outState);
    }

    private void showLoading(){
        progressBar.setVisibility(View.VISIBLE);
        rvVideos.setVisibility(View.INVISIBLE);
        errorText.setVisibility(View.INVISIBLE);
        emptyText.setVisibility(View.INVISIBLE);
    }

    private void showData(){
        progressBar.setVisibility(View.INVISIBLE);
        rvVideos.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.INVISIBLE);
        emptyText.setVisibility(View.INVISIBLE);
    }

    private void showError(){
        rvVideos.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        errorText.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.INVISIBLE);
    }

    private void showEmpty(){
        rvVideos.setVisibility(View.INVISIBLE);
        emptyText.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        errorText.setVisibility(View.INVISIBLE);
    }

    private void fetchVideos() {
        showLoading();
        //we need a network request
        if(favorite==0){
            loadVideosFromNetwork();
        }
        else{
            //we need to run a loader to load data from ContentProvider
            getSupportLoaderManager().restartLoader(VIDEOS_LOADER_ID,null, this).forceLoad();
        }
    }

    private void loadVideosFromNetwork() {
        Retrofit retrofit = new Retrofit.Builder().
                baseUrl(Utils.TMDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        VideoAPI videoAPI = retrofit.create(VideoAPI.class);
        Call<VideosList> videos = videoAPI.listMovieVideos(movieId, Utils.TMDB_API_KEY);
        videos.enqueue(new Callback<VideosList>() {
            @Override
            public void onResponse(Call<VideosList> call, Response<VideosList> response) {
                showData();
                videoAdapter.setVideos(response.body().getVideoList());
                if(videoAdapter.getItemCount()==0)
                    showEmpty();
            }
            @Override
            public void onFailure(Call<VideosList> call, Throwable t) {
                showError();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home: NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
            case R.id.mi_video_share: shareVideoUrl();
            default:return super.onOptionsItemSelected(item);
        }
    }

    private void shareVideoUrl(){
        if(videoAdapter.getItemCount()!=0){
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, Utils.YOUTUBE_BASE_URL+videoAdapter.getVideos().get(0).getUrlKey());
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
        else
            Toast.makeText(this, R.string.message_nothing_to_share,Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_video,menu);
        return true;
    }

    @Override
    public void onVideoClicked(String urlKey) {
        //DONE: Launch youtube app with the urlKey
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.YOUTUBE_BASE_URL+urlKey)));
    }

    @Override
    public Loader<ArrayList<Video>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<ArrayList<Video>>(this) {

            @Override
            public ArrayList<Video> loadInBackground() {
                Cursor cursor = getContext().getContentResolver().query(MovieContract.VideoEntry.CONTENT_URI.buildUpon().appendPath(movieId).build(),null,null,null,null);
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
        if(data.size()==0)
            showEmpty();
        else {
            showData();
            videoAdapter.setVideos(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Video>> loader) {

    }
}
