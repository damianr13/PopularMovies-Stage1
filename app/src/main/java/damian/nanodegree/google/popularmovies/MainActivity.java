package damian.nanodegree.google.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import damian.nanodegree.google.popularmovies.data.Movie;
import damian.nanodegree.google.popularmovies.data.MovieDBContract;
import damian.nanodegree.google.popularmovies.sync.MoviesSyncUtils;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.GridItemClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    private static final int ID_MOVIES_LOADER = 13;
    private static final int ID_MOVIES_LOADER_POPULARITY = 14;
    private static final int ID_MOVIES_LOADER_RATING = 15;

    private MovieAdapter mMoviesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv_movies);
        rv.setLayoutManager(new GridLayoutManager(this, getNumberOfColumns(this)));

        mMoviesAdapter = new MovieAdapter(this);
        rv.setAdapter(mMoviesAdapter);

        getSupportLoaderManager().initLoader(ID_MOVIES_LOADER, null, this);
        MoviesSyncUtils.initialize(this);
    }

    @Override
    public void clickItem(Movie clickedMovie) {
        // TODO: logic to open the new activity with movie details
        Intent goToDetailsActivity = new Intent(this, DetailActivity.class);
        goToDetailsActivity.putExtra(DetailActivity.EXTRA_MOVIE_KEY, clickedMovie);
        startActivity(goToDetailsActivity);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        /* URI for all rows of weather data in our weather table */
        Uri moviesUri = MovieDBContract.MovieEntry.CONTENT_URI;

        switch (id) {
            case ID_MOVIES_LOADER:
                return new CursorLoader(this,
                        moviesUri,
                        null,
                        null,
                        null,
                        null);
            case ID_MOVIES_LOADER_POPULARITY:
                return new CursorLoader(this,
                        moviesUri,
                        null,
                        null,
                        null,
                        MovieDBContract.MovieEntry.COLUMN_POPULARITY + " DESC");
            case ID_MOVIES_LOADER_RATING:
                return new CursorLoader(this,
                        moviesUri,
                        null,
                        null,
                        null,
                        MovieDBContract.MovieEntry.COLUMN_RATING + " DESC");
            default:
                throw new UnsupportedOperationException("Loader not implemented:" + id);

        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMoviesAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdapter.swapCursor(null);
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
        Loader existingLoader;
        switch (item.getItemId()) {
            case R.id.action_refresh:
                getSupportLoaderManager().restartLoader(ID_MOVIES_LOADER, null, this);
                break;
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
}
