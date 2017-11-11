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

import popularmovies.udacity.com.br.popularmovies.model.Extra;
import popularmovies.udacity.com.br.popularmovies.model.Movie;
import popularmovies.udacity.com.br.popularmovies.utilities.JSONUtilities;
import popularmovies.udacity.com.br.popularmovies.utilities.NetworkUtilities;

public class MovieDetailsActivity
        extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Extra[]>
{
    private static final int MOVIE_REQUEST_LOADER = 11;
    private static final String TAG = MovieAdapter.class.getSimpleName();

    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        TextView mTitleView = findViewById(R.id.tv_details_title);
        ImageView mPosterView = findViewById(R.id.iv_details_poster);
        TextView mSynopsisView = findViewById(R.id.tv_details_synopsis);
        TextView mReleaseView = findViewById(R.id.tv_details_date);
        TextView mRatingView = findViewById(R.id.tv_details_rating);

        Intent starterIntent = getIntent();

        if (starterIntent != null)
        {
            String movieIntent = getString(R.string.movie_details_intent);
            getSupportLoaderManager().initLoader(MOVIE_REQUEST_LOADER, null, this);

            if (starterIntent.hasExtra(movieIntent))
            {
                mMovie = starterIntent.getParcelableExtra(movieIntent);

                mTitleView.setText(mMovie.getMovieTitle());
                mSynopsisView.setText(mMovie.getMovieSynopsis());
                mReleaseView.setText(mMovie.getMovieReleaseDate());
                mRatingView.setText(String.valueOf(mMovie.getMovieRating()));

                String picassoURL = this.getString(R.string.base_picasso_url)
                        + mMovie.getMoviePosterPath();

                Log.v(TAG, "Picasso URL " + picassoURL);
                Picasso.with(this)
                        .load(picassoURL)
                        .placeholder(R.drawable.ic_error_white_24px)
                        .error(R.drawable.ic_photo_size_select_actual_white_24px)
                        .into(mPosterView);

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
                String request, id, trailer, review;

                id = args.getString(getString(R.string.bundle_id));
                trailer = args.getString(getString(R.string.bundle_trailers));
                review = args.getString(getString(R.string.bundle_reviews));

                if (id == null)
                    return null;

                if(trailer != null)
                    request = id + trailer;
                else
                    request = id + review;

                try
                {
                    URL detailsUrl = new URL(request);
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
        //TODO: Insert code that will update the recycler views for both the trailers and the reviews
    }


    @Override
    public void onLoaderReset(Loader<Extra[]> loader)
    {
    }

    private void makeMovieDetailsRequest(@NonNull String endpoint)
    {
        Bundle requestBundle = new Bundle();
        requestBundle.putInt(getString(R.string.bundle_id), mMovie.getMovieId());
        if(endpoint.equals(getString(R.string.request_trailers_url)))
            requestBundle.putString(getString(R.string.bundle_trailers), endpoint);
        else
            requestBundle.putString(getString(R.string.bundle_reviews), endpoint);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> requestLoader = loaderManager.getLoader(MOVIE_REQUEST_LOADER);

        if (requestLoader == null) {
            loaderManager.initLoader(MOVIE_REQUEST_LOADER, requestBundle, this);
        } else {
            loaderManager.restartLoader(MOVIE_REQUEST_LOADER, requestBundle, this);
        }
    }

    public void addFavorite(View view)
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
        //TODO: Make sure to add the id of the trailer in the tag.
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
