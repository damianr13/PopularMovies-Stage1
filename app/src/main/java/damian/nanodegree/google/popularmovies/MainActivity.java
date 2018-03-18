package damian.nanodegree.google.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
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
import android.widget.Toast;

import org.json.JSONException;
import org.parceler.Parcels;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import damian.nanodegree.google.popularmovies.data.Movie;
import damian.nanodegree.google.popularmovies.data.MovieDBContract;
import damian.nanodegree.google.popularmovies.data.MoviesSharedPreferences;
import damian.nanodegree.google.popularmovies.helpers.LoaderHelper;
import damian.nanodegree.google.popularmovies.utils.JSONUtils;
import damian.nanodegree.google.popularmovies.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.GridItemClickListener, LoaderManager.LoaderCallbacks,
        LoaderHelper.ResultsDisplayer{

    private static final int ID_MOVIES_LOADER = 13;
    private static final int ID_MOVIES_LOADER_POPULARITY = 14;
    private static final int ID_MOVIES_LOADER_RATING = 15;
    private static final int ID_MOVIES_LOADER_FAVORITES = 16;

    private static final String SAVED_STATE_LOADER_KEY = "loader_id";

    private MovieAdapter mMoviesAdapter;

    private RecyclerView mMoviesRecyclerView;
    private LoaderHelper mMoviesLoaderHelper;

    private MoviesSharedPreferences mMoviesPreferences;
    private int mCurrentLoaderId;

    private int mCurrentPage;

    private static final Collection<Integer> HANDLED_MENU_ITEMS = new TreeSet<Integer>();
    static {
        HANDLED_MENU_ITEMS.add(R.id.action_sort_favorites);
        HANDLED_MENU_ITEMS.add(R.id.action_sort_popularity);
        HANDLED_MENU_ITEMS.add(R.id.action_sort_rating);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMoviesRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        RecyclerView.LayoutManager rvLayoutManager =
                new GridLayoutManager(this, getNumberOfColumns(this));
        mMoviesRecyclerView.setLayoutManager(rvLayoutManager);

        mMoviesAdapter = new MovieAdapter(this);
        mMoviesRecyclerView.setAdapter(mMoviesAdapter);

        mMoviesLoaderHelper = new LoaderHelper.LoaderHelperBuilder()
                .setDisplayedResultsView(mMoviesRecyclerView)
                .setErrorView(findViewById(R.id.tv_error))
                .setLoadingView(findViewById(R.id.pb_loading))
                .setHelperId(ID_MOVIES_LOADER)
                .setResultDisplayer(this)
                .build(this);

        mMoviesPreferences = new MoviesSharedPreferences(this);

        mCurrentPage = 1;

        // check for stored preference
        mCurrentLoaderId = mMoviesPreferences.getPreferedLoaderId(ID_MOVIES_LOADER_POPULARITY);

        // check for saved instance state
        if (savedInstanceState != null &&
                savedInstanceState.getInt(SAVED_STATE_LOADER_KEY, -1) != -1) {
            mCurrentLoaderId = savedInstanceState.getInt(SAVED_STATE_LOADER_KEY);
        }

        if (NetworkUtils.isConnected(this)) {
            mMoviesLoaderHelper.loadStarted();
            initOrRestartLoader(mCurrentLoaderId);
        }
        else {
            mMoviesLoaderHelper.loadFailed();
        }

        initScrollListener();
    }

    /**
     * Inspired by answer on stackOverflow:
     * Source: <a href="https://stackoverflow.com/questions/10316743/detect-end-of-scrollview">
     *     StackOverflow</a>
     */
    private void initScrollListener() {
        mMoviesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    // favorite movies are loaded all at once
                    if (mCurrentLoaderId == ID_MOVIES_LOADER_FAVORITES) {
                        return ;
                    }

                    getSupportLoaderManager().restartLoader(mCurrentLoaderId, null,
                            MainActivity.this);
                }
            }
        });
    }

    @Override
    public void clickItem(Movie clickedMovie) {
        Intent goToDetailsActivity = new Intent(this, DetailActivity.class);
        goToDetailsActivity.putExtra(DetailActivity.EXTRA_MOVIE_KEY, Parcels.wrap(clickedMovie));
        startActivity(goToDetailsActivity);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        mCurrentLoaderId = id;
        mMoviesPreferences.setPreferedLoaderId(id);
        switch (id) {
            case ID_MOVIES_LOADER_POPULARITY:
                return new MoviesLoader(this, NetworkUtils.API_MOST_POPULAR, mCurrentPage++);
            case ID_MOVIES_LOADER_RATING:
                return new MoviesLoader(this, NetworkUtils.API_TOP_RATED, mCurrentPage++);
            case ID_MOVIES_LOADER_FAVORITES:
                return new CursorLoader(this,
                        MovieDBContract.MovieEntry.CONTENT_URI,
                        null, null, null, null);
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
    @SuppressWarnings("unchecked")
    public void onLoadFinished(Loader loader, Object data) {
        if (data == null) {
            mMoviesLoaderHelper.loadFailed();
            return ;
        }

        if (data instanceof Cursor) {
            mMoviesLoaderHelper.loadSucceeded(buildMoviesListFromCursor((Cursor) data));
            return ;
        }
        try {
            mMoviesLoaderHelper.loadSucceeded(data);
        } catch (ClassCastException ex) {
            Log.e("MainActivity", null, ex);
            mMoviesLoaderHelper.loadFailed();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void displayResults(int helperId, Object result) {
        mMoviesAdapter.addToSource((List<Movie>) result);
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
        mMoviesAdapter.swapSource(new ArrayList<Movie>());
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
        mMoviesLoaderHelper.loadStarted();
        Loader existingLoader;

        int selectedLoaderId = -1;
        int menuItemId = item.getItemId();

        if (!HANDLED_MENU_ITEMS.contains(menuItemId)) {
            return super.onOptionsItemSelected(item);
        }

        switch (menuItemId) {
            case R.id.action_sort_popularity:
                selectedLoaderId = ID_MOVIES_LOADER_POPULARITY;
                break;
            case R.id.action_sort_rating:
                selectedLoaderId = ID_MOVIES_LOADER_RATING;
                break;
            case R.id.action_sort_favorites:
                selectedLoaderId = ID_MOVIES_LOADER_FAVORITES;
        }

        mCurrentPage = 1;
        mMoviesAdapter.swapSource(new ArrayList<Movie>());

        initOrRestartLoader(selectedLoaderId);
        return true;
    }

    private void initOrRestartLoader(int loaderId) {
        Loader existingLoader = getSupportLoaderManager().getLoader(loaderId);
        if (existingLoader == null) {
            getSupportLoaderManager().initLoader(
                    loaderId, null, this);
            return ;
        }
        getSupportLoaderManager().restartLoader(
                loaderId, null, this);
    }
    
    private static class MoviesLoader extends AsyncTaskLoader<List<Movie>> {

        private List<Movie> mResult;
        private String mSortCriteria;
        private int mPageIndex;
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
        MoviesLoader(Context context, String sortCriteria) {
            this (context, sortCriteria, 1);
        }

        MoviesLoader(Context context, String sortCriteria, int pageIndex) {
            super(context);
            mResult = null;
            mSortCriteria = sortCriteria;
            mPageIndex = pageIndex;
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
                URL topRatedURL = NetworkUtils.getURL(mSortCriteria, mPageIndex);
                String sortedResult = NetworkUtils.getResponse(topRatedURL);

                List<Movie> loadedMovieList = JSONUtils.readMoviesFromJSON(sortedResult);

                for (Movie movie : loadedMovieList) {
                    movie._setFavorite(movie.isStoredInDatabase(this.getContext()));
                }

                return loadedMovieList;
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
