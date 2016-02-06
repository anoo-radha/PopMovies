package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private MovieDetailAdapter mMovieAdapter = null;

    public MainActivityFragment() {
    }

    /* Creates GridView for displaying the posters. Also when a poster is clicked,
       it opens the details about that movie */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gView = (GridView) rootView.findViewById(R.id.posters_grid);
        mMovieAdapter = new MovieDetailAdapter(getActivity(), new ArrayList<Movies>());
        gView.setAdapter(mMovieAdapter);
        gView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movies movieDetail = mMovieAdapter.getItem(position);
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("detail", movieDetail);
                startActivity(detailIntent);
            }
        });
        return rootView;
    }

    /* Get the list of movies according to sort order every time this activity starts */
    @Override
    public void onStart() {
        super.onStart();
        updateMovieList();
    }

    /* Gets the sort order from the Sharedpreference and checks if network connection is available before fetching the URL */
    private void updateMovieList() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = sharedPref.getString(getString(R.string.pref_sort_key), getString(R.string.default_sort));

        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new FetchMovieData().execute(sortBy);
        } else {
            Toast.makeText(getActivity(), R.string.network_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    /* Starts the background task for fetching the URL and parsing the JSON string
     * @param String for the selected sort-order is given as input for the doInBackground method
     * @param Void represents no input for onProgressUpdate method
     * @param List<Movies> the arraylist of movie details is got as result from doInBackground method
     *         and given as input for onPostExecute method*/
    private class FetchMovieData extends AsyncTask<String, Void, List<Movies>> {
        /**
         * Take the String representing the complete response in JSON Format and
         * pull out the data we need to construct the Strings needed for the layout/wireframes.
         *
         * @param moviesJsonResult the string in JSON format that is returned by the URL call
         * @return List<Movies>  the Array List of details for all the movies by parsing the JSON string
         */
        private List<Movies> getMovieDataFromJson(String moviesJsonResult)
                throws JSONException {
            // Names of the JSON objects that need to be extracted.
            final String JSON_RESULTS = "results";
            final String JSON_ID = "id";
            final String JSON_ORIGINAL_TITLE = "original_title";
            final String JSON_OVERVIEW = "overview";
            final String JSON_RELEASE_DATE = "release_date";
            final String JSON_POSTER_PATH = "poster_path";
            final String JSON_VOTE_AVERAGE = "vote_average";

            JSONObject movieJson = new JSONObject(moviesJsonResult);
            JSONArray movieArray = movieJson.getJSONArray(JSON_RESULTS);
            List<Movies> moviesList = new ArrayList<>(movieArray.length());
            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movieToAdd = movieArray.getJSONObject(i);
                int id = movieToAdd.getInt(JSON_ID);
                String originalTitle = movieToAdd.getString(JSON_ORIGINAL_TITLE);
                String synopsis = movieToAdd.getString(JSON_OVERVIEW);
                String releaseDate = movieToAdd.getString(JSON_RELEASE_DATE);
                String posterPath = movieToAdd.getString(JSON_POSTER_PATH);
                float userRating = (float) movieToAdd.getDouble(JSON_VOTE_AVERAGE);

                Movies movieDetail = new Movies(
                        id,
                        originalTitle,
                        synopsis,
                        releaseDate,
                        posterPath,
                        userRating);
                moviesList.add(movieDetail);
            }
            return moviesList;
        }

        @Override
        protected List<Movies> doInBackground(String... params) {

            // Will contain the raw JSON response as a string.
            String moviesJsonResult = null;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            final String MOVIES_BASE_URL = getString(R.string.base_url);
            final String QUERY_PARAM = getString(R.string.query_param);
            final String APPID_PARAM = getString(R.string.api_key);
            try {
                Uri uri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(APPID_PARAM, getString(R.string.api_key_value))
                        .build();

                URL url = new URL(uri.toString());
                Log.i(LOG_TAG, "Built URI " + uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                // Starts the query
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                StringBuilder buffer = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging easier
                    buffer.append(line);
                    buffer.append("\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                moviesJsonResult = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(moviesJsonResult);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        /* This is called after the background action is completed to add the the arraylist of
         * movie detials to the movieAdapter. Thus, the gridview can be updated and the
         * corresponding details can be shown when the poster is clicked
         */
        @Override
        protected void onPostExecute(List<Movies> movieDataList) {
            super.onPostExecute(movieDataList);
            if (movieDataList != null) {
                mMovieAdapter.clear();
                for (Movies movie : movieDataList) {
                    mMovieAdapter.add(movie);
                }
            }
        }

    }
}