package damian.nanodegree.google.popularmovies.utils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import damian.nanodegree.google.popularmovies.BuildConfig;
import damian.nanodegree.google.popularmovies.data.Movie;

/**
 * Created by robert_damian on 17.02.2018.
 */

public class NetworkUtils {

    private static final String API_KEY_QUERY = "api_key";
    private static final String API_BASE_URL = "http://api.themoviedb.org";
    private static final String API_VERSION = "3";
    private static final String API_MOVIE_PATH = "movie";

    public static final String API_TOP_RATED = "top_rated";
    public static final String API_MOST_POPULAR = "popular";

    private static final String IMAGES_BASE_URL = "https://image.tmdb.org/t/p";
    private static final String IMAGES_SIZE = "w185";


    public static URL getURL(String category) {
        Uri moviesUri = Uri.parse(API_BASE_URL).buildUpon()
                .appendPath(API_VERSION)
                .appendPath(API_MOVIE_PATH)
                .appendPath(category)
                .appendQueryParameter(API_KEY_QUERY, BuildConfig.MOVIEDB_API_KEY)
                .build();

        try {
            return new URL(moviesUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getResponse(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }

    public static String getImageURL(Movie movie) {
        return Uri.parse(IMAGES_BASE_URL).buildUpon()
                .appendPath(IMAGES_SIZE)
                .appendPath(movie.getImagePath())
                .build()
                .toString();
    }
}
