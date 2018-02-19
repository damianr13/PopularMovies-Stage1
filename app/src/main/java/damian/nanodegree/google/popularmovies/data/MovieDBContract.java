package damian.nanodegree.google.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by robert_damian on 17.02.2018.
 */

public class MovieDBContract {

    public static final String CONTENT_AUTHORITY = "damian.nanodegree.google.popularmovies";
    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_RELEASE_DATE = "release_date";
    }

}
