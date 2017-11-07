package popularmovies.udacity.com.br.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity
{
    private static final String TAG = MovieAdapter.class.getSimpleName();

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
            if (starterIntent.hasExtra(movieIntent))
            {
                Movie mMovie = starterIntent.getParcelableExtra(getString(R.string.movie_details_intent));

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

    public void addFavorite(View view)
    {
        //TODO Implement the logic to add a movie to a list of favorites.
    }
}
