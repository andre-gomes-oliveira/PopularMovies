package popularmovies.udacity.com.br.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
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

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import popularmovies.udacity.com.br.popularmovies.adapters.MovieAdapter;
import popularmovies.udacity.com.br.popularmovies.adapters.ReviewAdapter;
import popularmovies.udacity.com.br.popularmovies.adapters.TrailerAdapter;
import popularmovies.udacity.com.br.popularmovies.model.Extra;
import popularmovies.udacity.com.br.popularmovies.model.Movie;
import popularmovies.udacity.com.br.popularmovies.model.Review;
import popularmovies.udacity.com.br.popularmovies.model.Trailer;
import popularmovies.udacity.com.br.popularmovies.utilities.JSONUtilities;
import popularmovies.udacity.com.br.popularmovies.utilities.NetworkUtilities;

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

    /* The Adapterd that will fetch the data and bind them to the views*/
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

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
                mTitleView.setText(mMovie.getMovieTitle());
                mSynopsisView.setText(mMovie.getMovieSynopsis());
                mReleaseView.setText(mMovie.getMovieReleaseDate());
                mRatingView.setText(String.valueOf(mMovie.getMovieRating()));

                /* Loading the movie poster, with a placeholder image to treat errors */
                String picassoURL = this.getString(R.string.base_picasso_url)
                        + mMovie.getMoviePosterPath();

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

                /* Performing the network requests that will fetch thee extra information*/
                makeMovieDetailsRequest(getString(R.string.request_trailers_url));
                makeMovieDetailsRequest(getString(R.string.request_reviews_url));
            }
        }
    }

    @Override
    public Loader<Extra[]> onCreateLoader(int id, final Bundle args)
    {
        return new AsyncTaskLoader<Extra[]>(this)
        {

            @Override
            protected void onStartLoading()
            {

                if (args == null) {
                    return;
                }

                // COMPLETED (7) Show the loading indicator
                /*
                 * When we initially begin loading in the background, we want to display the
                 * loading indicator to the user
                 */
                //mLoadingIndicator.setVisibility(View.VISIBLE);

                forceLoad();
            }

            @Override
            public Extra[] loadInBackground()
            {
                Integer id;
                String request, trailer, review;

                id = args.getInt(getString(R.string.bundle_id));
                trailer = args.getString(getString(R.string.bundle_trailers));
                review = args.getString(getString(R.string.bundle_reviews));

                if (id == 0)
                    return null;

                if(trailer != null)
                    request = "/" + id.toString() + trailer;
                else
                    request = "/" + id.toString() + review;

                try
                {
                    URL detailsUrl = NetworkUtilities.buildMoviesUrl(request);
                    String result = NetworkUtilities.getResponseFromHttpUrl(detailsUrl);

                    if(trailer != null)
                        return JSONUtilities.getTrailersDataFromJson(result);
                    else
                        return JSONUtilities.getReviewsDataFromJson(result);
                }
                catch (IOException | JSONException e)
                {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Extra[]> loader, Extra[] data)
    {
        if(data.length > 0)
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
        requestBundle.putInt(getString(R.string.bundle_id), mMovie.getMovieId());
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

        mTrailerAdapter = new TrailerAdapter(trailers.length);
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

        mReviewAdapter = new ReviewAdapter(reviews.length);
        mReviewAdapter.setReviews(reviews);
        mReviewRecyclerView.setAdapter(mReviewAdapter);
    }

    private void addFavorite(View view)
    {
        //TODO Implement the logic to add a movie to a list of favorites.
        Toast.makeText(this, getString(R.string.favorite_inserted_message), Toast.LENGTH_SHORT)
                .show();

        ImageButton favoriteButton = findViewById(R.id.ib_favorite);
        favoriteButton.setImageResource(R.drawable.ic_favorite_black_24px);
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
