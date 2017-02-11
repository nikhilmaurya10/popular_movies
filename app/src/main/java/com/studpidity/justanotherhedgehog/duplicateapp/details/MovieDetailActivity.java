package com.studpidity.justanotherhedgehog.duplicateapp.details;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.studpidity.justanotherhedgehog.duplicateapp.MainActivity;
import com.studpidity.justanotherhedgehog.duplicateapp.R;
import com.studpidity.justanotherhedgehog.duplicateapp.interfaces.CoverClickListener;
import com.studpidity.justanotherhedgehog.duplicateapp.model.MovieItem;

public class MovieDetailActivity extends AppCompatActivity implements CoverClickListener {
    MovieItem movieDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(mToolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        if(intent.hasExtra(MovieDetailFragment.ARG_MOVIE)) {
            movieDetails = (MovieItem) intent.getSerializableExtra(MovieDetailFragment.ARG_MOVIE);
        }
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putSerializable(MovieDetailFragment.ARG_MOVIE, movieDetails);
            arguments.putBoolean(MainActivity.ARG_TWO_PANE, false);
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClickCover(final String trailerUrl){
        ImageButton imageButton = (ImageButton)findViewById(R.id.movie_cover_play);
        imageButton.setVisibility(View.VISIBLE);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(trailerUrl));
                startActivity(intent);
            }
        });
    }
}