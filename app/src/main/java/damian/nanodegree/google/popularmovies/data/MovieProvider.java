package damian.nanodegree.google.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by robert_damian on 17.02.2018.
 */

public class MovieProvider extends ContentProvider{

    private static final int CODE_MOVIES = 100;
    private static final int CODE_SINGLE_MOVIE = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDBHelper mDbHelper;

    private static final String UNKNOWN_URI_MESSAGE = "Unknown uri: ";

    private static UriMatcher buildUriMatcher() {
        UriMatcher result = new UriMatcher(UriMatcher.NO_MATCH);
        result.addURI(MovieDBContract.CONTENT_AUTHORITY, MovieDBContract.PATH_MOVIES, CODE_MOVIES);
        result.addURI(MovieDBContract.CONTENT_AUTHORITY, MovieDBContract.PATH_MOVIES + "/#",
                CODE_SINGLE_MOVIE);
        return result;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        SQLiteDatabase moviesDatabase = mDbHelper.getReadableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES:
                return moviesDatabase.query(MovieDBContract.MovieEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
            case CODE_SINGLE_MOVIE:
                selectionArgs = new String[]{uri.getLastPathSegment()};
                return moviesDatabase.query(MovieDBContract.MovieEntry.TABLE_NAME, projection,
                        MovieDBContract.MovieEntry._ID + " = ?",
                        selectionArgs, null, null, sortOrder);
            default:
                throw new UnsupportedOperationException(UNKNOWN_URI_MESSAGE + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        //TODO: not implemented in Sunshine. See if we need it here
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase moviesDatabase = mDbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIES:
                long insertedId = moviesDatabase.insert(MovieDBContract.MovieEntry.TABLE_NAME,
                        null, contentValues);
                return uri.buildUpon().appendPath("/" + insertedId).build();
            case CODE_SINGLE_MOVIE:
                throw new UnsupportedOperationException("Inserting at specific id not supported");
            default:
                throw new UnsupportedOperationException(UNKNOWN_URI_MESSAGE + uri);
        }
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase moviesDatabase = mDbHelper.getWritableDatabase();
        switch(sUriMatcher.match(uri)) {
            case CODE_MOVIES:
                int rowsCount;
                try{
                    moviesDatabase.beginTransaction();
                    rowsCount = insertAllValuesInDatabase(moviesDatabase, values);
                    moviesDatabase.setTransactionSuccessful();
                } finally {
                    moviesDatabase.endTransaction();
                }

                if (rowsCount > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    private int insertAllValuesInDatabase(SQLiteDatabase db, ContentValues[] values) {
        int result = 0;
        for (ContentValues contentValues : values) {
            long insertedId = db.insert(MovieDBContract.MovieEntry.TABLE_NAME,
                    null, contentValues);
            if (insertedId != -1) {
                result++;
            }
        }

        return result;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        // Prevent from deleting the table. Delete all the rows inside instead
        if (selection == null) {
            selection = "1";
        }

        SQLiteDatabase moviesDatabase = mDbHelper.getWritableDatabase();
        switch(sUriMatcher.match(uri)) {
            case CODE_MOVIES:
                return moviesDatabase.delete(MovieDBContract.MovieEntry.TABLE_NAME,
                        selection, selectionArgs);
            case CODE_SINGLE_MOVIE:
                selectionArgs = new String[]{uri.getLastPathSegment()};
                return moviesDatabase.delete(MovieDBContract.MovieEntry.TABLE_NAME,
                        MovieDBContract.MovieEntry._ID + " = ?",
                        selectionArgs);
            default:
                throw new UnsupportedOperationException(UNKNOWN_URI_MESSAGE + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String s, @Nullable String[] strings) {
        //TODO: not implemented in Sunshine. See if we need it here
        return 0;
    }
}
