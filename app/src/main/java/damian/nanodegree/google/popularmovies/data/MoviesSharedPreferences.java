package damian.nanodegree.google.popularmovies.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import damian.nanodegree.google.popularmovies.MainActivity;
import damian.nanodegree.google.popularmovies.R;

/**
 * Created by robert_damian on 18.03.2018.
 */

public class MoviesSharedPreferences {

    private SharedPreferences mSharedPreferences;
    private Activity mActivity;

    public MoviesSharedPreferences(Activity activity) {
        mActivity = activity;
        mSharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
    }

    public void setPreferedLoaderId(int loaderId) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(
                mActivity.getResources().getString(R.string.selected_sort_order_key),
                loaderId);
        editor.apply();
    }

    public int getPreferedLoaderId(int defaultValue) {
        return mSharedPreferences.getInt(
                mActivity.getResources().getString(R.string.selected_sort_order_key),
                defaultValue);
    }
}
