package moviesapp.udacity.com.moviesapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import moviesapp.udacity.com.moviesapp.adapter.TrailersListRecyclerViewAdapter;
import moviesapp.udacity.com.moviesapp.api.model.Video;
import moviesapp.udacity.com.moviesapp.api.model.response.FetchVideosResponse;
import moviesapp.udacity.com.moviesapp.api.service.MoviesApiServiceHelper;
import moviesapp.udacity.com.moviesapp.util.DialogUtil;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MovieTrailersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MovieTrailersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieTrailersFragment extends Fragment implements TrailersListRecyclerViewAdapter.TrailersListItemListener {

    public static final String ARG_MOVIE_ID = "MovieTrailersFragment_ARG_MOVIE_ID";

    private int movieId;

    @BindView(R.id.progressBar_movie_trailers)
    ProgressBar mProgressBarMovieTrailers;

    @BindView(R.id.recyclerView_trailers)
    RecyclerView mRecyclerViewTrailers;

    @BindView(R.id.textView_trailers_load_state)
    TextView mTextViewLoadState;

    private TrailersListRecyclerViewAdapter adapter;

    private OnFragmentInteractionListener mListener;

    private CompositeDisposable disposable = new CompositeDisposable();

    public MovieTrailersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movieId Parameter 1.
     * @return A new instance of fragment MovieTrailersFragment.
     */
    public static MovieTrailersFragment newInstance(int movieId) {
        MovieTrailersFragment fragment = new MovieTrailersFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MOVIE_ID, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.movieId = getArguments().getInt(ARG_MOVIE_ID);
        }

        adapter = new TrailersListRecyclerViewAdapter(new ArrayList<Video>(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_trailers, container, false);

        ButterKnife.bind(this, view);

        mRecyclerViewTrailers.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerViewTrailers.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerViewTrailers.setAdapter(adapter);

        showLoadingIndicator(true);
        disposable.add(
                getFetchVideosObservable()
        );

        return view;
    }

    @NonNull
    private DisposableSingleObserver<Response<FetchVideosResponse>> getFetchVideosObservable() {
        return MoviesApiServiceHelper.getInstance(getContext())
                .fetchMoviesVideos(movieId, BuildConfig.MOVIES_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Response<FetchVideosResponse>>() {
                    @Override
                    public void onSuccess(Response<FetchVideosResponse> response) {
                        if(response.code() == 200) {
                            FetchVideosResponse fetchVideosResponse = response.body();
                            if(fetchVideosResponse != null) {
                                adapter.setVideos(fetchVideosResponse.getResults());
                                if(fetchVideosResponse.getResults() != null && fetchVideosResponse.getResults().size() == 0) {
                                    showNoTrailersMessage();
                                }
                            } else {
                                adapter.clearVideos();
                            }
                        }
                        showLoadingIndicator(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        showLoadingIndicator(false);
                        if (e instanceof ConnectException || e instanceof UnknownHostException) {
                            DialogUtil.showAlertDialogMessage(MovieTrailersFragment.this.getActivity(), getString(R.string.api_connection_error_title), getString(R.string.api_connection_error_message));
                        } else {
                            DialogUtil.showAlertDialogMessage(MovieTrailersFragment.this.getActivity(), getString(R.string.api_connection_error_title), getString(R.string.error_failed_to_load_trailers));
                        }
                    }
                });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public void onTrailerClick(String youtubeKey) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.youtube_base_url_with_query_id) + youtubeKey)));
    }

    private void showLoadingIndicator(boolean showLoading) {
        mProgressBarMovieTrailers.setVisibility(showLoading ? View.VISIBLE : View.GONE);
        mRecyclerViewTrailers.setVisibility(showLoading ? View.GONE : View.VISIBLE);
        hideNoTrailersMessage();
    }

    private void showNoTrailersMessage() {
        mTextViewLoadState.setVisibility(View.VISIBLE);
        mTextViewLoadState.setText(getString(R.string.no_trailers_for_movie));
        mProgressBarMovieTrailers.setVisibility(View.GONE);
        mRecyclerViewTrailers.setVisibility(View.GONE);
    }

    private void hideNoTrailersMessage() {
        mTextViewLoadState.setVisibility(View.GONE);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

    }
}
