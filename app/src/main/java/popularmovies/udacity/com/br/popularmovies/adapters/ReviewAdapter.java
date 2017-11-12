package popularmovies.udacity.com.br.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import popularmovies.udacity.com.br.popularmovies.R;
import popularmovies.udacity.com.br.popularmovies.model.Review;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.reviewViewHolder>
{
    private static final String TAG = MovieAdapter.class.getSimpleName();

    private final int mNumberReviews;
    private Review[] mReviews;

    @Override
    public reviewViewHolder onCreateViewHolder(ViewGroup group, int viewType)
    {
        Context context = group.getContext();
        int layoutId = R.layout.review_grid_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutId, group, false);

        return new ReviewAdapter.reviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(reviewViewHolder holder, int position)
    {
        holder.bind(position);
    }

    @Override
    public int getItemCount()
    {
        return mNumberReviews;
    }

    public ReviewAdapter(int numberReviews)
    {
        this.mNumberReviews = numberReviews;
        mReviews = new Review[numberReviews];
    }

    public void setReviews(Review[] reviews)
    {
        mReviews = reviews;
        notifyDataSetChanged();
    }

    class reviewViewHolder extends RecyclerView.ViewHolder
    {
        final TextView authorView;
        final TextView contentView;

        reviewViewHolder(View itemView)
        {
            super(itemView);
            authorView = itemView.findViewById(R.id.tv_review_author);
            contentView = itemView.findViewById(R.id.tv_review_content);
        }

        void bind (int index)
        {
            Review boundReview = mReviews[index];
            authorView.setText(boundReview.getAuthor());
            contentView.setText(boundReview.getContent());

            Log.d(TAG, boundReview.getId());
            Log.d(TAG, boundReview.getUrl());
        }
    }
}
