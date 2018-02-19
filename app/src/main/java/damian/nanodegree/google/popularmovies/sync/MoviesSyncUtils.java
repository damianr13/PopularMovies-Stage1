package damian.nanodegree.google.popularmovies.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

import damian.nanodegree.google.popularmovies.data.MovieDBContract;

/**
 * Created by robert_damian on 17.02.2018.
 */

public class MoviesSyncUtils {
    public static final int SYNC_INTERVAL_HOURS = 24;
    public static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    public static final int SYNC_FLEX_SECONDS = SYNC_INTERVAL_SECONDS / 2;

    public static final String MOVIES_SYNC_JOB_TAG = "sync-movies";

    private static boolean sInitialized = false;

    static void scheduleFirebasJob(@NonNull final Context context) {
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher jobDispatcher = new FirebaseJobDispatcher(driver);

        Job moviesSyncJob = jobDispatcher.newJobBuilder()
                .setService(MoviesFirebaseJobService.class)
                .setTag(MOVIES_SYNC_JOB_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setTrigger(Trigger.executionWindow(SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEX_SECONDS))
                .setReplaceCurrent(true)
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .build();
        jobDispatcher.schedule(moviesSyncJob);
    }

    synchronized public static void initialize(@NonNull final Context context) {
        if (sInitialized) {
            return ;
        }
        sInitialized = true;

        scheduleFirebasJob(context);

        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri moviesUri = MovieDBContract.MovieEntry.CONTENT_URI;
                String[] projection = new String[]{MovieDBContract.MovieEntry._ID};

                Cursor cursor = context.getContentResolver().query(moviesUri,
                        projection,
                        null,
                        null,
                        null);

                if (cursor == null || cursor.getCount() == 0) {
                    startImmediateSync(context);
                }
                cursor.close();
            }
        });
        checkForEmpty.run();
    }

    public static void startImmediateSync(@NonNull Context context) {
        Intent intentToStartSync = new Intent(context, MoviesSyncIntentService.class);
        context.startService(intentToStartSync);
    }
}
