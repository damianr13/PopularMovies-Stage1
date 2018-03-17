package damian.nanodegree.google.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.parceler.Parcels;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import damian.nanodegree.google.popularmovies.data.Movie;
import damian.nanodegree.google.popularmovies.data.Review;
import damian.nanodegree.google.popularmovies.databinding.ActivityDetailBinding;
import damian.nanodegree.google.popularmovies.helpers.LoaderHelper;
import damian.nanodegree.google.popularmovies.utils.JSONUtils;
import damian.nanodegree.google.popularmovies.utils.NetworkUtils;

public class DetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks, LoaderHelper.ResultsDisplayer{

    public static final String EXTRA_MOVIE_KEY = "movie";

    private static final int TRAILERS_LOADER_ID = 13;
    private static final int REVIEWS_LOADER_ID = 17;

    private Movie mMovie;

    private LinearLayout mTrailersContainerLinearLayout;
    private LinearLayout mReviewsContainerLinearLayout;

    private LoaderHelper trailersLoaderHelper;
    private LoaderHelper reviewsLoaderHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        ActivityDetailBinding dataBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_detail);

        mMovie = Parcels.unwrap(
                getIntent().getParcelableExtra(EXTRA_MOVIE_KEY));
        fillUserInterface(dataBinding);

        mTrailersContainerLinearLayout = dataBinding.llTrailersContainer;
        mReviewsContainerLinearLayout = dataBinding.llReviewsContainer;

        trailersLoaderHelper = new LoaderHelper.LoaderHelperBuilder()
                .setHelperId(TRAILERS_LOADER_ID)
                .setErrorView(dataBinding.tvTrailersError)
                .setDisplayedResultsView(dataBinding.llTrailersContainer)
                .setLoadingView(dataBinding.pbTrailersLoading)
                .setResultDisplayer(this)
                .build(this);

        reviewsLoaderHelper = new LoaderHelper.LoaderHelperBuilder()
                .setHelperId(REVIEWS_LOADER_ID)
                .setErrorView(dataBinding.tvReviewsError)
                .setDisplayedResultsView(dataBinding.llReviewsContainer)
                .setLoadingView(dataBinding.pbReviewsLoading)
                .setResultDisplayer(this)
                .build(this);

        if (NetworkUtils.isConnected(this)) {
            trailersLoaderHelper.loadStarted();
            getSupportLoaderManager().initLoader(TRAILERS_LOADER_ID, null, this);

            reviewsLoaderHelper.loadStarted();
            getSupportLoaderManager().initLoader(REVIEWS_LOADER_ID, null, this);
        }
        else {
            trailersLoaderHelper.loadFailed();
            reviewsLoaderHelper.loadFailed();
        }
    }

    private void fillUserInterface(ActivityDetailBinding dataBinding) {
        dataBinding.tvPlot.setText(mMovie.getPlot());
        dataBinding.tvTitle.setText(mMovie.getTitle());
        dataBinding.tvRating.setText(String.valueOf(mMovie.getRating()));
        dataBinding.tvReleaseDate.setText(mMovie.getReleaseDate());

        Picasso.with(this).load(NetworkUtils.getImageURLAsString(mMovie)).into(dataBinding.ivCover);
    }

    public void addToFavorites(View view) {
        ImageView starImageView = (ImageView) view;

        if (mMovie.isFavorite()) {
            starImageView.setImageResource(R.mipmap.ic_empty_star);
            mMovie.setFavorite(false);
        }
        else {
            starImageView.setImageResource(R.mipmap.ic_star_full);
            mMovie.setFavorite(true);
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case TRAILERS_LOADER_ID:
                return new TrailersLoader(this, mMovie.getId());
            case REVIEWS_LOADER_ID:
                return new ReviewsLoader(this, mMovie.getId());
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        int loaderId = loader.getId();
        switch (loaderId) {
            case TRAILERS_LOADER_ID:
                onLoadFinished(trailersLoaderHelper, data);
            case REVIEWS_LOADER_ID:
                onLoadFinished(reviewsLoaderHelper, data);
        }

    }

    private void onLoadFinished(LoaderHelper helper, Object data) {
        if (data == null) {
            helper.loadFailed();
            return ;
        }

        try {
            helper.loadSucceeded(data);
        } catch (ClassCastException ex) {
            Log.e("MainActivity", null, ex);
            helper.loadFailed();
        }

    }

    private void addTrailer(LayoutInflater layoutInflater, final String trailerKey) {
        View trailerView = layoutInflater.inflate(R.layout.video_icon,
                mTrailersContainerLinearLayout, false);
        mTrailersContainerLinearLayout.addView(trailerView);

        ImageView trailerIcon = (ImageView) trailerView.findViewById(R.id.iv_video_thumbnail);

        trailerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchYoutube = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("vnd.youtube:" + trailerKey));
                DetailActivity.this.startActivity(launchYoutube);
            }
        });
        Picasso.with(this).load(NetworkUtils.getTrailerImageURLAsString(trailerKey)).into(trailerIcon);
    }

    private void addReview(LayoutInflater layoutInflater, final Review review) {
        View reviewView = layoutInflater.inflate(R.layout.review, mReviewsContainerLinearLayout,
                false);
        mReviewsContainerLinearLayout.addView(reviewView);

        TextView userTextView = (TextView) reviewView.findViewById(R.id.tv_reviewer);
        userTextView.setText(review.getUsername());

        final TextView reviewContentTextView =
                (TextView) reviewView.findViewById(R.id.tv_review_text);
        reviewContentTextView.setText(review.getLimitedReviewText());

        reviewContentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reviewContent = review.isExpanded() ?
                        review.getLimitedReviewText():
                        review.getReviewText();

                review.setExpanded(!review.isExpanded());
                reviewContentTextView.setText(reviewContent);
            }
        });
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    @SuppressWarnings("unchecked")
    public void displayResults(int helperId, Object result) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        switch (helperId) {
            case TRAILERS_LOADER_ID:
                List<String> trailerKeys = (List<String>) result;
                for (String trailerKey : trailerKeys) {
                    addTrailer(layoutInflater, trailerKey);
                }
                break;
            case REVIEWS_LOADER_ID:
                List<Review> reviewList = (List<Review>) result;
                for (Review review : reviewList) {
                    addReview(layoutInflater, review);
                }
                break;
        }
    }

    private static class TrailersLoader extends AsyncTaskLoader<List<String>> {

        private int mMovieId;
        private List<String> mResult;

        TrailersLoader(Context context, int movieId) {
            super(context);
            mMovieId = movieId;
        }

        @Override
        protected void onStartLoading() {
            if (mResult != null) {
                deliverResult(mResult);
                return ;
            }
            forceLoad();
        }

        @Override
        public List<String> loadInBackground() {
            try {
                URL trailersURL = NetworkUtils.getTrailersURL(mMovieId);
                String movieTrailersInfo = NetworkUtils.getResponse(trailersURL);

                return JSONUtils.readTrailersFromJSON(movieTrailersInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public void deliverResult(List<String> data) {
            mResult = data;
            super.deliverResult(data);
        }
    }

    private static class ReviewsLoader extends AsyncTaskLoader<List<Review>> {

        private int mMovieId;
        private List<Review> mResult;

        ReviewsLoader(Context context, int movieId) {
            super(context);
            mMovieId = movieId;
        }

        @Override
        protected void onStartLoading() {
            if (mResult != null) {
                deliverResult(mResult);
                return ;
            }
            forceLoad();
        }

        @Override
        public List<Review> loadInBackground() {
            try {
                URL reviewsURL = NetworkUtils.getReviewsURL(mMovieId);
                String reviewsTrailersInfo = NetworkUtils.getResponse(reviewsURL);

                return JSONUtils.readReviewsFromJSON(reviewsTrailersInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public void deliverResult(List<Review> data) {
            mResult = data;
            super.deliverResult(data);
        }
    }
}
