package damian.nanodegree.google.popularmovies;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import damian.nanodegree.google.popularmovies.data.Movie;
import damian.nanodegree.google.popularmovies.utils.JSONUtils;
import damian.nanodegree.google.popularmovies.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.GridItemClickListener, LoaderManager.LoaderCallbacks{

    private static final int ID_MOVIES_LOADER = 13;
    private static final int ID_MOVIES_LOADER_POPULARITY = 14;
    private static final int ID_MOVIES_LOADER_RATING = 15;

    private static final String SAVED_STATE_LOADER_KEY = "loader_id";

    private MovieAdapter mMoviesAdapter;

    private TextView mErrorTextView;
    private ProgressBar mLoadingProgressBar;
    private RecyclerView mMoviesRecyclerView;

    private int mCurrentLoaderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMoviesRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mMoviesRecyclerView.setLayoutManager(new GridLayoutManager(this, getNumberOfColumns(this)));

        mMoviesAdapter = new MovieAdapter(this);
        mMoviesRecyclerView.setAdapter(mMoviesAdapter);

        mErrorTextView = (TextView) findViewById(R.id.tv_error);
        mLoadingProgressBar = (ProgressBar) findViewById(R.id.pb_loading);
        loadStarted();

        // default value
        mCurrentLoaderId = ID_MOVIES_LOADER_POPULARITY;

        if (savedInstanceState != null &&
                savedInstanceState.getInt(SAVED_STATE_LOADER_KEY, -1) != -1) {
            mCurrentLoaderId = savedInstanceState.getInt(SAVED_STATE_LOADER_KEY);
        }
        getSupportLoaderManager().initLoader(mCurrentLoaderId, null, this);
    }

    @Override
    public void clickItem(Movie clickedMovie) {
        Intent goToDetailsActivity = new Intent(this, DetailActivity.class);
        goToDetailsActivity.putExtra(DetailActivity.EXTRA_MOVIE_KEY, clickedMovie);
        startActivity(goToDetailsActivity);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        mCurrentLoaderId = id;
        switch (id) {
            case ID_MOVIES_LOADER_POPULARITY:
                return new MoviesLoader(this, NetworkUtils.API_MOST_POPULAR);
            case ID_MOVIES_LOADER_RATING:
                return new MoviesLoader(this, NetworkUtils.API_TOP_RATED);
            default:
                throw new UnsupportedOperationException("Loader not implemented:" + id);

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(SAVED_STATE_LOADER_KEY, mCurrentLoaderId);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if (data == null) {
            loadFailed();
            return ;
        }

        if (data instanceof Cursor) {
            loadSuccessful(buildMoviesListFromCursor((Cursor) data));
            return ;
        }
        try {
            loadSuccessful((List<Movie>) data);
        } catch (ClassCastException ex) {
            Log.e("MainActivity", null, ex);
            loadFailed();
        }
    }

    private void loadStarted() {
        mMoviesRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingProgressBar.setVisibility(View.VISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
    }

    private void loadSuccessful(List<Movie> newMoviesList) {
        mMoviesAdapter.swapSource(newMoviesList);
        mLoadingProgressBar.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
        mMoviesRecyclerView.setVisibility(View.VISIBLE);
    }

    private void loadFailed() {
        mLoadingProgressBar.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.VISIBLE);
        mMoviesRecyclerView.setVisibility(View.INVISIBLE);
    }

    private List<Movie> buildMoviesListFromCursor(Cursor cursor) {
        ArrayList<Movie> newMoviesList = new ArrayList<>();
        while (cursor.moveToNext()) {
            newMoviesList.add(Movie.buildFromCursor(cursor));
        }

        return newMoviesList;
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mMoviesAdapter.swapSource(null);
    }

    /**
     * Computes the number of movie posters to be displayed on one row
     * Source: <a href="https://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns">StackOverflow</a>
     * @param context
     * @return number of columns (posters) to be displayed on 1 row
     */
    private int getNumberOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int columnWidth = (int) (context.getResources().getDimension(R.dimen.column_width));
        float screenWidth = displayMetrics.widthPixels;

        return (int) (screenWidth / columnWidth);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        loadStarted();
        Loader existingLoader;
        switch (item.getItemId()) {
            case R.id.action_sort_popularity:
                existingLoader =
                        getSupportLoaderManager().getLoader(ID_MOVIES_LOADER_POPULARITY);
                if (existingLoader == null) {
                    getSupportLoaderManager()
                            .initLoader(ID_MOVIES_LOADER_POPULARITY, null, this);
                    break;
                }
                getSupportLoaderManager()
                        .restartLoader(ID_MOVIES_LOADER_POPULARITY, null, this);
                break;
            case R.id.action_sort_rating:
                existingLoader = getSupportLoaderManager().getLoader(ID_MOVIES_LOADER_RATING);
                if (existingLoader == null) {
                    getSupportLoaderManager()
                            .initLoader(ID_MOVIES_LOADER_RATING, null, this);
                    break;
                }
                getSupportLoaderManager()
                        .restartLoader(ID_MOVIES_LOADER_RATING, null, this);
        }

        return super.onOptionsItemSelected(item);
    }

    private static class MoviesLoader extends AsyncTaskLoader<List<Movie>> {

        private List<Movie> mResult;
        private String mSortCriteria;
        /**
         * Stores away the application context associated with context.
         * Since Loaders can be used across multiple activities it's dangerous to
         * store the context directly; always use {@link #getContext()} to retrieve
         * the Loader's Context, don't use the constructor argument directly.
         * The Context returned by {@link #getContext} is safe to use across
         * Activity instances.
         *
         * @param context used to retrieve the application context.
         */
        public MoviesLoader(Context context, String sortCriteria) {
            super(context);
            mResult = null;
            mSortCriteria = sortCriteria;
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
        public List<Movie> loadInBackground() {
            try {
                URL topRatedURL = NetworkUtils.getURL(mSortCriteria);
                String sortedResult = NetworkUtils.getResponse(topRatedURL);

                return JSONUtils.readMoviesFromJSON(sortedResult);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public void deliverResult(List<Movie> data) {
            mResult = data;
            super.deliverResult(data);
        }
    }
}
