package com.studpidity.justanotherhedgehog.duplicateapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrailerItem implements Serializable {
    public static String baseTrailerUrl = "http://www.youtube.com/watch?v=";
    public static String getClassName() {
        return TrailerItem.class.getSimpleName();
    }
    private String id;
    private String key;
    private String name;
    private String site;
    private String size;



    public String getTrailerUrl() {
        return key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
