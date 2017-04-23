package app.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Madeyedexter on 23-04-2017.
 */

public class MovieContentProvider extends ContentProvider {

    private MovieDbHelper movieDbHelper;

    private static final String TAG = MovieContentProvider.class.getSimpleName();

    private static final int MOVIES = 100;
    private static final int MOVIES_WITH_ID = 101;

    private static final int REVIEWS = 200;
    //Not really needed
    private static final int REVIEWS_WITH_ID = 201;

    private static final int VIDEOS = 300;
    private static final int VIDEOS_WITH_ID = 301;

    private  static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //multiple movies
        uriMatcher.addURI(MovieContract.AUTHORITY,MovieContract.PATH_MOVIE,MOVIES);
        //movie with id, match a single movie
        uriMatcher.addURI(MovieContract.AUTHORITY,MovieContract.PATH_MOVIE+"/#",MOVIES_WITH_ID);
        //multiple videos
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_VIDEO, VIDEOS);
        //videos with id, matching a single movie
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_VIDEO + "/#", VIDEOS_WITH_ID);
        //multiple reviews
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_REVIEW, REVIEWS);
        //review with id, matching a single movie
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_REVIEW + "/#", REVIEWS_WITH_ID);
        return uriMatcher;
    }

    public MovieContentProvider() {
        super();
    }

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //two types of query, one returning multiple rows, and one returning row with a particular id
        SQLiteDatabase db = movieDbHelper.getReadableDatabase();
        Cursor retCursor=null;
        switch(sUriMatcher.match(uri)){
            case MOVIES: retCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case MOVIES_WITH_ID: retCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,projection,"_ID= ?",new String[]{uri.getLastPathSegment()},null,null,sortOrder);
                break;
            case REVIEWS_WITH_ID:
                retCursor = db.query(MovieContract.ReviewEntry.TABLE_NAME, projection, MovieContract.ReviewEntry.COL_MOV_ID + "=?", new String[]{uri.getLastPathSegment()}, null, null, sortOrder);
                break;
            case VIDEOS_WITH_ID: retCursor = db.query(MovieContract.VideoEntry.TABLE_NAME, projection, MovieContract.VideoEntry.COL_MOV_ID + "=?", new String[]{uri.getLastPathSegment()}, null, null, sortOrder);
            break;
            default: new UnsupportedOperationException("Unrecognized Uri: "+uri);
        }
        if(retCursor!=null)
            retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db =movieDbHelper.getWritableDatabase();
        Uri returnUri=null;
        switch(sUriMatcher.match(uri)){
            case MOVIES: long id = db.insert(MovieContract.MovieEntry.TABLE_NAME,null,values);
                if(id > 0){
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI,Long.parseLong(values.get(MovieContract.MovieEntry._ID).toString()));
                }
                else throw new SQLException("Failed to insert row for Uri: "+uri);
                break;
            default: throw new UnsupportedOperationException("Unrecognized URI: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        Uri returnUri = null;
        db.beginTransaction();
        int rowsInserted = 0;
        switch (sUriMatcher.match(uri)) {
            case VIDEOS:
                try {
                    for (ContentValues value : values) {
                        long id = db.insert(MovieContract.VideoEntry.TABLE_NAME, null, value);
                        if (id != -1) rowsInserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case REVIEWS:
                try {
                    for (ContentValues value : values) {
                        long id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, value);
                        if (id != -1) rowsInserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            default:
                return super.bulkInsert(uri, values);
        }
        if (rowsInserted > 0) getContext().getContentResolver().notifyChange(uri, null);
        return rowsInserted;
    }

    //The delete implementation for Reviews and Videos is not really needed as they have been set to cascade on delete, which will delete all child entries
    //for a movie with a particular id in the movie table
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int deleted=-1;
        switch(sUriMatcher.match(uri)){
            case MOVIES_WITH_ID: deleted = db.delete(MovieContract.MovieEntry.TABLE_NAME,"_ID=?",new String[]{uri.getLastPathSegment()});
                break;
            // delete all movies
            case MOVIES: deleted = db.delete(MovieContract.MovieEntry.TABLE_NAME,null,null);
            default: throw new UnsupportedOperationException("Unrecognized Uri: "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return deleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
