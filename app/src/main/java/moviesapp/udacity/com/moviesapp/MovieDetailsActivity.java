package moviesapp.udacity.com.moviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
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
import moviesapp.udacity.com.moviesapp.api.model.Movie;
import moviesapp.udacity.com.moviesapp.fragment.MovieTrailersFragment;

public class MovieDetailsActivity extends AppCompatActivity implements MovieTrailersFragment.OnFragmentInteractionListener {

    public static final String ARG_MOVIE_PARCEL = "MovieDetailsActivity_ARG_MOVIE_PARCEL";
    private Movie movie;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

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

    private BottomSheetBehavior bottomSheetBehavior;

    @BindString(R.string.api_movie_image_base_uri)
    String imageBaseUrl;

    @BindString(R.string.api_default_image_size)
    String defaultImageSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        validateAndGetMovieIdArgument();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        mTextViewOriginalTitle.setText(movie.getOriginal_title());

        bottomSheetBehavior = BottomSheetBehavior.from(mLinearLayoutTrailersContainer);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        mRelativeLayoutViewTrailerList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sheetBehaviorState = bottomSheetBehavior.getState();
                bottomSheetBehavior.setState(sheetBehaviorState == BottomSheetBehavior.STATE_COLLAPSED ?  BottomSheetBehavior.STATE_EXPANDED : BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

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

            Date parsedReleaseDate = null;
            try {
                parsedReleaseDate = releaseDateFormat.parse(releaseDate);
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

}
