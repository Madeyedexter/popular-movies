package app.popularmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.popularmovies.R;
import app.popularmovies.Utils;
import app.popularmovies.holders.LoadingHolder;
import app.popularmovies.holders.TextHolder;
import app.popularmovies.model.Video;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Madeyedexter on 22-04-2017.
 */

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = VideoAdapter.class.getSimpleName();

    public interface VideoClickListener {
        void onVideoClicked(String urlKey);
    }

    public VideoClickListener getVideoClickListener() {
        return videoClickListener;
    }

    public void setVideoClickListener(VideoClickListener videoClickListener) {
        this.videoClickListener = videoClickListener;
    }

    private VideoClickListener videoClickListener;

    public ArrayList<Video> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<Video> videos) {
        this.videos = videos;
        notifyDataSetChanged();
    }

    private ArrayList<Video> videos;

    private static final int ITEM_TYPE_LOADING = 0;
    private static final int ITEM_TYPE_DATA = 1;
    private static final int ITEM_TYPE_EMPTY = 2;
    private static final int ITEM_TYPE_DONE = 3;

    public void setLoading(boolean loading) {
        this.loading = loading;
        notifyItemChanged(getItemCount() - 1);
    }

    public void setDone(boolean done) {
        this.done = done;
        notifyItemChanged(getItemCount() - 1);
    }

    private boolean loading = false;
    private boolean done = false;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_DATA:
                return new VideoAdapter.VideoHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false));
            case ITEM_TYPE_LOADING:
                return new LoadingHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading_video_review, parent, false));
            default:
                return new TextHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text_message, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM_TYPE_DATA:
                ((VideoAdapter.VideoHolder) holder).bindToView(videos.get(position));
                break;
            case ITEM_TYPE_LOADING:
                break;
            case ITEM_TYPE_EMPTY:
                ((TextHolder) holder).setMessage("No Videos for this movie yet.");
                break;
            case ITEM_TYPE_DONE:
                ((TextHolder) holder).collapse();
                break;
        }
    }

    @Override
    public int getItemCount() {
        return videos == null ? 1 : videos.size() + 1;
    }

    public void clearStatus() {
        loading = done = false;
        notifyItemChanged(getItemCount() - 1);
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

    public class VideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_video_thumb)
        ImageView videoThumb;
        @BindView(R.id.tv_video_name)
        TextView videoName;

        private View rootView;


        public VideoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.rootView = itemView;
        }

        public void bindToView(Video video) {
            Log.d(TAG, "Video is: " + video);
            Picasso.with(videoThumb.getContext()).load(Utils.VIDEO_THUMB_BASE_URL + video.getUrlKey() + "/0.jpg").placeholder(R.drawable.play_circle_placeholder).error(R.drawable.placeholder_video_load_error).into(videoThumb);
            videoName.setText(video.getName());
            rootView.setTag(video.getUrlKey());
            rootView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            videoClickListener.onVideoClicked(v.getTag().toString());
        }
    }
}