package popularmovies.udacity.com.br.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.movieViewHolder>
{
    private static final String TAG = MovieAdapter.class.getSimpleName();
    private final movieClickListener mMovieClickListener;

    private final int mNumberMovies;
    private Movie[] mMovies;

    @Override
    public movieViewHolder onCreateViewHolder(ViewGroup group, int viewType)
    {
        Context context = group.getContext();
        int layoutId = R.layout.movie_grid_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutId, group, false);

        return new movieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(movieViewHolder holder, int position)
    {
        Log.d(TAG, "#" + position);
        Context viewContext = holder.posterView.getContext();
        holder.bind(position, viewContext);
    }

    @Override
    public int getItemCount()
    {
        return mNumberMovies;
    }

    MovieAdapter(movieClickListener listener)
    {
        mNumberMovies = MainActivity.num_mov_posters;
        mMovieClickListener = listener;
        mMovies = new Movie[mNumberMovies];
    }

    public class movieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        final ImageView posterView;

        movieViewHolder(View itemView)
        {
            super(itemView);

            posterView = itemView.findViewById(R.id.iv_item_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            mMovieClickListener.onMovieItemClick(mMovies[getAdapterPosition()]);
        }

        void bind (int index, Context context)
        {
            Movie clickedMovie = mMovies[index];
            String picassoURL = context.getString(R.string.base_picasso_url)
                    + clickedMovie.getMoviePosterPath();

            Log.v(TAG, "Picasso URL " + picassoURL);
            Picasso.with(context)
                    .load(picassoURL)
                    .placeholder(R.drawable.ic_error_white_24px)
                    .error(R.drawable.ic_photo_size_select_actual_white_24px)
                    .into(posterView);
        }
    }

    void setMovies(Movie[] movies)
    {
        mMovies = movies;
        notifyDataSetChanged();
    }

    public interface movieClickListener
    {
        void onMovieItemClick (Movie clickedMovie);
    }
}
