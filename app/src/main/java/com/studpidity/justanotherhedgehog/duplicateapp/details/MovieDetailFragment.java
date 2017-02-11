package com.studpidity.justanotherhedgehog.duplicateapp.details;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.studpidity.justanotherhedgehog.duplicateapp.MainActivity;
import com.studpidity.justanotherhedgehog.duplicateapp.MovieCollectionFragment;
import com.studpidity.justanotherhedgehog.duplicateapp.adapters.ReviewAdapter;
import com.studpidity.justanotherhedgehog.duplicateapp.adapters.TrailerAdapter;
import com.studpidity.justanotherhedgehog.duplicateapp.R;
import com.studpidity.justanotherhedgehog.duplicateapp.interfaces.CoverClickListener;
import com.studpidity.justanotherhedgehog.duplicateapp.interfaces.FavoriteChangeListener;
import com.studpidity.justanotherhedgehog.duplicateapp.model.MovieItem;
import com.studpidity.justanotherhedgehog.duplicateapp.model.ReviewItem;
import com.studpidity.justanotherhedgehog.duplicateapp.model.TrailerItem;
import com.studpidity.justanotherhedgehog.duplicateapp.utilities.ReviewAsyncTask;
import com.studpidity.justanotherhedgehog.duplicateapp.utilities.TrailerAsyncTask;
import com.studpidity.justanotherhedgehog.duplicateapp.provider.MovieContract;


public class MovieDetailFragment extends Fragment implements TrailerAsyncTask.IAsyncTask,
        TrailerAdapter.Callbacks, ReviewAsyncTask.IAsyncTask, ReviewAdapter.Callbacks {

    @SuppressWarnings("unused")
    public static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    public static final String ARG_MOVIE = "ARG_MOVIE";
    private MovieItem mMovie;
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;
    private String shareString;
    public int smallestScreenWidth=0;
    @BindView(R.id.trailer_list) RecyclerView mRecyclerViewForTrailers;
    @BindView(R.id.review_list) RecyclerView mRecyclerViewForReviews;
    @BindView(R.id.movie_title) TextView mMovieTitleView;
    @BindView(R.id.movie_overview) TextView mMovieOverviewView;
    @BindView(R.id.movie_release_date) TextView mMovieReleaseDateView;
    @BindView(R.id.movie_user_rating) TextView mMovieRatingView;
    @BindView(R.id.movie_poster) ImageView mMoviePosterView;
    @BindView(R.id.help_text) TextView mHelpTextView;
    @BindView(R.id.no_trailer_text_view) TextView noTrailerTextView;
    @BindView(R.id.no_review_text_view) TextView noReviewTextView;
    @BindView(R.id.container) LinearLayout mContainerLinearLayout;
    @BindView(R.id.fav_button) ImageButton favToggle;
    @BindView(R.id.base_container) LinearLayout baseContainer;
    CoordinatorLayout coordinatorLayout;

    boolean mTowPane;
    boolean isChecked;
    private Snackbar snackbar;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);
        ButterKnife.bind(this,rootView);
        Bundle arguments = getArguments();
        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.base_detail_layout);
        if (arguments != null) {
            mTowPane = getArguments().getBoolean(MainActivity.ARG_TWO_PANE);

            if(!mTowPane) {

                Configuration config = getResources().getConfiguration();
                smallestScreenWidth = config.smallestScreenWidthDp;
                if (smallestScreenWidth > 0) {
                    Resources r = getResources();
                    int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, smallestScreenWidth, r.getDisplayMetrics());
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            px,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    );
                    lp.gravity = Gravity.CENTER_HORIZONTAL;

                    mContainerLinearLayout.setLayoutParams(lp);
                    baseContainer.setBackgroundColor(getResources().getColor(R.color.grey_background));
                } else {
                    mContainerLinearLayout.setBackgroundColor(getResources().getColor(R.color.windowBackground));
                }
            }
            mHelpTextView.setVisibility(View.GONE);
            mContainerLinearLayout.setVisibility(View.VISIBLE);
            mMovie = (MovieItem) getArguments().getSerializable(ARG_MOVIE);
            if(mMovie!=null) {
                shareString = mMovie.getTitle()
                        + "("
                        + mMovie.getRelease_date().split("-")[0]
                        + ")" + "Rating:"
                        + mMovie.getVote_average()
                        + mMovie.OUTOF
                        + ". \nhttps://www.themoviedb.org/movie/"
                        + mMovie.getId()
                        + "-"
                        + mMovie.getTitle().replaceAll("[^a-zA-Z0-9]", "-").toLowerCase();
            }

            Activity activity = getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout)
                    activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null && activity instanceof MovieDetailActivity) {
                appBarLayout.setTitle(mMovie.getTitle());
            }
            ImageView movieBackdrop = (ImageView) activity.findViewById(R.id.movie_backdrop);
            if (movieBackdrop != null) {
                Picasso.with(activity)
                        .load(mMovie.getBaseBackDropImageUrlPath() + mMovie.getBackdrop_path())
                        .placeholder(R.color.colorPrimary)
                        .error(R.color.colorPrimary)
                        .into(movieBackdrop);
            }
            new ReviewAsyncTask(this).execute(mMovie.getId());
            new TrailerAsyncTask(this).execute(mMovie.getId());
            favToggle.setOnClickListener(favButtonListener(favToggle));
            favToggle.setVisibility(View.INVISIBLE);
            updateFavoriteButtons();
            mMovieTitleView.setText(mMovie.getTitle());
            mMovieRatingView.setText(mMovie.getVote_average()+mMovie.OUTOF);
            mMovieOverviewView.setText(mMovie.getOverview());
            mMovieReleaseDateView.setText(mMovie.getRelease_date());
            Picasso.with(getContext())
                    .load(mMovie.getBaseImageUrlPath() + mMovie.getPoster_path())
                    .placeholder(R.drawable.placeholder_light)
                    .error(R.drawable.error_placeholder)
                    .into(mMoviePosterView);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            mRecyclerViewForTrailers.setLayoutManager(layoutManager);
            mTrailerAdapter = new TrailerAdapter(new ArrayList<TrailerItem>(), this);
            mRecyclerViewForTrailers.setAdapter(mTrailerAdapter);
            mRecyclerViewForTrailers.setNestedScrollingEnabled(false);

            mReviewAdapter = new ReviewAdapter(new ArrayList<ReviewItem>(), this);
            mRecyclerViewForReviews.setAdapter(mReviewAdapter);
        } else {
            mHelpTextView.setVisibility(View.VISIBLE);
            mContainerLinearLayout.setVisibility(View.INVISIBLE);
        }
        return rootView;
    }

    @Override
    public void watch(TrailerItem trailer, int position) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(TrailerItem.baseTrailerUrl + trailer.getTrailerUrl())));
    }

    @Override
    public void read(ReviewItem review, int position) {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(review.getUrl())));
    }

    @Override
    public void onFinished(List<?> arrayList) {
        if (arrayList!=null && !arrayList.isEmpty()) {
            if (arrayList.get(0) instanceof ReviewItem) {
                noReviewTextView.setVisibility(View.GONE);
                mReviewAdapter.add((ArrayList<ReviewItem>) arrayList);
            } else if (arrayList.get(0) instanceof TrailerItem && mTrailerAdapter.getItemCount() >= 0 ) {
                mTrailerAdapter.add((ArrayList<TrailerItem>) arrayList);
                noTrailerTextView.setVisibility(View.GONE);
                if(getActivity() instanceof  MovieDetailActivity) {
                    CoverClickListener coverClickListener = (CoverClickListener) getActivity();
                    coverClickListener.onClickCover(TrailerItem.baseTrailerUrl + ((TrailerItem) arrayList.get(0)).getTrailerUrl());
                }
            }
        }
        else {
            mReviewAdapter.add(new ArrayList<ReviewItem>());
            if (mReviewAdapter.getItemCount()<=0) {
                noReviewTextView.setVisibility(View.VISIBLE);
            }
            mTrailerAdapter.add(new ArrayList<TrailerItem>());
            if (mTrailerAdapter.getItemCount()<=0){
                noTrailerTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void markAsFavorite() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (!isFavorite()) {
                    Picasso.with(getContext()).load(mMovie.getBaseImageUrlPath()+mMovie.getPoster_path()).fetch();
                    Picasso.with(getContext()).load(mMovie.getBaseBackDropImageUrlPath()+mMovie.getBackdrop_path()).fetch();
                    ContentValues movieValues = new ContentValues();
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                            mMovie.getId());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
                            mMovie.getTitle());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,
                            mMovie.getPoster_path());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
                            mMovie.getOverview());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE,
                            mMovie.getVote_average());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
                            mMovie.getRelease_date());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH,
                            mMovie.getBackdrop_path());
                    getContext().getContentResolver().insert(
                            MovieContract.MovieEntry.CONTENT_URI,
                            movieValues
                    );
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(mTowPane){
                FavoriteChangeListener favoriteChangeListener = (FavoriteChangeListener) getFragmentManager().getFragments().get(0);
                favoriteChangeListener.onUpdate();
                }
            }
        }.execute();
    }

    public void removeFromFavorites() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (isFavorite()) {
                    getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + mMovie.getId(), null);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(mTowPane){
                    FavoriteChangeListener favoriteChangeListener = (FavoriteChangeListener) getFragmentManager().getFragments().get(0);
                    favoriteChangeListener.onUpdate();
                }
            }
        }.execute();
    }


    private void updateFavoriteButtons() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                return isFavorite();
            }

            @Override
            protected void onPostExecute(Boolean isFavorite) {
                favToggle.setVisibility(View.VISIBLE);
                if (isFavorite) {
                    favToggle.setImageResource(R.drawable.ic_favorite_full);
                    isChecked =true;
                } else {
                    isChecked = false;
                    favToggle.setImageResource(R.drawable.ic_favorite_border);
                }
            }
        }.execute();

    }

    private boolean isFavorite() {
        Cursor movieCursor = null;
        boolean isFav = false;
        Context context=null;
        try {
            context = getContext();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(context!=null){
            try {
                movieCursor = context.getContentResolver().query(
                        MovieContract.MovieEntry.CONTENT_URI,
                        new String[]{MovieContract.MovieEntry.COLUMN_MOVIE_ID},
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + mMovie.getId(),
                        null,
                        null);
                if (movieCursor != null && movieCursor.moveToFirst()) {
                    isFav=true;
                } else {
                    isFav=false;
                }
            } finally {
                if (movieCursor != null) {
                    movieCursor.close();
                }
            }
        }
        return isFav;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(mMovie != null) {
        inflater.inflate(R.menu.menu, menu);

            MenuItem menuItem = menu.findItem(R.id.action_share);
            menuItem.setIntent(createShareForecastIntent());
        }
}
    private Intent createShareForecastIntent() {
        return ShareCompat.IntentBuilder.from(getActivity())
                .setType("text/plain")
                .setText(shareString)
                .getIntent();
    }


    private View.OnClickListener favButtonListener(final ImageButton buttton){

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isChecked) {
                    markAsFavorite();
                    buttton.setImageResource(R.drawable.ic_favorite_full);
                    snackbar.make(getView(),
                            getContext().getResources().getString(R.string.button_on_text),
                            Snackbar.LENGTH_SHORT)
                            .setAction(getContext().getResources().getString(R.string.favorites_label),
                                    snackBarOnclickListener()).show();

                } else {
                    removeFromFavorites();
                    buttton.setImageResource(R.drawable.ic_favorite_border);
                    snackbar.make(getView(),
                            getContext().getResources().getString(R.string.button_off_text),
                            Snackbar.LENGTH_SHORT)
                            .setAction(getContext().getResources().getString(R.string.favorites_label),
                                    snackBarOnclickListener()).show();

                }
                isChecked = !isChecked;
            }
        };
    }

    private View.OnClickListener snackBarOnclickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainActivity.class).setAction(MovieCollectionFragment.ARG_IS_FAVORITE);
                startActivity(intent);

            }
        };
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ARG_MOVIE, mMovie);
    }
}
