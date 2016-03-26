package com.amr.android.goosebumps.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amr.android.goosebumps.MovieDetailActivity;
import com.amr.android.goosebumps.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by amro on 3/25/16.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieHolder>
{
    private ArrayList<Movie> mMoviesSet;
    private Context mContext;

    public MoviesAdapter(Context context, ArrayList<Movie> movies)
    {
        mMoviesSet = movies;
        mContext = context;
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_poster, parent, false);
        return new MovieHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position)
    {
        Picasso.with(mContext).load(mMoviesSet.get(position).getPosterURL())
                .into(holder.getPosterview());
    }

    @Override
    public int getItemCount()
    {
        return mMoviesSet.size();
    }


    public void clear()
    {
        mMoviesSet.clear();
        notifyDataSetChanged();
    }

    public void addAll(Collection<? extends Movie> movies)
    {
        mMoviesSet.addAll(movies);
    }



    public class MovieHolder extends RecyclerView.ViewHolder
    {
        private final String TAG = MovieHolder.class.getSimpleName();
        private ImageView posterView;
        public MovieHolder(View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent detailsIntent = new Intent(v.getContext(), MovieDetailActivity.class);
                    Log.i(TAG, mMoviesSet.get(getPosition()).getID() + "");
                    detailsIntent.putExtra(
                            Movie.MOVIE_ID,
                            mMoviesSet.get(getPosition()).getID()
                    );
                    mContext.startActivity(detailsIntent);
                }
            });
            posterView = (ImageView) itemView.findViewById(R.id.poster_view);
        }

        public ImageView getPosterview()
        {
            return posterView;
        }
    }
}
