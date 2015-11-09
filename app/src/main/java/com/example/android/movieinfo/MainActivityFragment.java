package com.example.android.movieinfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private SharedPreferences prefs;
    String sortOrder;

    private ImageAdapter movieAdapter;
    List<MovieParse> movies = new ArrayList<MovieParse>();

    public MainActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sortOrder = prefs.getString(getString(R.string.sortBy_key),
                getString(R.string.sortBy_default_value));

        if(savedInstanceState != null){
            ArrayList<MovieParse> storedMovies = new ArrayList<MovieParse>();
            storedMovies = savedInstanceState.getParcelableArrayList("stored_movies");
            movies.clear();
            movies.addAll(storedMovies);
        }

        FetchMovieData MovieData = new FetchMovieData();
        MovieData.execute(sortOrder);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        movieAdapter = new ImageAdapter(getActivity(), R.layout.image_ids_array, R.id.Image_Ids_Array, new ArrayList<String>());


        GridView Grid_ImageView = (GridView) rootView.findViewById(R.id.gridView_id);
        Grid_ImageView.setAdapter(movieAdapter);

        Grid_ImageView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieParse movieInfo = movies.get(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, movieInfo);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        // get sort order to see if it has recently changed
        String prefSortOrder = prefs.getString(getString(R.string.sortBy_key),
                getString(R.string.sortBy_default_value));

        FetchMovieData MovieData = new FetchMovieData();
        MovieData.execute(prefSortOrder);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            String prefSortOrder = prefs.getString(getString(R.string.sortBy_key),
                    getString(R.string.sortBy_default_value));

            FetchMovieData MovieData = new FetchMovieData();
            MovieData.execute(prefSortOrder);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class FetchMovieData extends AsyncTask<String, Void, List<MovieParse>> {

        private final String LOG_TAG = FetchMovieData.class.getSimpleName();
        private final String API_KEY = "your_api_key";
        private final String MOVIE_POSTER_BASE = "http://image.tmdb.org/t/p/";
        private final String MOVIE_POSTER_SIZE = "w185";

        @Override
        protected List<MovieParse> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //URL url = new URL("http://api.themoviedb.org/3/discover/movie?api_key=1b724d42398a0285a8846e297ee0440f");

                // Create the request to OpenWeatherMap, and open the connection
                final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_BY = "sort_by";
                final String KEY = "api_key";
                String sortBy = params[0];

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY, sortBy)
                        .appendQueryParameter(KEY, API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());


                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
                //calling method to get data from JSON string
                try {
                    //forecastJsonStr_1 = getweatherDataFromJson(forecastJsonStr, 7);
                    return getMoviesDataFromJson(moviesJsonStr);
                    //Log.v(LOG_TAG,""+forecastJsonStr_1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.v(LOG_TAG, "JasonString" + moviesJsonStr);


            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
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

            return null;
        }


        private String getYear(String date) {
            final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            final Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(df.parse(date));
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

            return Integer.toString(cal.get(Calendar.YEAR));
        }

        private List<MovieParse> getMoviesDataFromJson(String moviesJsonStr) throws JSONException {

            // Items to extract
            final String ARRAY_OF_MOVIES = "results";
            final String ORIGINAL_TITLE = "original_title";
            final String POSTER_PATH = "poster_path";
            final String OVERVIEW = "overview";
            final String VOTE_AVERAGE = "vote_average";
            final String RELEASE_DATE = "release_date";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(ARRAY_OF_MOVIES);
            int moviesLength = moviesArray.length();
            //List<MovieParse> movies = new ArrayList<MovieParse>();
            //String[] PosterArray = new String[100];

            for (int i = 0; i < moviesLength; ++i) {

                // for each movie in the JSON object create a new
                // movie object with all the required data
                JSONObject movie = moviesArray.getJSONObject(i);
                String title = movie.getString(ORIGINAL_TITLE);
                //PosterArray[i] = MOVIE_POSTER_BASE + MOVIE_POSTER_SIZE + movie.getString(POSTER_PATH);
                String poster = MOVIE_POSTER_BASE + MOVIE_POSTER_SIZE + movie.getString(POSTER_PATH);
                Log.v(LOG_TAG, "poster Array:" + poster);
                String overview = movie.getString(OVERVIEW);
                String voteAverage = movie.getString(VOTE_AVERAGE);
                String releaseDate = getYear(movie.getString(RELEASE_DATE));

                movies.add(new MovieParse(title, poster, overview, voteAverage, releaseDate));

            }

            return movies;


        }

        @Override
        protected void onPostExecute(List<MovieParse> results) {
            //super.onPostExecute(movieParses);
            if (results != null) {

                 movieAdapter.clear();
                for(MovieParse movie : results){
                    movieAdapter.add(movie.getPoster());
                }

            }
        }
    }

}
