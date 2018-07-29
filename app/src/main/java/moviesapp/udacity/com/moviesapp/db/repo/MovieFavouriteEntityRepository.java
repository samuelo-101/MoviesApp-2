package moviesapp.udacity.com.moviesapp.db.repo;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import moviesapp.udacity.com.moviesapp.api.model.Movie;
import moviesapp.udacity.com.moviesapp.db.MovieDatabase;
import moviesapp.udacity.com.moviesapp.db.dao.MovieFavouriteEntityDao;
import moviesapp.udacity.com.moviesapp.db.entity.MovieFavouriteEntity;
import moviesapp.udacity.com.moviesapp.R;

public class MovieFavouriteEntityRepository {

    private final Context mContext;
    private final MovieFavouriteEntityDao movieFavouriteEntityDao;
    private final LiveData<List<MovieFavouriteEntity>> movieFavouritesLiveData;

    private final DatabaseOperationCallback mDatabaseOperationCallback;

    public MovieFavouriteEntityRepository(Context context, DatabaseOperationCallback databaseOperationCallback) {
        this.mContext = context;
        this.movieFavouriteEntityDao = MovieDatabase.getInstance(context).movieFavouriteDao();
        this.movieFavouritesLiveData = movieFavouriteEntityDao.getAll();
        this.mDatabaseOperationCallback = databaseOperationCallback;
    }

    public LiveData<List<MovieFavouriteEntity>> getAllMovieFavouritesLiveData() {
        return this.movieFavouritesLiveData;
    }

    public void insert(final MovieFavouriteEntity movieFavouriteEntity) {
        Completable.fromAction(new Action() {
            @Override
            public void run() {
                movieFavouriteEntityDao.insert(movieFavouriteEntity);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        mDatabaseOperationCallback.onSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mDatabaseOperationCallback.onError(mContext.getResources().getString(R.string.error_db_query_insert_failed));
                    }
                });

    }

    public void deleteById(final int id) {
        Completable.fromAction(new Action() {
            @Override
            public void run() {
                movieFavouriteEntityDao.deleteById(id);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        mDatabaseOperationCallback.onSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mDatabaseOperationCallback.onError(mContext.getResources().getString(R.string.error_db_query_delete_failed));
                    }
                });
    }

    public void isExistingById(final int id) {
        Completable.fromAction(new Action() {
            @Override
            public void run() {
                boolean isExisting = movieFavouriteEntityDao.getOne(id) != null;
                mDatabaseOperationCallback.onIsExistingSuccess(isExisting);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        mDatabaseOperationCallback.onSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mDatabaseOperationCallback.onError(mContext.getResources().getString(R.string.error_db_query_is_existing_failed));
                    }
                });
    }

    public static MovieFavouriteEntity fromMovie(Movie movie) {
        MovieFavouriteEntity movieFavouriteEntity = new MovieFavouriteEntity();
        movieFavouriteEntity.setId(movie.getId());
        movieFavouriteEntity.setPosterPath(movie.getPoster_path());
        movieFavouriteEntity.setAdult(movie.isAdult());
        movieFavouriteEntity.setOverview(movie.getOverview());
        movieFavouriteEntity.setReleaseDate(movie.getRelease_date());
        movieFavouriteEntity.setOriginalTitle(movie.getOriginal_title());
        movieFavouriteEntity.setTitle(movie.getTitle());
        movieFavouriteEntity.setBackdropPath(movie.getBackdrop_path());
        movieFavouriteEntity.setVoteCount(movie.getVote_count());
        movieFavouriteEntity.setVoteAverage(movie.getVote_average());
        return movieFavouriteEntity;
    }

    public interface DatabaseOperationCallback {
        void onSuccess();
        void onIsExistingSuccess(boolean isExisting);
        void onError(String message);
    }

}
