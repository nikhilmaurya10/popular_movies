package com.studpidity.justanotherhedgehog.duplicateapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieItem implements Serializable {
    public static String getClassName(){
        return MovieItem.class.getSimpleName();
    }
    private String poster_path;
    private String title;
    private String release_date;
    private String vote_average;
    private String overview;
    private int id;
    private String backdrop_path;
    private final String baseImageUrlPath = "http://image.tmdb.org/t/p/w500";
    private final String baseBackDropImageUrlPath = "http://image.tmdb.org/t/p/original";
    public final String OUTOF = "/10";

    public String getBaseBackDropImageUrlPath() {
        return baseBackDropImageUrlPath;
    }

    public String getBackdrop_path() {

        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path =  poster_path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getBaseImageUrlPath() {
        return baseImageUrlPath;
    }

    @Override
    public String toString() {
        return "MovieItem{" +
                "poster_path='" + poster_path + '\'' +
                ", title='" + title + '\'' +
                ", release_date='" + release_date + '\'' +
                ", vote_average='" + vote_average + '\'' +
                ", overview='" + overview + '\'' +
                ", id=" + id +
                '}';
    }
}
