package com.perozzo.tmdbexample.classes;

import java.io.Serializable;

/**
 * Created by Perozzo on 15/09/2017.
 * Object Movie
 */

public class Movie implements Serializable{
    private String title;
    private String year;
    private String poster_path;
    private String backdrop_path;
    private int vote_count;
    private double vote_average;
    private String overview;

    public Movie(String title, String year, String poster_path, String backdrop_path, int vote_count, double vote_average, String overview) {
        this.title = title;
        this.year = year;
        this.poster_path = poster_path;
        this.backdrop_path = backdrop_path;
        this.vote_count = vote_count;
        this.vote_average = vote_average;
        this.overview = overview;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public int getVote_count() {
        return vote_count;
    }

    public double getVote_average() {
        return vote_average;
    }

    public String getOverview() {
        return overview;
    }
}
