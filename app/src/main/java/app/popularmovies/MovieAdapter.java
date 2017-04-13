package app.popularmovies;

import android.nfc.Tag;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.popularmovies.model.Movie;

/**
 * Created by n188851 on 12-04-2017.
 */

public class MovieAdapter extends RecyclerView.Adapter {
    private static final String TAG = MovieAdapter.class.getSimpleName();

    private List<Movie> movies;

    interface ThumbClickListener{
        void onThumbClicked(Movie movie);
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public ThumbClickListener clickListener;

    public MovieAdapter(ThumbClickListener clickListener){
        this.clickListener=clickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MovieThumbHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_thumbnail,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MovieThumbHolder) holder).bindData(position);
    }

    @Override
    public int getItemCount() {
        return movies==null?0:movies.size();
    }

    public class MovieThumbHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //TextView tv_title;
        ImageView iv_thumb;

        public MovieThumbHolder(View rootView){
            super(rootView);
            //tv_title = (TextView) rootView.findViewById(R.id.tv_title);
            iv_thumb = (ImageView) rootView.findViewById(R.id.iv_thumb);
            rootView.setOnClickListener(MovieThumbHolder.this);
        }

        public void bindData(int position){
            Movie movie = movies.get(position);
            String url = Utils.TMDB_IMAGE_URL + movie.getPosterPath();
            Picasso.with(iv_thumb.getContext()).load(url).into(iv_thumb);

        }

        @Override
        public void onClick(View v) {
            clickListener.onThumbClicked(movies.get(getAdapterPosition()));
        }
    }
}
