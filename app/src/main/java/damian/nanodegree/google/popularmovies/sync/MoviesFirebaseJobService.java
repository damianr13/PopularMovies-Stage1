package damian.nanodegree.google.popularmovies.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by robert_damian on 17.02.2018.
 */

public class MoviesFirebaseJobService extends JobService{
    private AsyncTask<Void, Void, Void> mAsyncTask;

    @Override
    public boolean onStartJob(final JobParameters job) {
        mAsyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getApplicationContext();
                MoviesSyncTask.syncMovies(context);
                jobFinished(job, false);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(job, false);
            }
        };

        mAsyncTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
        }
        return true;
    }
}
