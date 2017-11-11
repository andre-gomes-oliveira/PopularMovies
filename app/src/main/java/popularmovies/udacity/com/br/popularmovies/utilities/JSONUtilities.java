package popularmovies.udacity.com.br.popularmovies.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import popularmovies.udacity.com.br.popularmovies.model.Movie;
import popularmovies.udacity.com.br.popularmovies.model.Review;
import popularmovies.udacity.com.br.popularmovies.model.Trailer;

public class JSONUtilities
{

    public static Movie[] getMovieDataFromJson(String moviesJsonStr)
            throws JSONException
    {

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

        JSONObject resultsJson = new JSONObject(moviesJsonStr);

        /* Is there an error? */
        if (resultsJson.has(MESSAGE_CODE))
        {
            int errorCode = resultsJson.getInt(MESSAGE_CODE);

            switch (errorCode)
            {
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

        for (int i = 0; i < moviesArray.length(); i++)
        {
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

    public static Trailer[] getTrailersDataFromJson(String trailersJson)
            throws JSONException {

        final String TRAILERS_LIST = "results";
        final String MESSAGE_CODE = "cod";

        final String TRAILER_ID = "id";
        final String TRAILER_NAME = "name";
        final String TRAILER_TYPE = "type";
        final String TRAILER_KEY = "key";

        /* String array to hold each day's weather String */
        Trailer[] parsedTrailersData;

        JSONObject resultsJson = new JSONObject(trailersJson);

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

        JSONArray trailersArray = resultsJson.getJSONArray(TRAILERS_LIST);

        parsedTrailersData = new Trailer[trailersArray.length()];

        for (int i = 0; i < trailersArray.length(); i++) {
            /* Get the JSON object representing the trailer */
            JSONObject trailer = trailersArray.getJSONObject(i);

            /*Obtaining the trailer data */
            int id = trailer.getInt((TRAILER_ID));
            String name = trailer.getString(TRAILER_NAME);
            String type = trailer.getString(TRAILER_TYPE);
            String key = trailer.getString(TRAILER_KEY);

            parsedTrailersData[i] = new Trailer(id, name, type, key);
        }

        return parsedTrailersData;
    }

    public static Review[] getReviewsDataFromJson(String reviewsJson)
            throws JSONException {

        final String TRAILERS_LIST = "results";
        final String MESSAGE_CODE = "cod";

        final String REVIEW_ID = "id";
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_CONTENT = "content";
        final String REVIEW_URL = "url";

        /* String array to hold each day's weather String */
        Review[] parsedReviewsData;

        JSONObject resultsJson = new JSONObject(reviewsJson);

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

        JSONArray reviewsArray = resultsJson.getJSONArray(TRAILERS_LIST);

        parsedReviewsData = new Review[reviewsArray.length()];

        for (int i = 0; i < reviewsArray.length(); i++) {
            /* Get the JSON object representing the review */
            JSONObject review = reviewsArray.getJSONObject(i);

            /*Obtaining the review data */
            int id = review.getInt((REVIEW_ID));
            String author = review.getString(REVIEW_AUTHOR);
            String content = review.getString(REVIEW_CONTENT);
            String url = review.getString(REVIEW_URL);

            parsedReviewsData[i] = new Review(id, author, content, url);
        }

        return parsedReviewsData;
    }
}
