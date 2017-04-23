package app.popularmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.popularmovies.R;
import app.popularmovies.model.Review;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Madeyedexter on 22-04-2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
        return new ReviewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ReviewHolder)holder).bindToView(reviews.get(position));
    }

    @Override
    public int getItemCount() {
        return reviews==null?0:reviews.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        super.onBindViewHolder(holder, position, payloads);
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
            tvAuthor.setText(review.getAuthor());
        }
    }
}
