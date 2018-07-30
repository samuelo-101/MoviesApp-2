package moviesapp.udacity.com.moviesapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import moviesapp.udacity.com.moviesapp.R;
import moviesapp.udacity.com.moviesapp.api.model.Review;

public class ReviewsListRecyclerViewAdapter extends RecyclerView.Adapter<ReviewsListRecyclerViewAdapter.ViewHolder> {

    private List<Review> mReviews;

    public ReviewsListRecyclerViewAdapter(List<Review> reviews) {
        this.mReviews = reviews;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_review_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Review review = mReviews.get(position);
        holder.textViewAuthor.setText(review.getAuthor());
        holder.textViewReview.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return this.mReviews.size();
    }

    public void setReviews(List<Review> reviews) {
        this.mReviews = reviews;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewAuthor;
        TextView textViewReview;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewAuthor = itemView.findViewById(R.id.textView_author);
            textViewReview = itemView.findViewById(R.id.textView_review);
        }
    }
}
