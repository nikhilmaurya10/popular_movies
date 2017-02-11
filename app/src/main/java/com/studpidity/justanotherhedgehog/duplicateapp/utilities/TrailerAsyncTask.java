package com.studpidity.justanotherhedgehog.duplicateapp.utilities;


import android.os.AsyncTask;
import android.util.Log;

import com.studpidity.justanotherhedgehog.duplicateapp.model.TrailerItem;

import java.util.ArrayList;
import java.util.List;

public class TrailerAsyncTask extends AsyncTask<Integer, Void, String> {
    private IAsyncTask asyncTaskListener;
    private ArrayList<TrailerItem> mTrailerList;

    public TrailerAsyncTask(IAsyncTask asyncTaskListener) {
        this.asyncTaskListener = asyncTaskListener;
    }

    public interface IAsyncTask {
        void onFinished(List<?> arrayList);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mTrailerList = null;
    }

    @Override
    protected String doInBackground(Integer... ints) {
        int movieId;
        movieId = ints[0];
        return MovieUtil.fetchJsonFromUrl(MovieUtil.makeTrailersUrl(movieId));
    }

    @Override
    protected void onPostExecute(String reviewsJsonString) {
        mTrailerList = (ArrayList<TrailerItem>) MovieUtil.parseJson(reviewsJsonString, TrailerItem.getClassName());
        asyncTaskListener.onFinished(mTrailerList);
    }

}
