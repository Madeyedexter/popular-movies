package app.popularmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.popularmovies.R;
import app.popularmovies.Utils;
import app.popularmovies.model.Movie;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by n188851 on 12-04-2017.
 */

public class MovieAdapter extends RecyclerView.Adapter {
    private static final String TAG = MovieAdapter.class.getSimpleName();

    private List<Movie> movies;

    public interface ThumbClickListener{
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

        @BindView(R.id.iv_thumb)
        ImageView iv_thumb;

        public MovieThumbHolder(View rootView){
            super(rootView);
            ButterKnife.bind(this,rootView);
            rootView.setOnClickListener(MovieThumbHolder.this);
        }

        public void bindData(int position){
            Movie movie = movies.get(position);
            String url = Utils.TMDB_IMAGE_URL + movie.getPosterPath();
            Picasso.with(iv_thumb.getContext()).load(url).placeholder(R.drawable.placeholder_poster_load).error(R.drawable.placeholder_video_load_error).into(iv_thumb);

        }

        @Override
        public void onClick(View v) {
            clickListener.onThumbClicked(movies.get(getAdapterPosition()));
        }
    }
}
