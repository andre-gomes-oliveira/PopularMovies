package popularmovies.udacity.com.br.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity
{
    private static final String TAG = MovieAdapter.class.getSimpleName();

    /* The movie that will be displayed*/
    private Movie mMovie;

    /* TextView that will display the title of the movie */
    private TextView mTitleView;

    /* The Image View where the poster image will be displayed */
    private ImageView mPosterView;

    /* The TextView that will display the synopsis of the movie */
    private TextView mSynopsisView;

    /* The TextView that will display the synopsis of the movie */
    private TextView mReleaseView;

    /* The TextView that will display the synopsis of the movie */
    private TextView mRatingView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        mTitleView = (TextView)  findViewById(R.id.tv_details_title);
        mPosterView = (ImageView) findViewById(R.id.iv_details_poster);
        mSynopsisView = (TextView) findViewById(R.id.tv_details_synopsis);
        mReleaseView = (TextView) findViewById(R.id.tv_details_date);
        mRatingView = (TextView) findViewById(R.id.tv_details_rating);

        Intent starterIntent = getIntent();

        if (starterIntent != null)
        {
            String movieIntent = getString(R.string.movie_details_intent);
            if (starterIntent.hasExtra(movieIntent))
            {
                mMovie = starterIntent.getParcelableExtra(getString(R.string.movie_details_intent));

                mTitleView.setText(mMovie.getMovieTitle());
                mSynopsisView.setText(mMovie.getMovieSynopsis());
                mReleaseView.setText(mMovie.getMovieReleaseDate());
                mRatingView.setText(String.valueOf(mMovie.getMovieRating()));

                String picassoURL = this.getString(R.string.base_picasso_url)
                        + mMovie.getMoviePosterPath();

                Log.v(TAG, "Picasso URL " + picassoURL);
                Picasso.with(this)
                        .load(picassoURL)
                        .into(mPosterView);
            }
        }
    }
}
