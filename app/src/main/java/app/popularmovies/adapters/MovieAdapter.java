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
        //notifyItemChanged(getItemCount()-1);
        //notifyDataSetChanged();
    }

    public void setEnded(boolean ended) {
        this.ended = ended;
        //notifyItemChanged(getItemCount()-1);
        //notifyDataSetChanged();
    }

    public void setError(boolean error) {
        this.error = error;
        //notifyItemChanged(getItemCount()-1);
        //notifyDataSetChanged();
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
        //notifyItemChanged(getItemCount()-1);
        //notifyDataSetChanged();
    }

    private boolean ended =false;
    private boolean error =false;
    private boolean empty =false;


    private static final int ITEM_TYPE_MOVIE = 0;
    private static final int ITEM_TYPE_LOADING = 1;
    private static final int ITEM_TYPE_ENDED = 2;
    private static final int ITEM_TYPE_ERROR = 3;
    private static final int ITEM_TYPE_EMPTY = 4;
    private static final int ITEM_TYPE_IDLE = 5;

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
        switch(viewType){
            case ITEM_TYPE_MOVIE: //default item
                return new MovieThumbHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_thumbnail,parent,false));
            case ITEM_TYPE_LOADING: //Loading indicator
                return new LoadingHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading,parent,false));
            default: //ITEM_TYPE_ENDED|ITEM_TYPE_ERROR|ITEM_TYPE_EMPTY
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
            case ITEM_TYPE_EMPTY: ((TextHolder)holder).setMessage("No data at the moment");
                break;
            case ITEM_TYPE_ERROR: ((TextHolder)holder).setMessage("An error occurred fetching data");
                break;
            case ITEM_TYPE_ENDED: ((TextHolder)holder).setMessage("End of Feed.");
                break;
            case ITEM_TYPE_IDLE: ((TextHolder)holder).setMessage("I am idle");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return movies==null?1:movies.size()+1;
    }

    public class LoadingHolder extends RecyclerView.ViewHolder{
        ProgressBar progressBar;

        public LoadingHolder(View rootView){
            super(rootView);
            progressBar = (ProgressBar) rootView.findViewById(R.id.pb_rv_movies);
            progressBar.setVisibility(View.VISIBLE);
        }
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
        loading=false;
        ended=false;
        error=false;
        empty=false;
    }

    @Override
    public int getItemViewType(int position) {
        Log.d(TAG, "Position is: "+position);
        Log.d(TAG, "Item Count is: "+getItemCount());
        Log.d(TAG, "loading is: "+loading);
        Log.d(TAG, "error is: "+error);
        Log.d(TAG, "ended is: "+ended);
        Log.d(TAG, "empty is: "+empty);

        //The check for last position
        /*if(movies.size()==position && loading)
            return ITEM_TYPE_LOADING;
        if(movies.size()==position && ended)
            return ITEM_TYPE_ENDED;
        if(movies.size()==position && error)
            return ITEM_TYPE_ERROR;
        if(movies.size()==position && empty)
            return ITEM_TYPE_EMPTY;*/
        if(getItemCount()-1 == position)
            return ITEM_TYPE_IDLE;
        return ITEM_TYPE_MOVIE;
    }

    public class TextHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_text_message)
        TextView tvMessage;

        public TextHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void setMessage(String message){
            tvMessage.setText(message);
        }

    }

    public void clear(){
        movies.clear();
        resetSpecialStates();
        notifyDataSetChanged();
    }



}
