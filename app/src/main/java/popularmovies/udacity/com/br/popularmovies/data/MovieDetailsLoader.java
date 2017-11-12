package popularmovies.udacity.com.br.popularmovies.data;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import popularmovies.udacity.com.br.popularmovies.R;
import popularmovies.udacity.com.br.popularmovies.model.Extra;
import popularmovies.udacity.com.br.popularmovies.utilities.JSONUtilities;
import popularmovies.udacity.com.br.popularmovies.utilities.NetworkUtilities;

public class MovieDetailsLoader extends AsyncTaskLoader<Extra[]>
{
    private final Bundle mBundle;

    public MovieDetailsLoader(@NonNull Context context, @NonNull final Bundle args) {
        super(context);
        mBundle = args;
    }

    @Override
    protected void onStartLoading()
    {
        if (mBundle == null) {
            return;
        }

        forceLoad();
    }

    @Override
    public Extra[] loadInBackground()
    {
        Context context = getContext();
        Integer id;
        String request, trailer, review;

        id = mBundle.getInt(context.getString(R.string.bundle_id));
        trailer = mBundle.getString(context.getString(R.string.bundle_trailers));
        review = mBundle.getString(context.getString(R.string.bundle_reviews));

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
}
