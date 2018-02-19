package damian.nanodegree.google.popularmovies.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by robert_damian on 17.02.2018.
 */

public class MoviesSyncIntentService extends IntentService{
    public MoviesSyncIntentService() {
        super("MoviesSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        MoviesSyncTask.syncMovies(this);
    }
}
