package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    /* displays the details of the selected movie poster with the details got from the parcelable intent*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        if ((intent != null) && (intent.hasExtra("detail"))) {
            Movies moviesInfo = intent.getParcelableExtra("detail");
            ((TextView) rootView.findViewById(R.id.title_view))
                    .setText(moviesInfo.mTitle);
            String overview = getString(R.string.default_synopsis);
            if (moviesInfo.mSynopsis != null) {
                overview = moviesInfo.mSynopsis;
            }
            ((TextView) rootView.findViewById(R.id.synopsis_view))
                    .setText(overview);
            TextView yearView = (TextView) rootView.findViewById(R.id.releasedt_view);
            String[] date = (moviesInfo.mReleaseDate).split(getString(R.string.delimiter));
            yearView.setText(date[0]);
            ImageView imageView = (ImageView) rootView.findViewById(R.id.poster_imgview);
            Picasso.with(getContext()).load(moviesInfo.getActualPosterPath()).into(imageView);
            ((TextView) rootView.findViewById(R.id.rating_view))
                    .setText(String.format(getResources().getString(R.string.default_rating), moviesInfo.mUserRating));
        }
        return rootView;
    }
}
