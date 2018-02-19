package damian.nanodegree.google.popularmovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import java.net.URL;
import java.util.List;

import damian.nanodegree.google.popularmovies.data.Movie;
import damian.nanodegree.google.popularmovies.data.MovieDBContract;
import damian.nanodegree.google.popularmovies.utils.JSONUtils;
import damian.nanodegree.google.popularmovies.utils.NetworkUtils;

/**
 * Created by robert_damian on 17.02.2018.
 */

public class MoviesSyncTask {
    @WorkerThread
    synchronized public static void syncMovies(@NonNull Context context) {
        try {
            URL topRatedURL = NetworkUtils.getURL(NetworkUtils.API_TOP_RATED);
            String topRatedResult = NetworkUtils.getResponse(topRatedURL);

            URL mostPopularURL = NetworkUtils.getURL(NetworkUtils.API_MOST_POPULAR);
            String mostPopularResult = NetworkUtils.getResponse(mostPopularURL);

            // when we don't have new data we can get out of this method
            if (!hasResponse(topRatedResult, mostPopularResult)) {
                return ;
            }

            ContentResolver contentResolver = context.getContentResolver();
            Uri moviesUri = MovieDBContract.MovieEntry.CONTENT_URI;

            // delete previous data
            contentResolver.delete(moviesUri, null, null);

            List<Movie> topRatedMovieList = JSONUtils.readMoviesFromJSON(topRatedResult);
            List<Movie> mostPopularMovieList = JSONUtils.readMoviesFromJSON(mostPopularResult);

            contentResolver.bulkInsert(moviesUri, getContentValueFor(topRatedMovieList));
            contentResolver.bulkInsert(moviesUri, getContentValueFor(mostPopularMovieList));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean hasResponse(String topRatedResponse, String mostPopularResponse) {
        if (topRatedResponse == null || "".equals(topRatedResponse)) {
            return false;
        }

        if (mostPopularResponse == null || "".equals(mostPopularResponse)) {
            return false;
        }

        return true;
    }

    private static ContentValues[] getContentValueFor(List<Movie> movieList) {
        ContentValues[] result = new ContentValues[movieList.size()];
        int index = 0;
        for (Movie movie: movieList) {
            result[index++] = movie.getContentValues();
        }

        return result;
    }

}
