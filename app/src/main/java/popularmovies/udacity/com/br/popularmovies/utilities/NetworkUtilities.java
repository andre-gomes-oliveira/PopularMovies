package popularmovies.udacity.com.br.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Andre on 03/10/2017.
 *
 * Class containing a few functions used to deal with internet requests.
 * Based on functions using during previous tasks.
 */

public final class NetworkUtilities
{

    private static final String TAG = NetworkUtilities.class.getSimpleName();

    /* Base URLs*/
    private static final String api = "api_key";
    private static final String key = "";
    private static final String BASE_MOVIE_URL = "https://api.themoviedb.org/3/movie";

    public static URL buildMoviesUrl(String endpoint)
    {
        Uri builtUri = Uri.parse(BASE_MOVIE_URL + endpoint).buildUpon()
                .appendQueryParameter(api, key)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URL " + url);

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException
    {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try
        {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        }
        finally
        {
            urlConnection.disconnect();
        }
    }
}
