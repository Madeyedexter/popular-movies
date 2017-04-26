package app.popularmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.popularmovies.R;
import app.popularmovies.Utils;
import app.popularmovies.holders.LoadingHolder;
import app.popularmovies.holders.TextHolder;
import app.popularmovies.model.Movie;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by n188851 on 12-04-2017.
 */

public class MovieAdapter extends RecyclerView.Adapter {
    private static final String TAG = MovieAdapter.class.getSimpleName();

    private boolean loading=false;

    public void setLoading(boolean loading) {
        this.loading = loading;
        notifyItemChanged(getItemCount()-1);
    }

    public void setEnded(boolean ended) {
        this.ended = ended;
        notifyItemChanged(getItemCount()-1);
    }

    public void setError(boolean error) {
        this.error = error;
        notifyItemChanged(getItemCount()-1);
    }

    private boolean ended =false;
    private boolean error =false;


    public static final int ITEM_TYPE_MOVIE = 0;
    public static final int ITEM_TYPE_LOADING = 1;
    public static final int ITEM_TYPE_ENDED = 2;
    public static final int ITEM_TYPE_ERROR = 3;
    public static final int ITEM_TYPE_EMPTY = 4;
    public static final int ITEM_TYPE_IDLE = 5;

    public ArrayList<Movie> getMovies() {
        return movies;
    }

    private ArrayList<Movie> movies;

    public interface ThumbClickListener{
        void onThumbClicked(Movie movie);
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public ThumbClickListener clickListener;

    public MovieAdapter(ThumbClickListener clickListener){
        this.clickListener=clickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG,"Item Type is: "+viewType);
        switch(viewType){
            case ITEM_TYPE_MOVIE: //default item
                //Log.d(TAG,"Created MovieHolder");
                return new MovieThumbHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_thumbnail,parent,false));
            case ITEM_TYPE_LOADING: //Loading indicator
                Log.d(TAG,"Created LoadingHolder");
                return new LoadingHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading,parent,false));
            default: //ITEM_TYPE_ENDED|ITEM_TYPE_ERROR|ITEM_TYPE_EMPTY
                Log.d(TAG,"Created TextHolder");
                return new TextHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text_message,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case ITEM_TYPE_MOVIE: ((MovieThumbHolder)holder).bindData(position);
                break;
            case ITEM_TYPE_LOADING: break;
            //all others
            case ITEM_TYPE_EMPTY: ((TextHolder)holder).setLightEmptyMessage("No Movies to show here.");
                break;
            case ITEM_TYPE_ERROR: ((TextHolder)holder).setLightMessage("An error occurred while fetching data");
                break;
            case ITEM_TYPE_ENDED: ((TextHolder)holder).setLightMessage("End of Feed.");
                break;
            case ITEM_TYPE_IDLE: ((TextHolder)holder).tvMessage.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return movies==null?1:movies.size()+1;
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

    //resets state variables
    public void resetSpecialStates(){
        loading=ended=error=false;
        notifyItemChanged(getItemCount()-1);
    }

    @Override
    public int getItemViewType(int position) {
        //The check for last position
        if(getItemCount()-1==position && loading)
            return ITEM_TYPE_LOADING;
        if(getItemCount()-1==position && ended)
            return ITEM_TYPE_ENDED;
        if(getItemCount()-1==position && error)
            return ITEM_TYPE_ERROR;
        if(getItemCount()-1==position && getItemCount()==1)
            return ITEM_TYPE_EMPTY;
        if(getItemCount()-1==position)
            return ITEM_TYPE_IDLE;
        return ITEM_TYPE_MOVIE;
    }



    public void clear(){
        if(movies!=null)
        movies.clear();
        resetSpecialStates();
        notifyDataSetChanged();
    }



}
