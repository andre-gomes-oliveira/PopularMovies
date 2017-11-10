package popularmovies.udacity.com.br.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import popularmovies.udacity.com.br.popularmovies.data.MovieContract.MovieEntry;

public class MovieDBHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "moviesDb.db";
    private static final int VERSION = 1;

    MovieDBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        final String CREATE_TABLE = "CREATE TABLE "  + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID               + " INTEGER PRIMARY KEY, " +
                MovieEntry.COLUMN_TITLE      + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTERPATH      + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_SYNOPSIS      + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RATING    + " INTEGER NOT NULL " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL);";

        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
