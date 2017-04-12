package app.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import app.popularmovies.model.Movie;

/**
 * Created by n188851 on 12-04-2017.
 */

public class MovieAdapter extends RecyclerView.Adapter {

    private app.popularmovies.model.Movie[] movies;

    public interface ThumbClickListener{
        void onThumbClicked(Movie movie);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MovieThumbHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_thumbnail,parent));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MovieThumbHolder) holder).bindData(position);
    }

    @Override
    public int getItemCount() {
        return movies==null?0:movies.length;
    }

    public class MovieThumbHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tv_title;
        ImageView iv_thumb;
        @Override
        public void onClick(View v) {

        }

        public MovieThumbHolder(View rootView){
            super(rootView);
            tv_title = (TextView) rootView.findViewById(R.id.tv_title);
            iv_thumb = (ImageView) rootView.findViewById(R.id.iv_thumb);
        }

        public void bindData(int position){
            Movie movie = movies[position];
            tv_title.setText(movie.getOriginal_title());
            String url = Utils.TMDB_IMAGE_URL + movie.getPoster_path();
            Picasso.with(iv_thumb.getContext()).load(url).into(iv_thumb);
        }
    }
}
