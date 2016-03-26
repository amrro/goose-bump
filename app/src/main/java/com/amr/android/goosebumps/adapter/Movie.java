package com.amr.android.goosebumps.adapter;

/**
 * Created by amro on 3/25/16.
 */
public class Movie
{

    private final String name;
    private final int ID;
    private final boolean adult;

    private final String posterURL;


    public Movie(String name, int ID, boolean adult, String posterURL)
    {
        this.name = name;
        this.ID = ID;
        this.adult = adult;
        this.posterURL = posterURL;
    }

    public String getName()
    {
        return name;
    }

    public int getID()
    {
        return ID;
    }

    public boolean isAdult()
    {
        return adult;
    }

    public String getPosterURL()
    {
        return posterURL;
    }



    @Override
    public String toString()
    {
        return new String(
                this.getName() + ", " +
                        this.getID() + ", " +
                        this.getPosterURL()
        );
    }

    public static final String API_BASE_URL = "https://api.themoviedb.org/3";
    public static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";

    public static final String API_KEY = "f5d2da75e7729eee412a43da5f542a9c";

    public static final String OVERVIEW =   "overview";
    public static final String ORIGINAL_TITLE = "original_title";
    public static final String MOVIE_ID = "id";
    public static String BACKDROP = "backdrop_path";
    public static final String MOVIE_ADULT = "adult";
    public static final String POSTER_PATH = "poster_path";
    public static final String[] POSTER_SIZES = new String[]
            {"w92",
            "w154",
            "w185",
            "w342",
            "w500",
            "w780",
            "original"
    };


    public static final String[] BACKDROP_SIZES = new String[]
            {
                    "w300",
                    "w780",
                    "w1280",
                    "original"
            };

}
