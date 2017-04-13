package app.popularmovies;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import app.popularmovies.model.Movie;

public class DetailActivity extends AppCompatActivity {

    private ImageView poster;
    private TextView title;
    private TextView rating;
    private TextView releaseDate;
    private TextView synopsis;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setTitle(getString(R.string.title_movie_details));

        //get reference to views
        poster = (ImageView)findViewById(R.id.iv_thumbnail);
        title  =(TextView) findViewById(R.id.tv_title);
        rating  =(TextView) findViewById(R.id.tv_rating);
        releaseDate  =(TextView) findViewById(R.id.tv_release_date);
        synopsis  =(TextView) findViewById(R.id.tv_synopsis);

        title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        title.setSelected(true);
        title.setSingleLine(true);


        releaseDate.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        releaseDate.setSingleLine(true);
        releaseDate.setSelected(true);



        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras!=null && extras.containsKey("movie")){
            bindMovie((Movie)extras.getParcelable("movie"));
        }
    }

    private void bindMovie(Movie movie) {
        Picasso.with(this).load(Utils.TMDB_IMAGE_URL+movie.getPosterPath()).into(poster);
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
    }
}
