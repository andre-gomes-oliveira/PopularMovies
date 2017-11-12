package popularmovies.udacity.com.br.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import popularmovies.udacity.com.br.popularmovies.R;
import popularmovies.udacity.com.br.popularmovies.model.Trailer;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.trailerViewHolder>
{
    private static final String TAG = MovieAdapter.class.getSimpleName();

    private final int mNumberTrailers;
    private Trailer[] mTrailers;

    @Override
    public trailerViewHolder onCreateViewHolder(ViewGroup group, int viewType)
    {
        Context context = group.getContext();
        int layoutId = R.layout.trailer_grid_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutId, group, false);

        return new TrailerAdapter.trailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(trailerViewHolder holder, int position)
    {
        holder.bind(position);
    }

    @Override
    public int getItemCount()
    {
        return mNumberTrailers;
    }

    public TrailerAdapter(int numberTrailers)
    {
        this.mNumberTrailers = numberTrailers;
        mTrailers = new Trailer[numberTrailers];
    }

    public void setTrailers(Trailer[] trailers)
    {
        mTrailers = trailers;
        notifyDataSetChanged();
    }

    class trailerViewHolder extends RecyclerView.ViewHolder
    {
        final ImageButton shareButton;
        final ImageButton playButton;
        final TextView trailerDescView;

        trailerViewHolder(View itemView)
        {
            super(itemView);
            shareButton = itemView.findViewById(R.id.ib_share);
            playButton = itemView.findViewById(R.id.ib_play);
            trailerDescView = itemView.findViewById(R.id.tv_trailer_description);
        }

        void bind (int index)
        {
            Trailer boundTrailer = mTrailers[index];
            trailerDescView.setText(boundTrailer.getName());

            shareButton.setTag(boundTrailer.getKey());
            playButton.setTag(boundTrailer.getKey());

            Log.d(TAG, boundTrailer.getId());
            Log.d(TAG, boundTrailer.getName());
        }
    }
}
