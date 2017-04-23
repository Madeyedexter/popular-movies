package app.popularmovies.tasks;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import app.popularmovies.R;
import app.popularmovies.Utils;
import app.popularmovies.data.MovieContract;
import app.popularmovies.model.Movie;
import app.popularmovies.model.Review;
import app.popularmovies.model.ReviewsList;
import app.popularmovies.model.Video;
import app.popularmovies.model.VideosList;
import app.popularmovies.retrofit_services.ReviewAPI;
import app.popularmovies.retrofit_services.VideoAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Madeyedexter on 23-04-2017.
 */

public class SetAsFavoriteIntentService extends IntentService {

    private static final String TAG = SetAsFavoriteIntentService.class.getSimpleName();

    private int tasksToComplete = 3;

    private boolean someTaskFailed = false;

    private Toast toast;

    public SetAsFavoriteIntentService(){
        super(SetAsFavoriteIntentService.class.getSimpleName());
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Movie movie = intent.getParcelableExtra(getString(R.string.key_movie));
        int isFavorite= intent.getIntExtra(getString(R.string.key_is_favorite),0);
        //We will persist this movie and its related videos and reviews in the sqlite DB via content provider
        if(isFavorite == 0){
            showToast(getString(R.string.message_adding_to_favorite));
            persistMovie(movie);
            //retrofit tasks
            fetchReviews(movie.getId());
            fetchVideos(movie.getId());
        if(someTaskFailed){
            showToast(String.format(getString(R.string.message_error_favoriting),movie.getOriginalTitle()));
            //remove from favorite, append the movie id in the uri
            getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(movie.getId()).build(),null,null);
        }else{
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(movie.getId(),true);
            editor.commit();
            showToast(String.format(getString(R.string.message_added_favorite),movie.getOriginalTitle()));
        }
        }
        else{
            //remove from favorite, append the movie id in the uri
            showToast(getString(R.string.message_removing_favorite));
            getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(movie.getId()).build(),null,null);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(movie.getId(),false);
            editor.commit();
            showToast(getString(R.string.message_removed_favorite));
        }
    }

    private void persistMovie(Movie movie) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry._ID,movie.getId());
        contentValues.put(MovieContract.MovieEntry.COL_AVG_VOTE,movie.getAverageVote());
        contentValues.put(MovieContract.MovieEntry.COL_OVERVIEW,movie.getOverview());
        contentValues.put(MovieContract.MovieEntry.COL_POPULARITY,movie.getPopularity());
        contentValues.put(MovieContract.MovieEntry.COL_POSTER,movie.getPosterPath());
        contentValues.put(MovieContract.MovieEntry.COL_REL_DATE,movie.getReleaseDate());
        contentValues.put(MovieContract.MovieEntry.COL_TITLE,movie.getOriginalTitle());
        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,contentValues);
    }

    private void persistReviews(ArrayList<Review> reviews, String movieId){
        if(reviews.size()>0){
            ContentValues[] contentValues= new ContentValues[reviews.size()];
            for(int i=0;i<reviews.size();i++){
                contentValues[i] = new ContentValues();
                contentValues[i].put(MovieContract.ReviewEntry._ID,reviews.get(i).getId());
                contentValues[i].put(MovieContract.ReviewEntry.COL_AUTHOR,reviews.get(i).getAuthor());
                contentValues[i].put(MovieContract.ReviewEntry.COL_CONTENT,reviews.get(i).getContent());
                contentValues[i].put(MovieContract.ReviewEntry.COL_MOV_ID,movieId);
            }
            int rows = getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI,contentValues);
            if(rows <= 0) someTaskFailed = true;
        }
    }

    private void persistVideos(ArrayList<Video> videos, String movieId) {
        if(videos.size()>0){
            ContentValues[] contentValues= new ContentValues[videos.size()];
            for(int i=0;i<videos.size();i++){
                contentValues[i] = new ContentValues();
                contentValues[i].put(MovieContract.VideoEntry._ID,videos.get(i).getId());
                contentValues[i].put(MovieContract.VideoEntry.COL_URL_KEY,videos.get(i).getUrlKey());
                contentValues[i].put(MovieContract.VideoEntry.COL_NAME,videos.get(i).getName());
                contentValues[i].put(MovieContract.VideoEntry.COL_SITE,videos.get(i).getSite());
                contentValues[i].put(MovieContract.VideoEntry.COL_MOV_ID,movieId);
            }
            int rows = getContentResolver().bulkInsert(MovieContract.VideoEntry.CONTENT_URI,contentValues);
            if(rows <= 0) someTaskFailed = true;
        }
    }

    private void fetchReviews(final String movieId){
        Retrofit retrofit = new Retrofit.Builder().
                baseUrl(Utils.TMDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ReviewAPI reviewAPI = retrofit.create(ReviewAPI.class);
        Call<ReviewsList> reviews = reviewAPI.listMovieReviews(movieId, Utils.TMDB_API_KEY);
        try {
            Response<ReviewsList> response = reviews.execute();
            persistReviews(response.body().getReviewList(), movieId);
        } catch (IOException e) {
            e.printStackTrace();
            //for rollback operation
            someTaskFailed=true;
            return;
        }
    }

    private void fetchVideos(final String movieId){
        Retrofit retrofit = new Retrofit.Builder().
                baseUrl(Utils.TMDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        VideoAPI videoAPI = retrofit.create(VideoAPI.class);
        Call<VideosList> videos = videoAPI.listMovieVideos(movieId, Utils.TMDB_API_KEY);
        try {
            Response<VideosList> response = videos.execute();
            persistVideos(response.body().getVideoList(), movieId);
        } catch (IOException e) {
            e.printStackTrace();
            //for rollback operation
            someTaskFailed=true;
            return;
        }
    }

    private void showToast(final String message){
        if(toast!=null)
        toast.cancel();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                toast = Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT);
                toast.show();}
        });
    }


}
