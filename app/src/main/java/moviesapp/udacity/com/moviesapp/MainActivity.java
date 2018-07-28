package moviesapp.udacity.com.moviesapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import java.net.ConnectException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import moviesapp.udacity.com.moviesapp.adapter.MoviesGridRecyclerViewAdapter;
import moviesapp.udacity.com.moviesapp.api.model.response.ErrorResponse;
import moviesapp.udacity.com.moviesapp.api.model.response.FetchMoviesResponse;
import moviesapp.udacity.com.moviesapp.api.service.MoviesApiServiceHelper;
import moviesapp.udacity.com.moviesapp.util.ApiUtil;
import moviesapp.udacity.com.moviesapp.api.model.Movie;
import moviesapp.udacity.com.moviesapp.util.SharedPrefsUtil;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.recyclerView_movies)
    RecyclerView mRecyclerViewMovies;

    private AlertDialog alertDialogSortOrder;
    private AlertDialog mAlertDialogApplicationMessage;

    private MoviesGridRecyclerViewAdapter adapter;

    private final CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        mRecyclerViewMovies.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        adapter = new MoviesGridRecyclerViewAdapter(getApplicationContext(), new ArrayList<Movie>());
        mRecyclerViewMovies.setAdapter(adapter);

        setupUIBasedOnOrderPreference();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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

            boolean isSortByPopular = SharedPrefsUtil.getBoolean(getApplicationContext(), getString(R.string.shared_prefs_sort_order_key));
            radioButtonSortByPopular.setChecked(isSortByPopular);
            radioButtonSortByTopRated.setChecked(!isSortByPopular);

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
    }

    @Override
    public void onBackPressed() {
        if (mAlertDialogApplicationMessage != null && mAlertDialogApplicationMessage.isShowing()) {
            mAlertDialogApplicationMessage.dismiss();
        } else {
            super.onBackPressed();
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
                SharedPrefsUtil.putBoolean(getApplicationContext(), getString(R.string.shared_prefs_sort_order_key), !checked);
                break;
            case R.id.radio_sort_by_popular:
            default:
                disposable.add(
                        getFetchPopularMoviesObservable()
                );
                SharedPrefsUtil.putBoolean(getApplicationContext(), getString(R.string.shared_prefs_sort_order_key), checked);
                break;
        }

        alertDialogSortOrder.dismiss();
    }

    private void setupUIBasedOnOrderPreference() {
        showLoadingIndicator(true);

        boolean isSortByPopular = SharedPrefsUtil.getBoolean(getApplicationContext(), getString(R.string.shared_prefs_sort_order_key));
        if(isSortByPopular) {
            disposable.add(
                    getFetchPopularMoviesObservable()
            );
        } else {
            disposable.add(
                    getFetchTopRatedMoviesObservable()
            );
        }
    }

    private void showLoadingIndicator(boolean showLoading) {
        mProgressBar.setVisibility(showLoading ? View.VISIBLE : View.GONE);
        mRecyclerViewMovies.setVisibility(showLoading ? View.GONE : View.VISIBLE);
    }

    @NonNull
    private DisposableSingleObserver<Response<FetchMoviesResponse>> getFetchPopularMoviesObservable() {
        return MoviesApiServiceHelper.getInstance(getApplicationContext())
                .fetchPopularMovies(getString(R.string.api_key))
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
                        if (e instanceof ConnectException) {
                            showConnectionFailedErrorMessage();
                        } else {
                            showGenericErrorMessage();
                        }

                    }
                });
    }

    @NonNull
    private DisposableSingleObserver<Response<FetchMoviesResponse>> getFetchTopRatedMoviesObservable() {
        return MoviesApiServiceHelper.getInstance(getApplicationContext())
                .fetchTopRatedMovies(getString(R.string.api_key))
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
                        if (e instanceof ConnectException) {
                            showConnectionFailedErrorMessage();
                        } else {
                            showGenericErrorMessage();
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
                showUnauthorizedErrorMessage();
                break;
            case 400:
            case 404:
                ErrorResponse errorResponse = ApiUtil.getApiErrorFromResponse(response);
                showApiErrorFromErrorResponse(errorResponse);
                break;
            default:
                showGenericErrorMessage();
                break;
        }
    }

    private void showConnectionFailedErrorMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.api_connection_error_title));
        builder.setMessage(getString(R.string.api_connection_error_message));
        if (mAlertDialogApplicationMessage != null && mAlertDialogApplicationMessage.isShowing()) {
            mAlertDialogApplicationMessage.dismiss();
        }
        mAlertDialogApplicationMessage = builder.create();
        mAlertDialogApplicationMessage.show();
    }

    private void showUnauthorizedErrorMessage() {
        showDialogMessage(getString(R.string.api_generic_error_title), getString(R.string.api_unauthorized_error_message));
    }

    private void showGenericErrorMessage() {
        showDialogMessage(getString(R.string.api_generic_error_title), getString(R.string.api_generic_error_message));
    }

    private void showApiErrorFromErrorResponse(ErrorResponse errorResponse) {
        if (errorResponse == null) {
            showGenericErrorMessage();
        } else {
            String message = new StringBuilder().append(errorResponse.getStatus_message())
                    .append(" (Code: ")
                    .append(errorResponse.getStatus_code())
                    .append(")").toString();
            showDialogMessage(getString(R.string.api_generic_error_title), message);
        }
    }

    private void showDialogMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        if (mAlertDialogApplicationMessage != null && mAlertDialogApplicationMessage.isShowing()) {
            mAlertDialogApplicationMessage.dismiss();
        }
        mAlertDialogApplicationMessage = builder.create();
        mAlertDialogApplicationMessage.show();
    }
}
