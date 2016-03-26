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
import android.widget.ImageView;
import android.widget.TextView;

import com.amr.android.goosebumps.adapter.Movie;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by amro on 3/26/16.
 */
public class MovieDetailsFragment extends Fragment
{
    private final String TAG = MovieDetailsFragment.class.getSimpleName();

    FetchMovieInfoTask mMovieInfoTask;

    private ImageView mBackDrop;
    private ImageView mMoviePoster;
    private TextView mMovieTitle;
    private TextView mMovieInfo;
    private TextView mMovieOverview;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_moviedetails, container, false);
        mBackDrop = (ImageView) rootView.findViewById(R.id.backdrop_view);
        mMoviePoster = (ImageView) rootView.findViewById(R.id.poster_view);
        mMovieTitle = (TextView) rootView.findViewById(R.id.movie_title_textview);
        mMovieInfo = (TextView) rootView.findViewById(R.id.movie_info_textview);
        mMovieOverview = (TextView) rootView.findViewById(R.id.movie_overview_textview);

        return rootView;
    }


    @Override
    public void onStart()
    {
        super.onStart();
        mMovieInfoTask = new FetchMovieInfoTask();
        mMovieInfoTask.execute();
    }




    public class FetchMovieInfoTask extends AsyncTask<Void, Void, String>
    {
        private final String TASK_TAG = FetchMovieInfoTask.class.getSimpleName();
        @Override
        protected String doInBackground(Void... params)
        {
            HttpsURLConnection httpsURLConnection = null;
            String detailsJsonStr = null;
            BufferedReader reader = null;

            int id = getArguments().getInt(Movie.MOVIE_ID);


            try
            {
                Uri uriBuilder = Uri.parse(Movie.API_BASE_URL).buildUpon()
                        .appendPath("movie")
                        .appendPath(id + "")
                        .appendQueryParameter("api_key", Movie.API_KEY)
                        .build();


                Log.i(TASK_TAG, uriBuilder.toString());
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
                detailsJsonStr = buffer.toString();
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (ProtocolException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (httpsURLConnection != null)
                    httpsURLConnection.disconnect();

                if (reader != null)
                    try
                    {
                        reader.close();
                    }
                    catch (IOException e)
                    {
                        Log.e(TAG, "error closing BufferedReader! " + e.getMessage());
                    }
            }


            return detailsJsonStr;
        }


        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            try
            {
                JSONObject movieObject = new JSONObject(s);
                mMovieTitle.setText(movieObject.getString(Movie.ORIGINAL_TITLE));
                Picasso.with(getContext()).load(Movie.IMAGE_BASE_URL + Movie.BACKDROP_SIZES[0]
                        + movieObject.getString(Movie.BACKDROP))
                        .into(mBackDrop);


                Picasso.with(getContext()).load("https://image.tmdb.org/t/p/w185"
                        + movieObject.getString(Movie.POSTER_PATH))
                        .into(mMoviePoster);

                mMovieOverview.setText(movieObject.getString("overview"));

                String movieInfo = movieObject.getString("release_date").substring(0, 4)
                        + " | " + movieObject.getString("runtime") + " mins" +
                        " | " + movieObject.getInt("vote_average") + "/10";

                mMovieInfo.setText(movieInfo);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }
}
