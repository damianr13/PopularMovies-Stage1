package damian.nanodegree.google.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import damian.nanodegree.google.popularmovies.data.Movie;
import damian.nanodegree.google.popularmovies.utils.NetworkUtils;

/**
 * Created by robert_damian on 17.02.2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{

    private Cursor mMoviesCursor;
    private final GridItemClickListener mItemClickListener;
    /**
     * Used for binding new view holders in the onCreateViewHolder method
     */
    public MovieAdapter(GridItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View movieView = inflater.inflate(R.layout.movie_item, parent, false);
        MovieViewHolder movieViewHolder = new MovieViewHolder(movieView);
        if (mMoviesCursor.isBeforeFirst()) {
            mMoviesCursor.moveToFirst();
        }

        movieViewHolder.bind(Movie.buildFromCursor(mMoviesCursor));
        mMoviesCursor.moveToNext();
        return movieViewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        if (position >= mMoviesCursor.getCount()) {
            return ;
        }
        mMoviesCursor.moveToPosition(position);
        holder.bind(Movie.buildFromCursor(mMoviesCursor));
    }

    @Override
    public int getItemCount() {
        if (mMoviesCursor == null) {
            return 0;
        }

        return mMoviesCursor.getCount();
    }

    public void swapCursor(Cursor newMovieCursor) {
        mMoviesCursor = newMovieCursor;
        notifyDataSetChanged();
    }

    public interface GridItemClickListener {
        void clickItem(Movie clickedMovie);
    }

    class MovieViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private ImageView mCoverImageView;
        private Context mContext;
        private Movie mAssociatedMovie;

        MovieViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mCoverImageView = (ImageView) itemView.findViewById(R.id.iv_movie_cover);
            mContext = itemView.getContext();
        }

        /**
         * Set the new movie to display
         * @param movie
         */
        void bind(Movie movie) {
            mAssociatedMovie = movie;
            Picasso.with(mContext).load(NetworkUtils.getImageURL(movie)).into(mCoverImageView);
            Log.i("MovieAdapter", NetworkUtils.getImageURL(movie));
        }

        @Override
        public void onClick(View view) {
            mItemClickListener.clickItem(mAssociatedMovie);
        }
    }
}
