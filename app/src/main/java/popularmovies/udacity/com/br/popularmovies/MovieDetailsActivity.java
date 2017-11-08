package popularmovies.udacity.com.br.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.net.URL;

import popularmovies.udacity.com.br.popularmovies.utilities.JSONUtilities;
import popularmovies.udacity.com.br.popularmovies.utilities.NetworkUtilities;

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
                Movie mMovie = starterIntent.getParcelableExtra(movieIntent);

                mTitleView.setText(mMovie.getMovieTitle());
                mSynopsisView.setText(mMovie.getMovieSynopsis());
                mReleaseView.setText(mMovie.getMovieReleaseDate());
                mRatingView.setText(String.valueOf(mMovie.getMovieRating()));

                String picassoURL = this.getString(R.string.base_picasso_url)
                        + mMovie.getMoviePosterPath();

                Log.v(TAG, "Picasso URL " + picassoURL);
                Picasso.with(this)
                        .load(picassoURL)
                        .placeholder(R.drawable.ic_error_white_24px)
                        .error(R.drawable.ic_photo_size_select_actual_white_24px)
                        .into(mPosterView);
            }
        }
    }

    public void addFavorite(View view)
    {
        //TODO Implement the logic to add a movie to a list of favorites.
        Toast.makeText(this, getString(R.string.favorite_inserted_message), Toast.LENGTH_SHORT)
                .show();

        ImageButton favoriteButton = findViewById(R.id.ib_favorite);
        favoriteButton.setImageResource(R.drawable.ic_favorite_black_24px);
    }
	
	/** This function attempts to open the selected trailer using YouTube.
	*   If that fails, it tries to open it in a browser.
	*   Since I wasn't sure of how to call YouTube via explicit intent, I used the following source as an example:
	*  https://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
	*/
	public void playMovieTrailer(Context context, String id)
	{
		Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.youtube_intent) + id));

		try
		{
			context.startActivity(appIntent);
		}
		catch (ActivityNotFoundException ex)
		{
			Intent webIntent = new Intent(Intent.ACTION_VIEW,
				Uri.parse(getString(R.string.youtube_url) + id));
				
			context.startActivity(webIntent);
		}
	}

}
