package com.studpidity.justanotherhedgehog.duplicateapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.ArrayList;
import com.squareup.picasso.Picasso;

import com.studpidity.justanotherhedgehog.duplicateapp.R;
import com.studpidity.justanotherhedgehog.duplicateapp.model.MovieItem;
import com.studpidity.justanotherhedgehog.duplicateapp.provider.MovieContract;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private Context context;
    private ArrayList<MovieItem> mMovieList;
    private GridItemClickLister mOnClickListener;

    public interface GridItemClickLister {
        void onGridItemClick(int clickedItemIndex);
    }

    public MovieAdapter(Context context, ArrayList<MovieItem> movieList) {
        this.mMovieList = movieList;
        this.context = context;
    }
    public void setOnItemClickListener(final GridItemClickLister mItemClickListener) {
        this.mOnClickListener = mItemClickListener;
    }
    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.ViewHolder viewHolder, int i) {
        Picasso.with(context)
                .load(mMovieList.get(i).getBaseImageUrlPath() + mMovieList.get(i).getPoster_path())
                .placeholder(R.drawable.placeholder_light).error(R.drawable.error_placeholder)
                .into(viewHolder.poster);
    }

    @Override
    public int getItemCount() {
        if(mMovieList == null) return 0;
        return mMovieList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.poster) ImageView poster;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onGridItemClick(clickedPosition);
        }
    }

    public void add(ArrayList<MovieItem> movies) {
        if (this.mMovieList == null) {
            this.mMovieList = movies;
        } else {
            this.mMovieList.addAll(movies);
        }
    }

    public void add(Cursor cursor) {
        if (this.mMovieList != null) {
            int size = this.mMovieList.size();
            this.mMovieList.clear();
            this.notifyItemRangeRemoved(0, size);
        } else {
            mMovieList = new ArrayList<>();
        }
        ArrayList<MovieItem> favList = new ArrayList<>();
        if(!cursor.isClosed()){
            if (cursor.moveToFirst()){
                do{
                    MovieItem movieItem = new MovieItem();
                    movieItem.setId(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)));
                    movieItem.setTitle(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE)));
                    movieItem.setPoster_path(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH)));
                    movieItem.setOverview(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW)));
                    movieItem.setVote_average(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE)));
                    movieItem.setRelease_date(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE)));
                    movieItem.setBackdrop_path(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH)));
                    favList.add(movieItem);
                }while(cursor.moveToNext());
            }
            this.mMovieList.addAll(favList);
            notifyDataSetChanged();
            cursor.close();
        }
    }

    public MovieItem getMovieItem(int index) {
        return mMovieList.get(index);
    }

    public void setMovieList(ArrayList<MovieItem> movies) {
        if (this.mMovieList == null) {
            this.mMovieList = movies;
        } else {
            this.mMovieList.addAll(movies);
        }
    }
    public void clear() {
        if (this.mMovieList != null) {
            int size = this.mMovieList.size();
            this.mMovieList.clear();
            this.notifyItemRangeRemoved(0, size);
        }
    }
    public ArrayList<MovieItem> getMovies(){
        if( mMovieList!=null) return mMovieList;
        return null;
    }
}
