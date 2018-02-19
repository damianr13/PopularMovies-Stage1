package damian.nanodegree.google.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import static damian.nanodegree.google.popularmovies.data.MovieDBContract.MovieEntry;
/**
 * Created by robert_damian on 17.02.2018.
 */

public class MovieDBHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "movies.db";

    public static final int DATABASE_VERSION = 1;

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
            MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            MovieEntry.COLUMN_TITLE + " VARCHAR(255) NOT NULL, " +
            MovieEntry.COLUMN_IMAGE + " VARCHAR(255) NOT NULL, " +
            MovieEntry.COLUMN_PLOT + " TEXT NOT NULL, " +
            MovieEntry.COLUMN_RATING + " REAL NOT NULL," +
            MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL," +
            MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //TODO: complete this when upgrading the database
    }
}
