package app.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Madeyedexter on 23-04-2017.
 */

public class MovieDbHelper extends SQLiteOpenHelper {


    public static final int DB_VERSION=1;

    public static final String DB_NAME="movies.db";

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
    }

    public MovieDbHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE "+ MovieContract.MovieEntry.TABLE_NAME+"("+
                MovieContract.MovieEntry._ID+" TEXT PRIMARY KEY NOT NULL, "+
                MovieContract.MovieEntry.COL_POSTER+" TEXT, "+
                MovieContract.MovieEntry.COL_TITLE+" TEXT, "+
                MovieContract.MovieEntry.COL_OVERVIEW+" TEXT, "+
                MovieContract.MovieEntry.COL_AVG_VOTE+" REAL, "+
                MovieContract.MovieEntry.COL_POPULARITY+" REAL, "+
                MovieContract.MovieEntry.COL_REL_DATE+" TEXT"+
                ");";
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE "+ MovieContract.ReviewEntry.TABLE_NAME+"("+
                MovieContract.ReviewEntry._ID+" TEXT PRIMARY KEY NOT NULL, "+
                MovieContract.ReviewEntry.COL_CONTENT+" TEXT, "+
                MovieContract.ReviewEntry.COL_AUTHOR+" TEXT, "+
                MovieContract.ReviewEntry.COL_MOV_ID+" TEXT REFERENCES "+ MovieContract.MovieEntry.TABLE_NAME+"("+ MovieContract.MovieEntry._ID+") ON DELETE CASCADE"+
                ");";
        db.execSQL(SQL_CREATE_REVIEW_TABLE);
        String SQL_CREATE_VIDEO_TABLE = "CREATE TABLE "+ MovieContract.VideoEntry.TABLE_NAME+"("+
                MovieContract.VideoEntry._ID+" TEXT PRIMARY KEY NOT NULL, "+
                MovieContract.VideoEntry.COL_NAME+" TEXT, "+
                MovieContract.VideoEntry.COL_SITE+" TEXT, "+
                MovieContract.VideoEntry.COL_URL_KEY+" TEXT, "+
                MovieContract.VideoEntry.COL_MOV_ID+" TEXT REFERENCES "+ MovieContract.MovieEntry.TABLE_NAME+"("+ MovieContract.MovieEntry._ID+") ON DELETE CASCADE"+
                ");";
        db.execSQL(SQL_CREATE_VIDEO_TABLE);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ MovieContract.ReviewEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ MovieContract.VideoEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
