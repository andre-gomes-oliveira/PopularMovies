package popularmovies.udacity.com.br.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import popularmovies.udacity.com.br.popularmovies.adapters.MovieAdapter;
import popularmovies.udacity.com.br.popularmovies.adapters.ReviewAdapter;
import popularmovies.udacity.com.br.popularmovies.adapters.TrailerAdapter;
import popularmovies.udacity.com.br.popularmovies.data.MovieContract.MovieEntry;
import popularmovies.udacity.com.br.popularmovies.data.MovieDetailsLoader;
import popularmovies.udacity.com.br.popularmovies.model.Extra;
import popularmovies.udacity.com.br.popularmovies.model.Movie;
import popularmovies.udacity.com.br.popularmovies.model.Review;
import popularmovies.udacity.com.br.popularmovies.model.Trailer;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

public class MovieDetailsActivity
        extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Extra[]>
{
    private static final String TAG = MovieAdapter.class.getSimpleName();

    /* Unique identifiers for both the loaders used by this activity*/
    private static final int MOVIE_TRAILERS_LOADER = 11;
    private static final int MOVIE_REVIEWS_LOADER = 22;

    /* The movie that will have it's details displayed */
    private Movie mMovie;

    /* RecyclerViews that will display the trailers and the reviews */
    private RecyclerView mTrailerRecyclerView, mReviewRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        /*Obtaining the references to the views from the XML.*/
        TextView mTitleView = findViewById(R.id.tv_details_title);
        ImageView mPosterView = findViewById(R.id.iv_details_poster);
        TextView mSynopsisView = findViewById(R.id.tv_details_synopsis);
        TextView mReleaseView = findViewById(R.id.tv_details_date);
        TextView mRatingView = findViewById(R.id.tv_details_rating);

        mTrailerRecyclerView = findViewById(R.id.rv_trailers);
        mReviewRecyclerView = findViewById(R.id.rv_reviews);

        Intent starterIntent = getIntent();

        if (starterIntent != null)
        {
            /* Obtaining a reference to the movie that will have it's details displayed */
            String movieIntent = getString(R.string.movie_details_intent);

            if (starterIntent.hasExtra(movieIntent))
            {
                mMovie = starterIntent.getParcelableExtra(movieIntent);

                /* Displaying the details of the movie in the UI */
                mTitleView.setText(mMovie.getTitle());
                mSynopsisView.setText(mMovie.getSynopsis());
                mReleaseView.setText(mMovie.getReleaseDate());
                mRatingView.setText(String.valueOf(mMovie.getRating()));

                /* Loading the movie poster, with a placeholder image to treat errors */
                String picassoURL = this.getString(R.string.base_picasso_url)
                        + mMovie.getPosterPath();

                Log.v(TAG, "Picasso URL " + picassoURL);
                Picasso.with(this)
                        .load(picassoURL)
                        .placeholder(R.drawable.ic_error_white_24px)
                        .error(R.drawable.ic_photo_size_select_actual_white_24px)
                        .into(mPosterView);

                /* Loading extra information, such as the trailers and the reviews*/
                getSupportLoaderManager().initLoader(MOVIE_TRAILERS_LOADER, null, this);
                getSupportLoaderManager().initLoader(MOVIE_REVIEWS_LOADER, null, this);

                /* Preparing the RecyclerViews that will hold and display this extra information */
                LinearLayoutManager trailerManager =
                        new LinearLayoutManager(this, VERTICAL, false);

                LinearLayoutManager reviewManager =
                        new LinearLayoutManager(this, VERTICAL, false);

                mTrailerRecyclerView.setLayoutManager(trailerManager);
                mReviewRecyclerView.setLayoutManager(reviewManager);

                /* Checking if the movie is in the favorites list */
                ContentResolver resolver = getContentResolver();
                Uri queryURI = MovieEntry.CONTENT_URI.buildUpon()
                        .appendPath(String.valueOf(mMovie.getId())).build();

                Cursor cursor = resolver.query(queryURI,
                        null,
                        null,
                        null,
                        null);

                if(cursor != null)
                {
                    if (cursor.getCount() > 0)
                    {
                        ImageButton favoriteButton = findViewById(R.id.ib_favorite);
                        favoriteButton.setImageResource(R.drawable.ic_favorite_black_24px);
                    }
                    else
                    {
                        ImageButton favoriteButton = findViewById(R.id.ib_favorite);
                        favoriteButton.setImageResource(R.drawable.ic_favorite_white_24px);
                    }
                    cursor.close();
                }

                /* Performing the network requests that will fetch thee extra information*/
                makeMovieDetailsRequest(getString(R.string.request_trailers_url));
                makeMovieDetailsRequest(getString(R.string.request_reviews_url));
            }
        }
    }

    @Override
    public Loader<Extra[]> onCreateLoader(int id, final Bundle args)
    {
        return new MovieDetailsLoader(this, args);
    }

    @Override
    public void onLoadFinished(Loader<Extra[]> loader, Extra[] data)
    {
        if((data != null) && (data.length > 0))
        {
            if(data[0] instanceof Trailer)
                displayTrailers(data);
            else
                displayReviews(data);
        }
    }

    /* This function had to be overridden, but it will not be used by this activity */
    @Override
    public void onLoaderReset(Loader<Extra[]> loader)
    {
    }

    private void makeMovieDetailsRequest(@NonNull String endpoint)
    {
        LoaderManager loaderManager = getSupportLoaderManager();

        Bundle requestBundle = new Bundle();
        requestBundle.putInt(getString(R.string.bundle_id), mMovie.getId());
        if(endpoint.equals(getString(R.string.request_trailers_url)))
        {
            requestBundle.putString(getString(R.string.bundle_trailers), endpoint);
            Loader<String> trailersLoader = loaderManager.getLoader(MOVIE_TRAILERS_LOADER);

            if (trailersLoader == null)
                loaderManager.initLoader(MOVIE_TRAILERS_LOADER, requestBundle, this);
            else
                loaderManager.restartLoader(MOVIE_TRAILERS_LOADER, requestBundle, this);
        }
        else
        {
            requestBundle.putString(getString(R.string.bundle_reviews), endpoint);
            Loader<String> reviewsLoader = loaderManager.getLoader(MOVIE_REVIEWS_LOADER);

            if (reviewsLoader == null)
                loaderManager.initLoader(MOVIE_REVIEWS_LOADER, requestBundle, this);
            else
                loaderManager.restartLoader(MOVIE_REVIEWS_LOADER, requestBundle, this);
        }
    }

    private void displayTrailers(Extra[] data)
    {
        Trailer[] trailers = new Trailer[data.length];
        int index = 0;

        for (Extra extra : data)
        {
            if(extra instanceof Trailer )
            {
                Trailer trailer = (Trailer) extra;
                trailers[index] = trailer;
                ++index;
            }
        }

        TrailerAdapter mTrailerAdapter = new TrailerAdapter(trailers.length);
        mTrailerAdapter.setTrailers(trailers);
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);
    }

    private void displayReviews(Extra[] data)
    {
        Review[] reviews = new Review[data.length];
        int index = 0;

        for (Extra extra : data)
        {
            if(extra instanceof Review )
            {
                Review review = (Review) extra;
                reviews[index] = review;
                ++index;
            }
        }

        ReviewAdapter mReviewAdapter = new ReviewAdapter(reviews.length);
        mReviewAdapter.setReviews(reviews);
        mReviewRecyclerView.setAdapter(mReviewAdapter);
    }

    public void addFavorite(View view)
    {
        ImageButton favoriteButton = findViewById(R.id.ib_favorite);

        /* Acquiring the required components to execute with database operations */
        ContentResolver resolver = getContentResolver();
        ContentValues values = new ContentValues();

        /* Populating the values with the data from the movie */
        values.put(MovieEntry._ID, mMovie.getId());
        values.put(MovieEntry.COLUMN_TITLE, mMovie.getTitle());
        values.put(MovieEntry.COLUMN_POSTER_PATH, mMovie.getPosterPath());
        values.put(MovieEntry.COLUMN_SYNOPSIS, mMovie.getSynopsis());
        values.put(MovieEntry.COLUMN_RATING, mMovie.getRating());
        values.put(MovieEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate());

        /* Checking weather the movie is in the database or not
        * If it is not, it will be inserted (the user added it to the list of favorites)
        * If it is, it will be deleted (the user removed it from the list of favorites)*/
        Uri queryURI = MovieEntry.CONTENT_URI.buildUpon()
                .appendPath(String.valueOf(mMovie.getId())).build();

        Cursor cursor = resolver.query(queryURI,
                null,
                null,
                null,
                null);

        if(cursor != null)
        {
            if (cursor.getCount() == 0)
            {
            /* Inserting a new movie in the database */
                Uri uri = resolver.insert(MovieEntry.CONTENT_URI, values);

                if(uri != null)
                {
                    favoriteButton.setImageResource(R.drawable.ic_favorite_black_24px);
                    Toast.makeText(this, getString(R.string.favorite_inserted_message), Toast.LENGTH_SHORT)
                            .show();
                }
            }
            else
            {
            /* Inserting a nre movie in the database */
                int deletedMovies = resolver.delete(queryURI, null, null);

                if(deletedMovies > 0)
                {
                    favoriteButton.setImageResource(R.drawable.ic_favorite_white_24px);
                    Toast.makeText(this, getString(R.string.favorite_removed_message), Toast.LENGTH_SHORT)
                            .show();
                }
            }

            cursor.close();
        }
    }

    public void shareMovieTrailer(View view)
    {
        String id = (String) view.getTag();

        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(getString(R.string.youtube_url) + id)
                .getIntent();

        if (shareIntent.resolveActivity(getPackageManager()) != null)
            startActivity(shareIntent);
    }

	/** This function attempts to open the selected trailer using YouTube.
	*   If that fails, it tries to open it in a browser.
	*   Since I wasn't sure of how to call YouTube via explicit intent, I used the following source as an example:
	*  https://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
	*/
    public void playMovieTrailer(View view)
    {
        String id = (String) view.getTag();

        Intent appIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.youtube_intent) + id));

        try
        {
            startActivity(appIntent);
        }
        catch (ActivityNotFoundException ex)
        {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.youtube_url) + id));

            startActivity(webIntent);
        }
    }
}
