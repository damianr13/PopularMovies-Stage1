package damian.nanodegree.google.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import damian.nanodegree.google.popularmovies.data.Movie;
import damian.nanodegree.google.popularmovies.utils.NetworkUtils;

/**
 * Created by robert_damian on 17.02.2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{

    private List<Movie> mMoviesList;
    private final GridItemClickListener mItemClickListener;
    private int mViewHoldersCount;
    /**
     * Used for binding new view holders in the onCreateViewHolder method
     */
    public MovieAdapter(GridItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
        mViewHoldersCount = 0;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View movieView = inflater.inflate(R.layout.movie_item, parent, false);
        MovieViewHolder movieViewHolder = new MovieViewHolder(movieView);

        movieViewHolder.bind(mMoviesList.get(mViewHoldersCount));
        mViewHoldersCount++;
        return movieViewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(mMoviesList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mMoviesList == null) {
            return 0;
        }

        return mMoviesList.size();
    }

    public void swapSource(List<Movie> newMoviesList) {
        mMoviesList = newMoviesList;
        mViewHoldersCount = 0;
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
            Picasso.with(mContext).load(NetworkUtils.getImageURLAsString(movie)).into(mCoverImageView);
            Log.i("MovieAdapter", NetworkUtils.getImageURLAsString(movie));
        }

        @Override
        public void onClick(View view) {
            mItemClickListener.clickItem(mAssociatedMovie);
        }
    }
}
