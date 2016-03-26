package com.amr.android.goosebumps;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amr.android.goosebumps.adapter.Movie;
import com.amr.android.goosebumps.adapter.MoviesAdapter;

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
    private final int SPAN_COUNT = 2;

    private FetchMoviesTask mFetchMovies;
    private RecyclerView  mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MoviesAdapter mAdapter;

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
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(getContext(), SPAN_COUNT);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mFetchMovies = new FetchMoviesTask();
        mAdapter = new MoviesAdapter(getContext(), new ArrayList<Movie>());
        mRecyclerView.setAdapter(mAdapter);

        mFetchMovies.execute("popular");
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_most_popular)
        {
            String title = item.getTitle().toString();

            if (title == getString(R.string.most_popular))
            {
                new FetchMoviesTask().execute("popular");
                Toast.makeText(getContext(), title, Toast.LENGTH_SHORT).show();
                item.setTitle(R.string.top_rated);
            }


            if (title == getString(R.string.top_rated))
            {
                new FetchMoviesTask().execute("top_rated");
                Toast.makeText(getContext(), title, Toast.LENGTH_SHORT).show();
                item.setTitle(R.string.most_popular);
            }

            Toast.makeText(
                    getContext(),
                    "Most Populsr",
                    Toast.LENGTH_SHORT
            ).show();
        }



        return super.onOptionsItemSelected(item);
    }

    class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>>
    {

        private final String TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected ArrayList<Movie> doInBackground(String... params)
        {
            HttpsURLConnection httpsURLConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;

            try
            {
                final String QUERY_KEY = "api_key";

                Uri uriBuilder = Uri.parse(Movie.API_BASE_URL)
                        .buildUpon()
                        .appendPath("movie")
                        .appendPath(params[0])
                        .appendQueryParameter(QUERY_KEY, Movie.API_KEY)
                        .build();


                URL url = new URL(uriBuilder.toString());

                // create request and open connection with TheMoviesDB:
                httpsURLConnection = (HttpsURLConnection) url.openConnection();
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
            } catch (MalformedURLException e)
            {
                Log.e(TAG, "Invalid URL" + e.getMessage());
            } catch (IOException e)
            {
                Log.e(TAG, "error! can't open connection" + e.getMessage());
            } finally
            {
                if (httpsURLConnection != null)
                    httpsURLConnection.disconnect();

                if (reader != null)
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
            return Uri.parse(Movie.IMAGE_BASE_URL).buildUpon()
                    .appendPath(Movie.POSTER_SIZES[3])
                    .appendEncodedPath(posterPath)
                    .build().toString();

        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies)
        {
            if (movies != null)
                mAdapter.clear();
            mAdapter.addAll(movies);
        }
    }
}
