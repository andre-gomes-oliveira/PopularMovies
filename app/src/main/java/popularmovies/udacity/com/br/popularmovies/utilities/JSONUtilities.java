package popularmovies.udacity.com.br.popularmovies.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import popularmovies.udacity.com.br.popularmovies.Movie;


public class JSONUtilities {

    public static Movie[] getMovieDataFromJson(String forecastJsonStr)
            throws JSONException {

        final String MOVIES_LIST = "results";
        final String MESSAGE_CODE = "cod";

        final String MOVIE_ID = "id";
        final String MOVIE_TITLE = "title";
        final String MOVIE_POSTER = "poster_path";
        final String MOVIE_SYNOPSIS = "overview";
        final String MOVIE_RATING = "vote_average";
        final String MOVIE_DATE = "release_date";

        /* String array to hold each day's weather String */
        Movie[] parsedMoviesData;

        JSONObject resultsJson = new JSONObject(forecastJsonStr);

        /* Is there an error? */
        if (resultsJson.has(MESSAGE_CODE)) {
            int errorCode = resultsJson.getInt(MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

        JSONArray moviesArray = resultsJson.getJSONArray(MOVIES_LIST);

        parsedMoviesData = new Movie[moviesArray.length()];

        for (int i = 0; i < moviesArray.length(); i++) {
            /* movie data to be obtained*/
            int id;
            String title;
            String posterPath;
            String synopsis;
            int rating;
            String releaseDate;

            /* Get the JSON object representing the movie */
            JSONObject popularMovie = moviesArray.getJSONObject(i);

            id = popularMovie.getInt(MOVIE_ID);
            title = popularMovie.getString(MOVIE_TITLE);
            posterPath = popularMovie.getString(MOVIE_POSTER);
            synopsis = popularMovie.getString(MOVIE_SYNOPSIS);
            rating = popularMovie.getInt(MOVIE_RATING);
            releaseDate = popularMovie.getString(MOVIE_DATE);

            parsedMoviesData[i] = new Movie(id, title, posterPath, synopsis, rating, releaseDate);
        }

        return parsedMoviesData;
    }
}
