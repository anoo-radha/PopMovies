package com.example.android.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/* for loading the gridview and for sending details to another intent */
public class MovieDetailAdapter extends ArrayAdapter<Movies> {

    /**
     * @param context The current context. Used to inflate the layout file
     * @param movies  A List of Movies objects to display in a list
     */
    public MovieDetailAdapter(Activity context, List<Movies> movies) {
        // The adapter is not using second argument, so it can be any value. here, its 0
        super(context, 0, movies);
    }

    /**
     * Provides a view for Poster GridView
     * @param position    The AdapterView position that is requesting a view
     * @param convertView The recycled view to populate
     * @param parent      The parent ViewGroup that is used for inflation
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.poster_view, parent, false);
        }
        Movies movie = getItem(position);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.poster_imgview);
        //Calls the Picasso API for loading the imageView from the given path
        Picasso.with(getContext()).load(movie.getActualPosterPath()).into(imageView);
        return convertView;
    }

}