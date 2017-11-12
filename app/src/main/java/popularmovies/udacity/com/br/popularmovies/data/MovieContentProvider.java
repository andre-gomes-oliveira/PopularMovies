package popularmovies.udacity.com.br.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static popularmovies.udacity.com.br.popularmovies.data.MovieContract.MovieEntry;

public class MovieContentProvider extends ContentProvider
{
    private static final String TAG = MovieContentProvider.class.getSimpleName();
    private final static String ERROR_MSG = "Content provider operation failed. Check the log for details.";

    private static final int MOVIES = 100 ;
    private static final int MOVIES_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private MovieDBHelper mMovieDbHelper;

    @Override
    public boolean onCreate()
    {
        Context context = getContext();
        mMovieDbHelper = new MovieDBHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder)
    {
        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match)
        {
            case MOVIES:
                retCursor =  db.query(MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case MOVIES_WITH_ID:
                String id = uri.getLastPathSegment();

                retCursor = db.query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        "_id=?",
                        new String[]{id},
                        null,
                        null,
                        sortOrder);
                break;
            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        try
        {
             retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        catch (java.lang.NullPointerException e)
        {
            Log.d(TAG, ERROR_MSG);
            e.printStackTrace();
        }

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri)
    {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri,
                      @Nullable ContentValues contentValues)
    {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match)
        {
            case MOVIES:
                long id = db.insert(MovieEntry.TABLE_NAME, null, contentValues);
                if ( id > 0 )
                {
                    returnUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, id);
                }
                else
                    throw new android.database.SQLException(ERROR_MSG + "On the following URI: " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        try
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        catch (java.lang.NullPointerException e)
        {
            Log.d(TAG, ERROR_MSG);
            e.printStackTrace();
        }

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs)
    {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int moviesDeleted;

        switch (match) {
            case MOVIES_WITH_ID:
                String id = uri.getLastPathSegment();
                moviesDeleted = db.delete(MovieEntry.TABLE_NAME,
                        MovieEntry._ID + "=?",
                        new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (moviesDeleted != 0) {
            try
            {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            catch (java.lang.NullPointerException e)
            {
                Log.d(TAG, ERROR_MSG);
                e.printStackTrace();
            }
        }

        return moviesDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs)
    {
        return 0;
    }

    private static UriMatcher buildUriMatcher()
    {
        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /*
          All paths added to the UriMatcher have a corresponding int.
          For each kind of uri you may want to access, add the corresponding match with addURI.
          The two calls below add matches for the task directory and a single item by ID.
         */
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES + "/#", MOVIES_WITH_ID);

        return uriMatcher;
    }
}
