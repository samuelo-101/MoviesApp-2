package moviesapp.udacity.com.moviesapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import moviesapp.udacity.com.moviesapp.R;
import moviesapp.udacity.com.moviesapp.api.model.Video;

public class TrailersListRecyclerViewAdapter extends RecyclerView.Adapter<TrailersListRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<Video> mVideos;
    private TrailersListItemListener mTrailersListItemListener;

    public TrailersListRecyclerViewAdapter(Context context, List<Video> videos, TrailersListItemListener trailersListItemListener) {
        this.mContext = context;
        this.mVideos = videos;
        this.mTrailersListItemListener = trailersListItemListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailers_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Video video = mVideos.get(position);
        holder.mTextViewTrailerName.setText(video.getName());

        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTrailersListItemListener.onTrailerClick(video.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVideos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View parentView;
        TextView mTextViewTrailerName;

        public ViewHolder(View itemView) {
            super(itemView);
            parentView = itemView;
            mTextViewTrailerName = itemView.findViewById(R.id.textView_trailer_name);
        }
    }

    public interface TrailersListItemListener {
        void onTrailerClick(String id);
    }

    public void setVideos(List<Video> videos) {
        this.mVideos = videos;
        notifyDataSetChanged();
    }

    public void clearVideos() {
        this.mVideos.clear();
        notifyDataSetChanged();
    }
}
