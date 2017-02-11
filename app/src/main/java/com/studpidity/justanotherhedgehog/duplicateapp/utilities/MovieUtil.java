package com.studpidity.justanotherhedgehog.duplicateapp.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studpidity.justanotherhedgehog.duplicateapp.model.MovieItem;
import com.studpidity.justanotherhedgehog.duplicateapp.model.ReviewItem;
import com.studpidity.justanotherhedgehog.duplicateapp.model.TrailerItem;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MovieUtil {
    private static final String API_KEY_PARAM = "api_key";
    //your key here
    private static final String API_KEY_VALUE = "YOUR_API_KEY";
    private static final String LANGUAGE_PARAM = "language";
    private static final String LANGUAGE_VALUE = "en-US";
    private static final String PAGE_NUMBER_PARAM = "page";
    private static final String SCHEME_VALUE = "https";
    private static final String AUTHORITY_VALUE = "api.themoviedb.org";

    private MovieUtil(){
        throw new IllegalAccessError("Utility Class");
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm;
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public static String fetchJsonFromUrl(URL url) {
        StringBuilder result = new StringBuilder();
        HttpURLConnection urlConnection = null;
        try {

            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result.toString();
    }

    public static URL makeMovieUrl(String sortOrderValue, int pageNumberValue){
        Uri.Builder builder = new Uri.Builder();
        Uri uri =  builder.scheme(SCHEME_VALUE)
                .authority(AUTHORITY_VALUE)
                .appendPath("3")
                .appendPath("movie")
                .appendPath(sortOrderValue)
                .appendQueryParameter(API_KEY_PARAM, API_KEY_VALUE)
                .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE_VALUE)
                .appendQueryParameter("adult","false")
                .appendQueryParameter(PAGE_NUMBER_PARAM, String.valueOf(pageNumberValue)).build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static URL makeReviewsUrl(int movieId){
        Uri.Builder builder = new Uri.Builder();
        Uri uri =  builder.scheme(SCHEME_VALUE)
                .authority(AUTHORITY_VALUE)
                .appendPath("3")
                .appendPath("movie")
                .appendPath(String.valueOf(movieId))
                .appendPath("reviews")
                .appendQueryParameter(API_KEY_PARAM, API_KEY_VALUE)
                .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE_VALUE)
                .appendQueryParameter(PAGE_NUMBER_PARAM, String.valueOf(1)).build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static URL makeTrailersUrl(int movieId){
        Uri.Builder builder = new Uri.Builder();
        Uri uri =  builder.scheme(SCHEME_VALUE)
                .authority(AUTHORITY_VALUE)
                .appendPath("3")
                .appendPath("movie")
                .appendPath(String.valueOf(movieId))
                .appendPath("videos")
                .appendQueryParameter(API_KEY_PARAM, API_KEY_VALUE)
                .appendQueryParameter(LANGUAGE_PARAM, LANGUAGE_VALUE).build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Object parseJson(String jsonString, String className ) {
        TypeReference typeReference = null;
        if (className.equals(MovieItem.getClassName())) {
            typeReference = new TypeReference<ArrayList<MovieItem>>(){};
        } else if (className.equals(ReviewItem.getClassName())) {
            typeReference = new TypeReference<ArrayList<ReviewItem>>(){};
        } else if (className.equals(TrailerItem.getClassName())) {
            typeReference = new TypeReference<ArrayList<TrailerItem>>(){};
        }
        try {

            ObjectMapper mapper = new ObjectMapper();
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            if(typeReference == null) {
                return null;
            }
            return mapper.readValue(jsonArray.toString(), typeReference);
        } catch (Exception e) {
            return null;
        }
    }
}

