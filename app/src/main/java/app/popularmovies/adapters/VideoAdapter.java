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
import app.popularmovies.model.Video;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Madeyedexter on 22-04-2017.
 */

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = VideoAdapter.class.getSimpleName();
    public interface VideoClickListener{
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VideoHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VideoHolder videoHolder = (VideoHolder) holder;
        videoHolder.bindToView(videos.get(position));
    }

    public class VideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.iv_video_thumb)
        ImageView videoThumb;
        @BindView(R.id.tv_video_name)
        TextView videoName;

        private View rootView;


        public VideoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            this.rootView=itemView;
        }

        public void bindToView(Video video){
            Log.d(TAG,"Video is: "+video);
            Picasso.with(videoThumb.getContext()).load(Utils.VIDEO_THUMB_BASE_URL+video.getUrlKey()+"/0.jpg").placeholder(R.drawable.play_circle_placeholder).error(R.drawable.placeholder_video_load_error).into(videoThumb);
            videoName.setText(video.getName());
            rootView.setTag(video.getUrlKey());
            rootView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            videoClickListener.onVideoClicked(v.getTag().toString());
        }
    }

    @Override
    public int getItemCount() {
        return videos==null?0:videos.size();
    }
}
