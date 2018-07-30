package moviesapp.udacity.com.moviesapp.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import moviesapp.udacity.com.moviesapp.BuildConfig;
import moviesapp.udacity.com.moviesapp.R;
import moviesapp.udacity.com.moviesapp.adapter.ReviewsListRecyclerViewAdapter;
import moviesapp.udacity.com.moviesapp.api.model.Review;
import moviesapp.udacity.com.moviesapp.api.model.response.ErrorResponse;
import moviesapp.udacity.com.moviesapp.api.model.response.FetchMovieReviewsResponse;
import moviesapp.udacity.com.moviesapp.api.service.MoviesApiServiceHelper;
import moviesapp.udacity.com.moviesapp.util.ApiUtil;
import moviesapp.udacity.com.moviesapp.util.DialogUtil;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MovieReviewsDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieReviewsDialogFragment extends DialogFragment {

    public static final String TAG = "MovieReviewsDialogFragment_TAG";

    private static final String ARG_MOVIE_ID_PARAM = "ARG_MOVIE_ID_PARAM";

    private int movieId;

    @BindView(R.id.recyclerView_reviews)
    RecyclerView mRecyclerViewReviews;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.textView_no_ratings)
    TextView mTextViewNoReviews;

    private ReviewsListRecyclerViewAdapter adapter;

    private CompositeDisposable disposable = new CompositeDisposable();

    public MovieReviewsDialogFragment() {
        // Required empty public constructor
    }

    public static MovieReviewsDialogFragment newInstance(int movieId) {
        MovieReviewsDialogFragment fragment = new MovieReviewsDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MOVIE_ID_PARAM, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_MOVIE_ID_PARAM)) {
            movieId = getArguments().getInt(ARG_MOVIE_ID_PARAM);
        }
        adapter = new ReviewsListRecyclerViewAdapter(new ArrayList<Review>());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                dismiss();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_reviews_dialog, container, false);
        ButterKnife.bind(this, view);

        mRecyclerViewReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerViewReviews.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerViewReviews.setAdapter(adapter);

        showLoadingIndicator(true);
        disposable.add(
                getFetchReviewsObservable()
        );

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Window window = getDialog().getWindow();
            if (window != null) {
                window.setLayout(width, height);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    private void showLoadingIndicator(boolean showLoading) {
        progressBar.setVisibility(showLoading ? View.VISIBLE : View.GONE);
        mRecyclerViewReviews.setVisibility(showLoading ? View.GONE : View.VISIBLE);
    }

    private void showNoResultsMessage(boolean show) {
        mTextViewNoReviews.setVisibility(show ? View.VISIBLE : View.GONE);
        mRecyclerViewReviews.setVisibility(show ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @NonNull
    private DisposableSingleObserver<Response<FetchMovieReviewsResponse>> getFetchReviewsObservable() {
        return MoviesApiServiceHelper.getInstance(getContext()).fetchMovieReviews(movieId, BuildConfig.MOVIES_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Response<FetchMovieReviewsResponse>>() {
                    @Override
                    public void onSuccess(Response<FetchMovieReviewsResponse> response) {
                        handleFetchReviewsResponse(response);
                        showLoadingIndicator(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        showLoadingIndicator(false);
                        if (e instanceof ConnectException || e instanceof UnknownHostException) {
                            DialogUtil.showAlertDialogMessage(getActivity(), getString(R.string.api_connection_error_title), getString(R.string.api_connection_error_message));
                        } else {
                            DialogUtil.showGenericErrorMessage(getActivity());
                        }
                    }
                });
    }

    private void handleFetchReviewsResponse(Response<FetchMovieReviewsResponse> response) {
        showNoResultsMessage(false);
        int responseCode = response.code();
        switch (responseCode) {
            case 200:
                FetchMovieReviewsResponse fetchMovieReviewsResponse = response.body();
                if (fetchMovieReviewsResponse != null) {
                    if(fetchMovieReviewsResponse.getResults() != null && fetchMovieReviewsResponse.getResults().isEmpty()) {
                        showNoResultsMessage(true);
                    } else {
                        adapter.setReviews(fetchMovieReviewsResponse.getResults());
                    }
                }

                break;
            case 401:
                DialogUtil.showUnauthorizedErrorMessage(getActivity());
                break;
            case 400:
            case 404:
                ErrorResponse errorResponse = ApiUtil.getApiErrorFromResponse(response);
                DialogUtil.showApiErrorFromErrorResponse(getActivity(), errorResponse);
                break;
            default:
                DialogUtil.showGenericErrorMessage(getActivity());
                break;
        }
    }
}
