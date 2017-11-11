package popularmovies.udacity.com.br.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable
{
    private final int mId;
    private final String mTitle;
    private final String mPosterPath;
    private final String mSynopsis;
    private final int mRating;
    private final String mReleaseDate;

    public Movie(int id, String title, String posterPath, String synopsis,
                 int rating, String releaseDate)
    {
        this.mId = id;
        this.mTitle = title;
        this.mPosterPath = posterPath;
        this.mSynopsis = synopsis;
        this.mRating = rating;
        this.mReleaseDate = releaseDate;
    }

    private Movie(Parcel in)
    {
        this.mId = in.readInt();
        this.mTitle = in.readString();
        this.mPosterPath = in.readString();
        this.mSynopsis = in.readString();
        this.mRating = in.readInt();
        this.mReleaseDate = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(mId);
        dest.writeString(mTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mSynopsis);
        dest.writeInt(mRating);
        dest.writeString(mReleaseDate);
    }

    public int getMovieId() { return mId; }

    public String getMovieTitle()
    {
        return mTitle;
    }

    public String getMoviePosterPath()
    {
        return mPosterPath;
    }

    public String getMovieSynopsis()
    {
        return mSynopsis;
    }

    public int getMovieRating()
    {
        return mRating;
    }

    public String getMovieReleaseDate()
    {
        return mReleaseDate;
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>()
    {
        @Override
        public Movie createFromParcel(Parcel parcel)
        {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };
}
