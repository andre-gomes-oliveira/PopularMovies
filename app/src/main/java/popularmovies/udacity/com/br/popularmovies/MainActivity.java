package popularmovies.udacity.com.br.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;


import java.net.URL;
import java.util.Objects;

import popularmovies.udacity.com.br.popularmovies.utilities.JSONUtilities;
import popularmovies.udacity.com.br.popularmovies.utilities.NetworkUtilities;

public class MainActivity extends AppCompatActivity implements  MovieAdapter.movieClickListener, AdapterView.OnItemSelectedListener  {

    /* RecyclerView that will be used to circle through the movie posters */
    private RecyclerView mRecyclerView;

    /* TextView used to display error messages when the connection fails */
    private TextView mErrorMessageDisplay;

    /* ProgressBar used to inform the user that data is being fetched, if necessary */
    private ProgressBar mLoadingIndicator;

    /* The Adapter that will fetch the data and bind them to the views*/
    static final int num_mov_posters = 18;
    private MovieAdapter mMovieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Obtaining the references to the views from the XML.*/
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        /*Creating and adjusting the layout manager
          the movie posters will be displayed in 2 columns, like the mock-up.*/
        GridLayoutManager layoutManager
                = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);

        loadMoviesData(getString(R.string.sort_query_popularity));
    }

    @Override
    public void onMovieItemClick(Movie clickedMovie)
    {
        Context context = this;
        Class destinationClass = MovieDetailsActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);

        intentToStartDetailActivity.putExtra(getString(R.string.movie_details_intent), clickedMovie);
        startActivity(intentToStartDetailActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort, menu);

        MenuItem item = menu.findItem(R.id.sort_functions_spinner);
        Spinner mSpinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(this);
        return true;
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id)
    {
        String option = parent.getItemAtPosition(pos).toString();

        if (Objects.equals(option, getString(R.string.sort_option_rating)))
            loadMoviesData(getString(R.string.sort_query_rating));
        else
            loadMoviesData(getString(R.string.sort_query_popularity));
    }

    public void onNothingSelected(AdapterView<?> parent)
    {
    }

    /**
     * This method will make the View for the movie posters visible and
     * hide the error message.
     */
    private void showMoviePostersView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide
     * the movie posters View.
     */
    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    /**
     * This method will get the user's preferred location for weather, and then tell some
     * background method to get the weather data in the background.
     */
    private void loadMoviesData(String sort_by) {
        showMoviePostersView();

        new FetchMoviesDataTask().execute(sort_by);
    }

    private class FetchMoviesDataTask extends AsyncTask<String, Void, Movie[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movie[] doInBackground(String... params) {

            /* If there's no url, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            String sort_by = params[0];
            URL moviesRequestUrl = NetworkUtilities.buildMoviesUrl(sort_by);

            try
            {
                String jsonWeatherResponse = NetworkUtilities
                        .getResponseFromHttpUrl(moviesRequestUrl);

                return JSONUtilities
                        .getMovieDataFromJson(jsonWeatherResponse);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie[] popularMovies) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if ((popularMovies != null) && (popularMovies.length >= 0))
            {
                showMoviePostersView();
                mMovieAdapter.setMovies(popularMovies);
                mRecyclerView.setAdapter(mMovieAdapter);
            } else {
                showErrorMessage();
            }
        }
    }
}
