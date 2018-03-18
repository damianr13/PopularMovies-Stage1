package damian.nanodegree.google.popularmovies.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import damian.nanodegree.google.popularmovies.data.Movie;
import damian.nanodegree.google.popularmovies.data.Review;

/**
 * Created by robert_damian on 17.02.2018.
 */

public class JSONUtils {

    private static final String RESULTS_KEY = "results";

    private static final String MOVIE_ID_KEY = "id";
    private static final String IMAGE_PATH_KEY = "poster_path";
    private static final String TITLE_KEY = "title";
    private static final String RATING_KEY = "vote_average";
    private static final String PLOT_KEY = "overview";
    private static final String POPULARITY_KEY = "popularity";
    private static final String RELEASE_DATE_KEY = "release_date";

    private static final String TRAILER_YOUTUBE_KEY = "key";

    private static final String REVIEW_USERNAME_KEY = "author";
    private static final String REVIEW_TEXT_KEY = "content";

    public static List<Movie> readMoviesFromJSON(String jsonString) throws JSONException {
        JSONObject rootJSON = new JSONObject(jsonString);
        JSONArray resultsJSONArr = rootJSON.optJSONArray(RESULTS_KEY);
        ArrayList<Movie> result = new ArrayList<>();
        for (int i = 0; i < resultsJSONArr.length(); i++) {
            result.add(readMovieFromJSON(resultsJSONArr.getJSONObject(i)));
        }

        return result;
    }

    public static Movie readMovieFromJSON(String jsonString) throws JSONException {
        return readMovieFromJSON(new JSONObject(jsonString));
    }

    public static Movie readMovieFromJSON(JSONObject movieJSON) {
        Movie result = new Movie();

        result.setId(movieJSON.optInt(MOVIE_ID_KEY));
        result.setImagePath(movieJSON.optString(IMAGE_PATH_KEY));
        result.setTitle(movieJSON.optString(TITLE_KEY));
        result.setPlot(movieJSON.optString(PLOT_KEY));
        result.setRating(movieJSON.optDouble(RATING_KEY));
        result.setPopularity(movieJSON.optDouble(POPULARITY_KEY));
        result.setReleaseDate(movieJSON.optString(RELEASE_DATE_KEY));


        return result;
    }

    public static List<String> readTrailersFromJSON(String jsonString) throws JSONException {
        List<String> result = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray allTrailers = jsonObject.optJSONArray(RESULTS_KEY);

        for (int i = 0; i < allTrailers.length(); i++) {
            JSONObject currentTrailerInfo = allTrailers.optJSONObject(i);
            result.add(currentTrailerInfo.optString(TRAILER_YOUTUBE_KEY));
        }

        return result;
    }

    public static List<Review> readReviewsFromJSON(String jsonString) throws JSONException{
        List<Review> result = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray allReviews = jsonObject.optJSONArray(RESULTS_KEY);

        for (int i = 0; i < allReviews.length(); i++) {
            JSONObject currentReviewInfo = allReviews.optJSONObject(i);

            Review review = new Review();
            review.setReviewText(currentReviewInfo.optString(REVIEW_TEXT_KEY));
            review.setUsername(currentReviewInfo.optString(REVIEW_USERNAME_KEY));

            result.add(review);
        }

        return result;
    }

}
