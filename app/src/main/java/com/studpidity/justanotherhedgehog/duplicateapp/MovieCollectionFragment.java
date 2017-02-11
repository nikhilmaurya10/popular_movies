package com.studpidity.justanotherhedgehog.duplicateapp;


import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.rockerhieu.rvadapter.endless.EndlessRecyclerViewAdapter;

import com.studpidity.justanotherhedgehog.duplicateapp.adapters.MovieAdapter;
import com.studpidity.justanotherhedgehog.duplicateapp.interfaces.FavoriteChangeListener;
import com.studpidity.justanotherhedgehog.duplicateapp.model.MovieItem;
import com.studpidity.justanotherhedgehog.duplicateapp.provider.MovieContract;
import com.studpidity.justanotherhedgehog.duplicateapp.utilities.MovieUtil;


public class MovieCollectionFragment extends Fragment implements MovieAdapter.GridItemClickLister, FavoriteChangeListener, EndlessRecyclerViewAdapter.RequestToLoadMoreListener {
    private LinearLayoutManager mStaggeredLayoutManager;
    public static final String ARG_IS_FAVORITE = "IS_FAVORITE";
    private static final String ARG_SORT_ORDER = "SORT_ORDER";
    private static final String ARG_FIRST_RUN = "IS_FIRST_RUN";
    private static final String ARG_POPULAR_MOVIES = "popular";
    private static final String ARG_TOP_RATED = "top_rated";

    ArrayList<MovieItem> allMovies = new ArrayList<>();
    ArrayList<MovieItem> favouriteMovies = new ArrayList<>();
    private String sortOrderValue = null;
    private Integer pageNumberValue = -1;
    private boolean showFavorites;
    boolean firstRun = true;
    int smallestScreenWidth;

    private MovieAdapter mAdapter;
    private EndlessRecyclerViewAdapter endlessRecyclerViewAdapter;

    @BindView(R.id.card_recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.loading_indicator) ProgressBar mProgressBar;
    @BindView(R.id.error_message) TextView mErrorTextView;
    @BindView(R.id.button_refresh) ImageButton refreshButton;
    @BindView(R.id.no_fav) TextView noFavTextView;
    @BindView(R.id.tab_text) TextView mTabText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
//        from snackbar of moviedetailfragment
        showFavorites = getActivity().getIntent().getAction().contentEquals(ARG_IS_FAVORITE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(showFavorites) {
            new FetchFavoritesTask().execute();
        }
    }

    @Override
    public void onGridItemClick(int clickedItemIndex) {
        MovieItem mMovie = mAdapter.getMovieItem(clickedItemIndex);
        ((Callback) getActivity()).onItemSelected(mMovie);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this,view);
        setupLayout();
        if(savedInstanceState != null){
            showFavorites =savedInstanceState.getBoolean(ARG_IS_FAVORITE);
            firstRun = savedInstanceState.getBoolean(ARG_FIRST_RUN);
            if (showFavorites){
                setTabText();
                new FetchFavoritesTask().execute();
                return view;
            }
            sortOrderValue = savedInstanceState.getString(ARG_SORT_ORDER);
            setTabText();
            pageNumberValue = 1;
            new MovieAsyncTask().execute(MovieUtil.makeMovieUrl(sortOrderValue,pageNumberValue));
        } else {
            if(showFavorites) {
                setTabText();
                new FetchFavoritesTask().execute();
            } else if (MovieUtil.isOnline(getContext())) {
                    sortOrderValue = ARG_POPULAR_MOVIES;
                    setTabText();

            } else {
                showFavorites = true;
                View sv =getActivity().findViewById(android.R.id.content);
                if(sv != null) {
                    Snackbar.make(sv, getContext().getResources().getString(R.string.offline_label), Snackbar.LENGTH_LONG).show();
                }
                new FetchFavoritesTask().execute();

                setTabText();
            }
        }
        return view;
    }

    public  class MovieAsyncTask extends AsyncTask<URL, Integer, ArrayList<MovieItem>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mErrorTextView.setVisibility(View.INVISIBLE);
            noFavTextView.setVisibility(View.GONE);
            refreshButton.setVisibility(View.INVISIBLE);
        }

        protected ArrayList<MovieItem> doInBackground(URL... receivedUrl) {
            String cn = MovieItem.getClassName();
            return  (ArrayList<MovieItem>) MovieUtil.parseJson(MovieUtil.fetchJsonFromUrl(receivedUrl[0]), cn);

        }
        protected void onPostExecute(ArrayList<MovieItem> moviesPerPage) {
            refreshButton.setVisibility(View.INVISIBLE);
            if(moviesPerPage != null) {
                allMovies.addAll(moviesPerPage);
                mRecyclerView.setVisibility(View.VISIBLE);
                mAdapter.setMovieList(moviesPerPage);
                endlessRecyclerViewAdapter.onDataReady(true);
                mAdapter.notifyDataSetChanged();
                setOnClickListenerOnItems(allMovies);
            } else {
                allMovies.clear();
                mProgressBar.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
                mErrorTextView.setVisibility(View.VISIBLE);
                refreshButton.setVisibility(View.VISIBLE);
            }
        }
    }


    private void setOnClickListenerOnItems(final Collection<MovieItem> movies) {
        MovieAdapter.GridItemClickLister onItemClickListener = new MovieAdapter.GridItemClickLister() {
            @Override
            public void onGridItemClick(int position) {
                MovieItem mMovie;
                if(position >= movies.size() || position < 0 || movies.size() ==0 ) return;
                if (showFavorites) {
                    mMovie = (MovieItem) movies.toArray()[position];
                } else {
                    mMovie = (MovieItem) allMovies.toArray()[position];
                }
                ((Callback) getActivity()).onItemSelected(mMovie);
            }
        };
        mAdapter.setOnItemClickListener(onItemClickListener);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main,menu);

        MenuItem action_sort_by_popularity = menu.findItem(R.id.sort_popularity);
        MenuItem action_sort_by_rating = menu.findItem(R.id.sort_rating);
        MenuItem action_show_fav = menu.findItem(R.id.favorites);
        if(!showFavorites) {
            if (sortOrderValue.contentEquals(ARG_POPULAR_MOVIES)) {
                if (!action_sort_by_popularity.isChecked()) {
                    action_sort_by_popularity.setChecked(true);
                }
            } else if (sortOrderValue.contentEquals(ARG_TOP_RATED)) {
                if (!action_sort_by_rating.isChecked()) {
                    action_sort_by_rating.setChecked(true);
                }
            }
        } else {
            action_show_fav.setChecked(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.favorites:
                showFavorites = true;
                setTabText();
                new FetchFavoritesTask().execute();
                if(!MovieUtil.isOnline(getContext())) {
                    if(getView()!=null) {
                        Snackbar.make(getView(), getContext().getResources().getString(R.string.offline_label), Snackbar.LENGTH_LONG).show();
                    }
                }
                return true;
            case R.id.sort_popularity:
                showFavorites = false;
                sortOrderValue =ARG_POPULAR_MOVIES;
                break;
            case R.id.sort_rating:
                showFavorites = false;
                sortOrderValue =ARG_TOP_RATED;
                break;
            case R.id.help:
                Toast.makeText(getActivity(),getContext().getResources().getString(R.string.help_menu_text), Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
            default:
                return false;
        }
        setTabText();
        mProgressBar.setVisibility(View.VISIBLE);
        item.setChecked(true);
        pageNumberValue = 1;
        allMovies.clear();
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
        new MovieAsyncTask().execute(MovieUtil.makeMovieUrl(sortOrderValue,pageNumberValue));
        return super.onOptionsItemSelected(item);
    }


    public  class FetchFavoritesTask extends AsyncTask<Void, Integer,  Cursor> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pageNumberValue = 1;
            mErrorTextView.setVisibility(View.INVISIBLE);
            refreshButton.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
            noFavTextView.setVisibility(View.GONE);

        }

        protected  Cursor doInBackground(Void... voids) {
            return getContext().getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
        }
        protected void onPostExecute( Cursor cursor) {
            showFavorites =true;
            mProgressBar.setVisibility(View.INVISIBLE);
            if(cursor != null ) {
                if (cursor.getCount() > 0) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mAdapter.clear();
                    mAdapter.add(cursor);
                    endlessRecyclerViewAdapter.onDataReady(true);
                    cursor.close();
                    favouriteMovies = mAdapter.getMovies();
                    if (favouriteMovies.size() > 0) {
                        setOnClickListenerOnItems(favouriteMovies);
                    }
                } else noFavTextView.setVisibility(View.VISIBLE);
            } else {
                mRecyclerView.setVisibility(View.INVISIBLE);
                mErrorTextView.setVisibility(View.VISIBLE);
                refreshButton.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onLoadMoreRequested() {
        if (sortOrderValue != null && !showFavorites){
            pageNumberValue++;
            if(firstRun) pageNumberValue =1;
            firstRun = false;
            new MovieAsyncTask().execute(MovieUtil.makeMovieUrl(sortOrderValue,pageNumberValue));

        }
    }



    public interface Callback {

        //callback for when an item has been selected.

        void onItemSelected(MovieItem movie);
    }

    @Override
    public void onUpdate() {
        if (showFavorites){
            new FetchFavoritesTask().execute();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ARG_IS_FAVORITE,showFavorites);
        outState.putBoolean(ARG_FIRST_RUN,firstRun);
        outState.putString(ARG_SORT_ORDER,sortOrderValue);
    }
    void setupLayout(){

        Configuration config = getResources().getConfiguration();
        smallestScreenWidth = config.smallestScreenWidthDp;
        if (config.smallestScreenWidthDp >= 600
                && (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)) {
            mStaggeredLayoutManager = new GridLayoutManager(getActivity(), 2);
        } else if (config.smallestScreenWidthDp < 600
                && (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)) {
            mStaggeredLayoutManager = new GridLayoutManager(getActivity(), 4);
        } else {
            mStaggeredLayoutManager = new GridLayoutManager(getActivity(), 2);
        }

        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(mStaggeredLayoutManager);
        mAdapter = new MovieAdapter(getActivity(),null);
        endlessRecyclerViewAdapter = new EndlessRecyclerViewAdapter(getActivity(), mAdapter, this,R.layout.empty_state,true);
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setAdapter(endlessRecyclerViewAdapter);
        mRecyclerView.setVisibility(View.INVISIBLE);


        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageNumberValue=1;
                allMovies.clear();
                new MovieAsyncTask().execute(MovieUtil.makeMovieUrl(sortOrderValue,pageNumberValue));
            }
        });
    }
    void setTabText (){
        if (showFavorites){
            mTabText.setText("Favorites");
        } else {
            if(sortOrderValue == "popular") {
                mTabText.setText("Most Popular");
            } else mTabText.setText("Top Rated");
        }
    }
}

