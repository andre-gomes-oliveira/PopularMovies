package popularmovies.udacity.com.br.popularmovies;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Parcelable;
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
import android.widget.Toast;


import java.net.URL;
import java.util.Objects;

import popularmovies.udacity.com.br.popularmovies.adapters.MovieAdapter;
import popularmovies.udacity.com.br.popularmovies.data.MovieContract;
import popularmovies.udacity.com.br.popularmovies.model.Movie;
import popularmovies.udacity.com.br.popularmovies.utilities.JSONUtilities;
import popularmovies.udacity.com.br.popularmovies.utilities.NetworkUtilities;

public class MainActivity
        extends AppCompatActivity
        implements  MovieAdapter.movieClickListener,
        AdapterView.OnItemSelectedListener
{
    /* RecyclerView that will be used to circle through the movie posters */
    private RecyclerView mRecyclerView;

    /* Layout manager used by the RecyclerView*/
    private GridLayoutManager mLayoutManager;

    /* TextView used to display error messages when the connection fails */
    private TextView mErrorMessageDisplay;

    /* ProgressBar used to inform the user that data is being fetched, if necessary */
    private ProgressBar mLoadingIndicator;

    /* The spinner that contains the sorting options */
    private Spinner mSpinner;
    private int mPosition;

    /* The Adapter that will fetch the data and bind them to the views*/
    public static final int num_mov_posters = 18;
    private MovieAdapter mMovieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Obtaining the references to the views from the XML.*/
        mRecyclerView = findViewById(R.id.rv_movies);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        /*Creating and adjusting the layout manager
          the movie posters will be displayed in 2 columns, like the mock-up.*/
        mLayoutManager = new GridLayoutManager(this, 2,
                LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new  MovieAdapter(this);

        if(savedInstanceState != null)
        {
            Parcelable recyclerLayoutState = savedInstanceState.getParcelable
                    (getString(R.string.bundle_recycler_position));

            mLayoutManager.onRestoreInstanceState(recyclerLayoutState);

        }
        else
            loadMoviesData(getString(R.string.sort_option_popularity_value));
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putParcelable(getString(R.string.bundle_recycler_position),
                mLayoutManager.onSaveInstanceState());

        outState.putInt(getString(R.string.bundle_spinner_position),
                mSpinner.getSelectedItemPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        mPosition = savedInstanceState.getInt(
                getString(R.string.bundle_spinner_position));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort, menu);

        MenuItem item = menu.findItem(R.id.sort_functions_spinner);
        mSpinner = (Spinner) item.getActionView();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options_labels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(this);
        mSpinner.setSelection(mPosition);
        return true;
    }

    @Override
    public void onMovieItemClick(Movie clickedMovie)
    {
        if (clickedMovie != null)
        {
            Context context = this;
            Class destinationClass = MovieDetailsActivity.class;
            Intent intentToStartDetailActivity = new Intent(context, destinationClass);

            intentToStartDetailActivity.putExtra(getString(R.string.movie_details_intent), clickedMovie);
            startActivity(intentToStartDetailActivity);
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id)
    {
        String option = parent.getItemAtPosition(pos).toString();

        if (Objects.equals(option, getString(R.string.sort_option_rating_label)))
            loadMoviesData(getString(R.string.sort_option_rating_value));
        else if(Objects.equals(option, getString(R.string.sort_option_popularity_label)))
            loadMoviesData(getString(R.string.sort_option_popularity_value));
        else if(Objects.equals(option, getString(R.string.sort_option_favorites_label)))
            loadMoviesData(getString(R.string.sort_option_favorites_value));
        else
            Toast.makeText(this,
                    getString(R.string.error_option_message),
                    Toast.LENGTH_LONG).show();
    }

    public void onNothingSelected(AdapterView<?> parent)
    {
    }

    /**
     * This method will make the View for the movie posters visible and
     * hide the error message.
     */
    private void showMoviePostersView()
    {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide
     * the movie posters View.
     */
    private void showErrorMessage()
    {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    /**
     * This method will send the request for the movie data based on the proper endpoint,
     * depending on what the user selected.
     */
    private void loadMoviesData(String endPoint)
    {
        if(endPoint.equals(getString(R.string.sort_option_favorites_value)))
            loadFavorites();
        else
            new FetchMoviesDataTask().execute(endPoint);
    }

    private void loadFavorites()
    {
        /* Fetching the list of favorite movies from the database*/
        mLoadingIndicator.setVisibility(View.VISIBLE);

        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        if (cursor != null)
        {
            if(cursor.getCount() > 0)
            {
                cursor.moveToFirst();
                Movie[] movies = new Movie[cursor.getCount()];
                int index = 0;

                while (cursor.moveToNext())
                {
                    int id = cursor.getInt(MovieContract.MovieEntry.INDEX_ID);
                    String title = cursor.getString(MovieContract.MovieEntry.INDEX_TITLE);
                    String posterPath = cursor.getString(MovieContract.MovieEntry.INDEX_POSTER_PATH);
                    String synopsis = cursor.getString(MovieContract.MovieEntry.INDEX_SYNOPSIS);
                    int rating = cursor.getInt(MovieContract.MovieEntry.INDEX_RATING);
                    String releaseDate = cursor.getString(MovieContract.MovieEntry.INDEX_RELEASE_DATE);

                    movies[index] = new Movie(id, title, posterPath, synopsis, rating, releaseDate);

                    ++index;
                }

                showMoviePostersView();
                mMovieAdapter.setMovies(movies);
                mRecyclerView.setAdapter(mMovieAdapter);
            }
            else
                showErrorMessage();

            cursor.close();
        }
        else
            showErrorMessage();

        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    private class FetchMoviesDataTask extends AsyncTask<String, Void, Movie[]>
    {
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

            String endPoint = params[0];
            URL moviesRequestUrl = NetworkUtilities.buildMoviesUrl(endPoint);

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
            }
            else
                showErrorMessage();
        }
    }
}
