package moviesapp.udacity.com.moviesapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import moviesapp.udacity.com.moviesapp.adapter.MoviesGridRecyclerViewAdapter;
import moviesapp.udacity.com.moviesapp.api.model.Movie;
import moviesapp.udacity.com.moviesapp.api.model.response.ErrorResponse;
import moviesapp.udacity.com.moviesapp.api.model.response.FetchMoviesResponse;
import moviesapp.udacity.com.moviesapp.api.service.MoviesApiServiceHelper;
import moviesapp.udacity.com.moviesapp.db.entity.MovieFavouriteEntity;
import moviesapp.udacity.com.moviesapp.db.factory.MovieFavouriteViewModelFactory;
import moviesapp.udacity.com.moviesapp.db.repo.MovieFavouriteEntityRepository;
import moviesapp.udacity.com.moviesapp.db.viewmodel.MovieFavouriteViewModel;
import moviesapp.udacity.com.moviesapp.util.ApiUtil;
import moviesapp.udacity.com.moviesapp.util.DialogUtil;
import moviesapp.udacity.com.moviesapp.util.SharedPrefsUtil;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MovieFavouriteEntityRepository.DatabaseOperationCallback, MoviesGridRecyclerViewAdapter.MovieGridItemListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.recyclerView_movies)
    RecyclerView mRecyclerViewMovies;

    private AlertDialog alertDialogSortOrder;

    private MoviesGridRecyclerViewAdapter adapter;

    private final CompositeDisposable disposable = new CompositeDisposable();

    private MovieFavouriteViewModelFactory movieFavouriteViewModelFactory;
    private MovieFavouriteViewModel movieFavouriteViewModel;
    private Observer<List<MovieFavouriteEntity>> movieFavouritesObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        mRecyclerViewMovies.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        adapter = new MoviesGridRecyclerViewAdapter(getApplicationContext(), new ArrayList<Movie>(), this);
        mRecyclerViewMovies.setAdapter(adapter);

        movieFavouriteViewModelFactory = new MovieFavouriteViewModelFactory(getApplicationContext(), this);
        movieFavouriteViewModel = ViewModelProviders.of(this, movieFavouriteViewModelFactory).get(MovieFavouriteViewModel.class);

        setupUIBasedOnOrderPreference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_order) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.sort_by);
            alertDialogSortOrder = builder.create();

            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.movie_sort_order_options, null);
            RadioButton radioButtonSortByPopular = dialogView.findViewById(R.id.radio_sort_by_popular);
            RadioButton radioButtonSortByTopRated = dialogView.findViewById(R.id.radio_sort_by_top_rated);
            RadioButton radioButtonSortByFavourites = dialogView.findViewById(R.id.radio_sort_by_favourite);

            String userSortOrderPreference = SharedPrefsUtil.getString(getApplicationContext(), getString(R.string.shared_prefs_sort_order_key));
            radioButtonSortByPopular.setChecked(userSortOrderPreference.equals(getString(R.string.sort_popular_option)));
            radioButtonSortByTopRated.setChecked(userSortOrderPreference.equals(getString(R.string.sort_top_rated_option)));
            radioButtonSortByFavourites.setChecked(userSortOrderPreference.equals(getString(R.string.sort_favourites_option)));

            alertDialogSortOrder.setView(dialogView);
            alertDialogSortOrder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        if(movieFavouriteViewModel.getMovieFavourites().hasObservers()) {
            movieFavouriteViewModel.getMovieFavourites().removeObserver(movieFavouritesObserver);
        }
    }

    public void onMovieSortOrderChanged(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        showLoadingIndicator(true);

        switch (view.getId()) {
            case R.id.radio_sort_by_top_rated:
                if (checked)
                    disposable.add(
                            getFetchTopRatedMoviesObservable()
                    );
                SharedPrefsUtil.putString(getApplicationContext(), getString(R.string.shared_prefs_sort_order_key), getString(R.string.sort_top_rated_option));
                break;
            case R.id.radio_sort_by_favourite:
                SharedPrefsUtil.putString(getApplicationContext(), getString(R.string.shared_prefs_sort_order_key), getString(R.string.sort_favourites_option));
                setupUserFavouriteMovies();
                showLoadingIndicator(false);
                break;
            case R.id.radio_sort_by_popular:
            default:
                disposable.add(
                        getFetchPopularMoviesObservable()
                );
                SharedPrefsUtil.putString(getApplicationContext(), getString(R.string.shared_prefs_sort_order_key), getString(R.string.sort_popular_option));
                break;
        }

        alertDialogSortOrder.dismiss();
    }

    private void setupUserFavouriteMovies() {
        Snackbar.make(mRecyclerViewMovies, getString(R.string.displaying_by_favourites), Snackbar.LENGTH_LONG).show();
        movieFavouritesObserver = new Observer<List<MovieFavouriteEntity>>() {
            @Override
            public void onChanged(@Nullable List<MovieFavouriteEntity> movieFavouriteEntities) {
                String userSortOrderPreference = SharedPrefsUtil.getString(getApplicationContext(), getString(R.string.shared_prefs_sort_order_key));
                String sortOrderFavourites = getString(R.string.sort_favourites_option);
                if (userSortOrderPreference.equals(sortOrderFavourites)) {
                    adapter.setMoviesFromFavouritesEntity(movieFavouriteEntities);
                }
            }
        };
        movieFavouriteViewModel.getMovieFavourites().observe(this, movieFavouritesObserver);
    }

    private void setupUIBasedOnOrderPreference() {
        showLoadingIndicator(true);

        boolean hasActiveObservers = movieFavouriteViewModel.getMovieFavourites().hasActiveObservers();
        if(hasActiveObservers) {
            movieFavouriteViewModel.getMovieFavourites().removeObserver(movieFavouritesObserver);
        }

        String userSortOrderPreference = SharedPrefsUtil.getString(getApplicationContext(), getString(R.string.shared_prefs_sort_order_key));
        if (userSortOrderPreference.equals(getString(R.string.sort_popular_option))) {
            disposable.add(
                    getFetchPopularMoviesObservable()
            );
        } else if (userSortOrderPreference.equals(getString(R.string.sort_top_rated_option))) {
            disposable.add(
                    getFetchTopRatedMoviesObservable()
            );
        } else {
            setupUserFavouriteMovies();
            showLoadingIndicator(false);
        }
    }

    private void showLoadingIndicator(boolean showLoading) {
        mProgressBar.setVisibility(showLoading ? View.VISIBLE : View.GONE);
        mRecyclerViewMovies.setVisibility(showLoading ? View.GONE : View.VISIBLE);
    }

    @NonNull
    private DisposableSingleObserver<Response<FetchMoviesResponse>> getFetchPopularMoviesObservable() {
        return MoviesApiServiceHelper.getInstance(getApplicationContext())
                .fetchPopularMovies(BuildConfig.MOVIES_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Response<FetchMoviesResponse>>() {
                    @Override
                    public void onSuccess(Response<FetchMoviesResponse> response) {
                        showLoadingIndicator(false);
                        handleFetchMoviesResponse(response);
                        Snackbar.make(mRecyclerViewMovies, getString(R.string.diplaying_by_popularity), Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        showLoadingIndicator(false);
                        if (e instanceof ConnectException || e instanceof UnknownHostException) {
                            DialogUtil.showAlertDialogMessage(MainActivity.this, getString(R.string.api_connection_error_title), getString(R.string.api_connection_error_message));
                        } else {
                            DialogUtil.showGenericErrorMessage(MainActivity.this);
                        }
                    }
                });
    }

    @NonNull
    private DisposableSingleObserver<Response<FetchMoviesResponse>> getFetchTopRatedMoviesObservable() {
        return MoviesApiServiceHelper.getInstance(getApplicationContext())
                .fetchTopRatedMovies(BuildConfig.MOVIES_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Response<FetchMoviesResponse>>() {
                    @Override
                    public void onSuccess(Response<FetchMoviesResponse> response) {
                        showLoadingIndicator(false);
                        handleFetchMoviesResponse(response);
                        Snackbar.make(mRecyclerViewMovies, getString(R.string.diplaying_by_top_rated), Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        showLoadingIndicator(false);
                        if (e instanceof ConnectException || e instanceof UnknownHostException) {
                            DialogUtil.showConnectionFailedErrorMessage(MainActivity.this);
                        } else {
                            DialogUtil.showGenericErrorMessage(MainActivity.this);
                        }

                    }
                });
    }

    private void handleFetchMoviesResponse(Response<FetchMoviesResponse> response) {
        int responseCode = response.code();
        switch (responseCode) {
            case 200:
                FetchMoviesResponse fetchMoviesResponse = response.body();
                if (fetchMoviesResponse != null) {
                    adapter.setMovies(fetchMoviesResponse.getResults());
                }
                break;
            case 401:
                DialogUtil.showUnauthorizedErrorMessage(MainActivity.this);
                break;
            case 400:
            case 404:
                ErrorResponse errorResponse = ApiUtil.getApiErrorFromResponse(response);
                DialogUtil.showApiErrorFromErrorResponse(MainActivity.this, errorResponse);
                break;
            default:
                DialogUtil.showGenericErrorMessage(MainActivity.this);
                break;
        }
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onIsExistingSuccess(boolean isExisting) {

    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void onItemSelected(Movie movie) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        Bundle extras = new Bundle();
        extras.putParcelable(MovieDetailsActivity.ARG_MOVIE_PARCEL, movie);
        intent.putExtras(extras);
        startActivity(intent);
    }
}
