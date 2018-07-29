package moviesapp.udacity.com.moviesapp.db.factory;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import moviesapp.udacity.com.moviesapp.db.repo.MovieFavouriteEntityRepository;
import moviesapp.udacity.com.moviesapp.db.viewmodel.MovieFavouriteViewModel;

public class MovieFavouriteViewModelFactory implements ViewModelProvider.Factory {

    private final Context mContext;
    private final MovieFavouriteEntityRepository.DatabaseOperationCallback mDatabaseOperationCallback;

    public MovieFavouriteViewModelFactory(Context context, MovieFavouriteEntityRepository.DatabaseOperationCallback databaseOperationCallback) {
        this.mContext = context;
        this.mDatabaseOperationCallback = databaseOperationCallback;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MovieFavouriteViewModel(this.mContext, this.mDatabaseOperationCallback);
    }
}
