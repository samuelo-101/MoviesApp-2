package moviesapp.udacity.com.moviesapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import moviesapp.udacity.com.moviesapp.api.model.Movie;
import moviesapp.udacity.com.moviesapp.db.repo.MovieFavouriteEntityRepository;
import moviesapp.udacity.com.moviesapp.fragment.MovieReviewsDialogFragment;
import moviesapp.udacity.com.moviesapp.fragment.MovieTrailersFragment;

public class MovieDetailsActivity extends AppCompatActivity implements MovieTrailersFragment.OnFragmentInteractionListener, MovieFavouriteEntityRepository.DatabaseOperationCallback {

    public static final String ARG_MOVIE_PARCEL = "MovieDetailsActivity_ARG_MOVIE_PARCEL";
    private Movie movie;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab_toggle_favourite)
    FloatingActionButton mFabToggleFavourite;

    @BindView(R.id.imageView_backdrop)
    ImageView mImageViewBackdrop;

    @BindView(R.id.imageView_poster)
    ImageView mImageViewPoster;

    @BindView(R.id.textView_user_rating)
    TextView mTextViewUserRating;

    @BindView(R.id.textView_release_month)
    TextView mTextViewReleaseMonth;

    @BindView(R.id.textView_release_year)
    TextView mTextViewReleaseYear;

    @BindView(R.id.textView_original_title)
    TextView mTextViewOriginalTitle;

    @BindView(R.id.linearLayout_trailers_container)
    LinearLayout mLinearLayoutTrailersContainer;

    @BindView(R.id.relative_layout_view_trailer_list)
    RelativeLayout mRelativeLayoutViewTrailerList;

    @BindView(R.id.textView_overview)
    TextView mTextViewOverview;

    @BindView(R.id.button_view_reviews)
    Button mButtonReviews;

    private BottomSheetBehavior bottomSheetBehavior;

    @BindString(R.string.api_movie_image_base_uri)
    String imageBaseUrl;

    @BindString(R.string.api_default_image_size)
    String defaultImageSize;

    private MovieFavouriteEntityRepository movieFavouriteEntityRepository;
    private boolean isMovieFavourite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        validateAndGetMovieIdArgument();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        movieFavouriteEntityRepository = new MovieFavouriteEntityRepository(getApplicationContext(), this);

        setupUI();
    }

    private void setupUI() {
        StringBuilder imageUrlStringBuilder = new StringBuilder().append(this.imageBaseUrl).append(this.defaultImageSize);
        Picasso.with(this)
                .load(imageUrlStringBuilder.append(movie.getBackdrop_path()).toString())
                .error(R.drawable.ic_broken_image_grey)
                .placeholder(R.drawable.ic_image_grey)
                .into(mImageViewBackdrop);

        imageUrlStringBuilder = new StringBuilder().append(this.imageBaseUrl).append(this.defaultImageSize);
        Picasso.with(this)
                .load(imageUrlStringBuilder.append(movie.getPoster_path()).toString())
                .error(R.drawable.ic_broken_image_grey)
                .placeholder(R.drawable.ic_image_grey)
                .into(mImageViewPoster);

        mTextViewUserRating.setText(String.valueOf(movie.getVote_average()));
        setReleaseMonthAndYearFromDateString(movie.getRelease_date());
        mTextViewOriginalTitle.setText(movie.getTitle());
        mTextViewOverview.setText(movie.getOverview());

        bottomSheetBehavior = BottomSheetBehavior.from(mLinearLayoutTrailersContainer);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        mRelativeLayoutViewTrailerList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sheetBehaviorState = bottomSheetBehavior.getState();
                bottomSheetBehavior.setState(sheetBehaviorState == BottomSheetBehavior.STATE_COLLAPSED ?  BottomSheetBehavior.STATE_EXPANDED : BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        movieFavouriteEntityRepository.isExistingById(movie.getId());

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.frameLayout_trailers_fragment, MovieTrailersFragment.newInstance(movie.getId()));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void validateAndGetMovieIdArgument() {
        Intent intent = getIntent();
        if(intent != null) {
            Bundle extras = intent.getExtras();
            if(extras != null && extras.containsKey(ARG_MOVIE_PARCEL)) {
                this.movie = extras.getParcelable(ARG_MOVIE_PARCEL);
            } else {
                Intent goToMainActivity = new Intent(this, MainActivity.class);
                startActivity(goToMainActivity);
                finish();
            }
        }
    }

    private void setReleaseMonthAndYearFromDateString(String releaseDate) {
        if(!TextUtils.isEmpty(releaseDate)) {
            SimpleDateFormat releaseDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());
            SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

            try {
                Date parsedReleaseDate = releaseDateFormat.parse(releaseDate);
                mTextViewReleaseMonth.setText(monthFormat.format(parsedReleaseDate));
                mTextViewReleaseYear.setText(yearFormat.format(parsedReleaseDate));
            } catch (ParseException e) {
                e.printStackTrace();
                mTextViewReleaseYear.setText(getString(R.string.error_failed_to_parse_release_date));
            }
        } else {
            mTextViewReleaseYear.setText(getString(R.string.default_no_data_available_placeholder));
        }
    }

    @OnClick(R.id.button_view_reviews)
    void onButtonReviewsClickListener(View view) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        MovieReviewsDialogFragment movieReviewsDialogFragment = MovieReviewsDialogFragment.newInstance(movie.getId());
        movieReviewsDialogFragment.show(fragmentTransaction, MovieReviewsDialogFragment.TAG);
        fragmentTransaction.addToBackStack(null);
    }

    @OnClick(R.id.fab_toggle_favourite)
    void toggleFavouriteOnClickListener(View view) {
        if(isMovieFavourite) {
            movieFavouriteEntityRepository.deleteById(movie.getId());
            Snackbar.make(mTextViewOriginalTitle, getString(R.string.removed_from_favourites), Snackbar.LENGTH_LONG).show();
        } else {
            movieFavouriteEntityRepository.insert(MovieFavouriteEntityRepository.fromMovie(movie));
            Snackbar.make(mTextViewOriginalTitle, getString(R.string.added_to_favourites), Snackbar.LENGTH_LONG).show();
        }
        isMovieFavourite = !isMovieFavourite;
        updateFavouriteIndicator(isMovieFavourite);
    }

    private void updateFavouriteIndicator(boolean isSetToFavourite) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mFabToggleFavourite.setImageDrawable(isSetToFavourite ? getResources().getDrawable(R.drawable.ic_star_gold, getApplicationContext().getTheme())
            : getResources().getDrawable(R.drawable.ic_star_white, getApplicationContext().getTheme()));
        } else {
            mFabToggleFavourite.setImageDrawable(isSetToFavourite ? getResources().getDrawable(R.drawable.ic_star_gold) :
                    getResources().getDrawable(R.drawable.ic_star_white));
        }
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onIsExistingSuccess(final boolean isExisting) {
        isMovieFavourite = isExisting;
        updateFavouriteIndicator(isExisting);
    }

    @Override
    public void onError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.generic_error_title));
        builder.setMessage(message);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
