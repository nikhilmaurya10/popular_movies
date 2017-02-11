package com.studpidity.justanotherhedgehog.duplicateapp;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.studpidity.justanotherhedgehog.duplicateapp.details.MovieDetailActivity;
import com.studpidity.justanotherhedgehog.duplicateapp.details.MovieDetailFragment;
import com.studpidity.justanotherhedgehog.duplicateapp.model.MovieItem;

public class MainActivity extends AppCompatActivity implements MovieCollectionFragment.Callback {
    private static final String TAG_FRAGMENT = "MOVIE_FRAGMENT";
    public static final String ARG_TWO_PANE = "TWO_PANE";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.movie_detail_container, new MovieDetailFragment(), TAG_FRAGMENT)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }


    @Override
    public void onItemSelected(MovieItem movie) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putBoolean(ARG_TWO_PANE, true);
            args.putSerializable(MovieDetailFragment.ARG_MOVIE, movie);
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, TAG_FRAGMENT)
                    .commit();
            //one-pane mode
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(MovieDetailFragment.ARG_MOVIE, movie);
            startActivity(intent);
            }
    }
}
