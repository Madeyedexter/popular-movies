package app.popularmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.popularmovies.R;
import app.popularmovies.holders.LoadingHolder;
import app.popularmovies.holders.TextHolder;
import app.popularmovies.model.Review;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Madeyedexter on 22-04-2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_TYPE_LOADING = 0;
    private static final int ITEM_TYPE_DATA = 1;
    private static final int ITEM_TYPE_EMPTY = 2;
    private static final int ITEM_TYPE_DONE = 3;

    public void setLoading(boolean loading) {
        this.loading = loading;
        notifyItemChanged(getItemCount()-1);
    }

    public void setDone(boolean done) {
        this.done = done;
        notifyItemChanged(getItemCount()-1);
    }

    private boolean loading = false;
    private boolean done = false;

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    private ArrayList<Review> reviews;

    private static final String TAG = ReviewAdapter.class.getSimpleName();

    public ReviewAdapter() {
        super();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case ITEM_TYPE_DATA: return new ReviewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review,parent,false));
            case ITEM_TYPE_LOADING: return new LoadingHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading_video_review,parent,false));
            default:return new TextHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text_message,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case ITEM_TYPE_DATA: ((ReviewHolder) holder).bindToView(reviews.get(position));
                break;
            case ITEM_TYPE_LOADING:
                break;
            case ITEM_TYPE_EMPTY: ((TextHolder) holder).setMessage("No Reviews for this movie yet.");
                break;
            case ITEM_TYPE_DONE: ((TextHolder) holder).collapse();
                break;
        }
    }

    @Override
    public int getItemCount() {
        return reviews==null?1:reviews.size()+1;
    }

    public void clearStatus() {
        loading=done=false;
        notifyItemChanged(getItemCount()-1);
    }

    public class ReviewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.tv_author)
        TextView tvAuthor;

        private View rootView;

        public ReviewHolder(View itemView) {
            super(itemView);
            this.rootView=itemView;
            ButterKnife.bind(this,itemView);
        }

        public void bindToView(Review review){
            tvContent.setText(review.getContent());
            tvAuthor.setText(String.format(tvAuthor.getContext().getString(R.string.review_author_name),review.getAuthor()));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position==getItemCount()-1 && loading)
            return ITEM_TYPE_LOADING;
        if(position==getItemCount()-1 && getItemCount()==1)
            return ITEM_TYPE_EMPTY;
        if(position == getItemCount()-1)
            return ITEM_TYPE_DONE;
        return ITEM_TYPE_DATA;
    }
}
