package moviesapp.udacity.com.moviesapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import moviesapp.udacity.com.moviesapp.MovieDetailsActivity;
import moviesapp.udacity.com.moviesapp.R;
import moviesapp.udacity.com.moviesapp.api.model.Movie;

public class MoviesGridRecyclerViewAdapter extends RecyclerView.Adapter<MoviesGridRecyclerViewAdapter.ViewHolder> {

    private final Context mContext;
    private List<Movie> mMovies;
    private final String imageBaseUrl;
    private final String defaultImageSize;

    public MoviesGridRecyclerViewAdapter(Context context, List<Movie> movies) {
        this.mContext = context;
        this.mMovies = movies;
        this.imageBaseUrl = context.getString(R.string.api_movie_image_base_uri);
        this.defaultImageSize = context.getString(R.string.api_default_image_size);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movies_grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Movie movie = this.mMovies.get(position);

        Picasso.with(mContext)
                .load(new StringBuilder().append(this.imageBaseUrl).append(this.defaultImageSize).append(movie.getPoster_path()).toString())
                .error(R.drawable.ic_broken_image_grey)
                .placeholder(R.drawable.ic_image_grey)
                .into(holder.mImageViewMovieImage);

        holder.mImageViewMovieImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MovieDetailsActivity.class);
                Bundle extras = new Bundle();
                extras.putParcelable(MovieDetailsActivity.ARG_MOVIE_PARCEL, movie);
                intent.putExtras(extras);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.mMovies.size();
    }

    public void setMovies(List<Movie> movies) {
        this.mMovies = movies;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imageView_movie_image)
        ImageView mImageViewMovieImage;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
