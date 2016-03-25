package com.amr.android.goosebumps;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amr.android.goosebumps.adapter.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by amro on 3/25/16.
 */
public class LaunchFragment extends Fragment
{

    private FetchMoviesTask mFetchMovies;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_launch, container, false);
        mFetchMovies = new FetchMoviesTask();
        mFetchMovies.execute();
        return rootView;
    }



    class FetchMoviesTask extends AsyncTask<Void, Void, ArrayList<Movie>>
    {
        final static String API_KEY = "f5d2da75e7729eee412a43da5f542a9c";
        private final String TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected ArrayList<Movie> doInBackground(Void... params)
        {
            HttpsURLConnection httpsURLConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;

            try
            {
                final String QUERY_KEY = "api_key";

                Uri uriBuilder = Uri.parse("https://api.themoviedb.org/3")
                        .buildUpon()
                        .appendPath("movie")
                        .appendPath("popular")
                        .appendQueryParameter(QUERY_KEY, API_KEY)
                        .build();


                URL url = new URL(uriBuilder.toString());

                // create request and open connection with TheMoviesDB:
                httpsURLConnection  = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setRequestMethod("GET");
                httpsURLConnection.connect();


                // Read input stream::
                //   >>>
                InputStream input = httpsURLConnection.getInputStream();
                if (input == null)
                    return null;

                reader = new BufferedReader(new InputStreamReader(input));

                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null)
                    buffer.append(line + "\n");

                if (buffer.length() == 0)
                {
                    Log.d(TAG, "    empty buffer!");
                    return null;
                }

                // finally getting jason string
                moviesJsonStr = buffer.toString();
                //  <<<
            }
            catch (MalformedURLException e)
            {
                Log.e(TAG, "Invalid URL" + e.getMessage());
            } catch (IOException e)
            {
                Log.e(TAG, "error! can't open connection" + e.getMessage());
            }
            finally
            {
                if (httpsURLConnection != null)
                    httpsURLConnection.disconnect();

                if(reader != null)
                    try
                    {
                        reader.close();
                    } catch (IOException e)
                    {
                        Log.e(TAG, "error closing BufferedReader! " + e.getMessage());
                    }
            }


            // >>> parsing
            try
            {
                return getMoviesFromJson(moviesJsonStr);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
            // <<<

            return null;
        }

        private ArrayList<Movie> getMoviesFromJson(String moviesJsonStr)
                throws JSONException
        {
            ArrayList<Movie> retList = new ArrayList<>();

            JSONObject page = new JSONObject(moviesJsonStr);
            JSONArray results = page.getJSONArray("results");

            for (int i = 0; i < results.length(); ++i)
            {
                JSONObject singleMovie = results.getJSONObject(i);
                retList.add(
                        new Movie(
                                singleMovie.getString(Movie.ORIGINAL_TITLE),
                                singleMovie.getInt(Movie.MOVIE_ID),
                                singleMovie.getBoolean(Movie.MOVIE_ADULT),
                                getPosterURL(singleMovie.getString(Movie.POSTER_PATH))
                                )
                );
            }

            return retList;
        }

        private String getPosterURL(String posterPath)
        {
            return  Uri.parse(Movie.IMAGE_BASE_URL).buildUpon()
                    .appendPath(Movie.POSTER_SIZES[1])
                    .appendEncodedPath(posterPath)
                    .build().toString();

        }
    }
}
