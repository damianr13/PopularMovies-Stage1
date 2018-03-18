package damian.nanodegree.google.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import org.parceler.Parcel;

import java.util.ConcurrentModificationException;

/**
 * Created by robert_damian on 17.02.2018.
 */

@Parcel
public class Movie {
    /**
     * URL of the movie poster
     */
    private int id;
    private String imagePath;
    private String title;
    private String plot;
    private double rating;
    private double popularity;
    private String releaseDate;
    private boolean isFavorite;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        if (imagePath.startsWith("/")) {
            imagePath = imagePath.substring(1, imagePath.length());
        }

        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(Context context, boolean favorite) {
        isFavorite = favorite;
        ContentResolver contentResolver = context.getContentResolver();

        syncDatabase(contentResolver);
    }

    public void _setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public boolean isStoredInDatabase(Context context) {
        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor = contentResolver.query(MovieDBContract.MovieEntry.CONTENT_URI,
                null,
                MovieDBContract.MovieEntry._ID + "=?",
                new String[]{String.valueOf(id)},
                null);

        return cursor != null && cursor.getCount() > 0;
    }

    private void syncDatabase(ContentResolver contentResolver) {
        if (isFavorite) {
            storeToDatabase(contentResolver);
        }
        else {
            deleteFromDatabase(contentResolver);
        }
    }

    private void storeToDatabase(ContentResolver contentResolver) {
        contentResolver.insert(MovieDBContract.MovieEntry.CONTENT_URI,
                getContentValues());
    }

    private void deleteFromDatabase(ContentResolver contentResolver) {
        contentResolver.delete(MovieDBContract.MovieEntry.CONTENT_URI,
                MovieDBContract.MovieEntry._ID + "=?",
                new String[]{String.valueOf(id)});
    }

    ContentValues getContentValues() {
        ContentValues result = new ContentValues();

        result.put(MovieDBContract.MovieEntry._ID, id);
        result.put(MovieDBContract.MovieEntry.COLUMN_IMAGE, imagePath);
        result.put(MovieDBContract.MovieEntry.COLUMN_PLOT, plot);
        result.put(MovieDBContract.MovieEntry.COLUMN_POPULARITY, popularity);
        result.put(MovieDBContract.MovieEntry.COLUMN_RATING, rating);
        result.put(MovieDBContract.MovieEntry.COLUMN_TITLE, title);
        result.put(MovieDBContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);

        return result;
    }

    public static Movie buildFromCursor(@NonNull Cursor cursor) {
        Movie result = new Movie();

        result.setId(cursor.getInt(
                cursor.getColumnIndex(MovieDBContract.MovieEntry._ID)));
        result.setImagePath(cursor.getString(
                cursor.getColumnIndex(MovieDBContract.MovieEntry.COLUMN_IMAGE)));
        result.plot = cursor.getString(
                cursor.getColumnIndex(MovieDBContract.MovieEntry.COLUMN_PLOT));
        result.title = cursor.getString(
                cursor.getColumnIndex(MovieDBContract.MovieEntry.COLUMN_TITLE));
        result.rating = cursor.getDouble(
                cursor.getColumnIndex(MovieDBContract.MovieEntry.COLUMN_RATING));
        result.popularity = cursor.getDouble(
                cursor.getColumnIndex(MovieDBContract.MovieEntry.COLUMN_POPULARITY));
        result.releaseDate = cursor.getString(
                cursor.getColumnIndex(MovieDBContract.MovieEntry.COLUMN_RELEASE_DATE));

        //All the movies in the database are favorites
        result.isFavorite = true;

        return result;
    }

}
