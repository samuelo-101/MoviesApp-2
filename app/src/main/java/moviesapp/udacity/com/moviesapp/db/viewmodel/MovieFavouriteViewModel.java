package moviesapp.udacity.com.moviesapp.db.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import java.util.List;

import moviesapp.udacity.com.moviesapp.db.entity.MovieFavouriteEntity;
import moviesapp.udacity.com.moviesapp.db.repo.MovieFavouriteEntityRepository;

public class MovieFavouriteViewModel extends ViewModel {

    private final LiveData<List<MovieFavouriteEntity>> movieFavourites;

    public MovieFavouriteViewModel(Context context, MovieFavouriteEntityRepository.DatabaseOperationCallback mDatabaseOperationCallback) {
        MovieFavouriteEntityRepository movieFavouriteEntityRepository = new MovieFavouriteEntityRepository(context, mDatabaseOperationCallback);
        this.movieFavourites = movieFavouriteEntityRepository.getAllMovieFavouritesLiveData();
    }

    public LiveData<List<MovieFavouriteEntity>> getMovieFavourites() {
        return movieFavourites;
    }


}
