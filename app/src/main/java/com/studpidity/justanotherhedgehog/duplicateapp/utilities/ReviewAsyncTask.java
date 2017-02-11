package com.studpidity.justanotherhedgehog.duplicateapp.utilities;

import android.os.AsyncTask;
import android.util.Log;

import com.studpidity.justanotherhedgehog.duplicateapp.model.ReviewItem;

import java.util.ArrayList;
import java.util.List;


public class ReviewAsyncTask extends AsyncTask<Integer, Void, String >{

    private IAsyncTask asyncTaskListener;
    private ArrayList<ReviewItem> mReviewList;


    public ReviewAsyncTask(IAsyncTask asyncTaskListener){
        this.asyncTaskListener = asyncTaskListener;
    }
    public interface IAsyncTask {
        void onFinished(List<?> arrayList);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mReviewList = null;
    }

    @Override
    protected String doInBackground(Integer... ints) {
        int movieId;
        movieId = ints[0];
        return MovieUtil.fetchJsonFromUrl(MovieUtil.makeReviewsUrl(movieId));
    }

    @Override
    protected void onPostExecute(String reviewsJsonString) {
        mReviewList = (ArrayList<ReviewItem>) MovieUtil.parseJson(reviewsJsonString, ReviewItem.getClassName());
        asyncTaskListener.onFinished(mReviewList);
    }
}
