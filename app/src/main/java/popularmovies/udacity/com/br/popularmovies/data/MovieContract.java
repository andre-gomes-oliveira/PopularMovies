package popularmovies.udacity.com.br.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract
{
    static final String AUTHORITY = "popularmovies.udacity.com.br.popularmovies";

    // The base content URI = "content://" + <authority>
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        /* The name of the table */
        static final String TABLE_NAME = "movies";

        /* The name of each of the table columns */
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "release";

        /* The column index of each of the columns */
        public static final int INDEX_ID = 0;
        public static final int INDEX_TITLE = 1;
        public static final int INDEX_POSTER_PATH = 2;
        public static final int INDEX_SYNOPSIS = 3;
        public static final int INDEX_RATING = 4;
        public static final int INDEX_RELEASE_DATE = 5;
    }
}

