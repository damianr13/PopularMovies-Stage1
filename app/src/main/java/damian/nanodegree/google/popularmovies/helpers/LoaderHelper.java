package damian.nanodegree.google.popularmovies.helpers;

import android.content.Context;
import android.util.Log;
import android.view.View;

import javax.xml.transform.Result;

/**
 * Created by robert_damian on 17.03.2018.
 */

public class LoaderHelper {

    public static final String LOADER_HELPER_TAG = "LoaderHelper";

    private View mLoadingView;
    private View mErrorView;
    private View mDisplayedResultsView;
    private ResultsDisplayer mResultDisplayer;
    private int mHelperId;

    private LoaderHelper(View loadingView, View errorView, View displayedResultsView,
                         ResultsDisplayer resultDisplayer, int helperId) {
        this.mLoadingView = loadingView;
        this.mErrorView = errorView;
        this.mDisplayedResultsView = displayedResultsView;
        this.mResultDisplayer = resultDisplayer;
        this.mHelperId = helperId;
    }

    public void loadSucceeded(Object result) {
        mErrorView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mDisplayedResultsView.setVisibility(View.VISIBLE);

        if (mResultDisplayer != null) {
            mResultDisplayer.displayResults(mHelperId, result);
        }
    }

    public void loadFailed() {
        mDisplayedResultsView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
    }

    public void loadStarted() {
        mDisplayedResultsView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);
    }

    public static interface ResultsDisplayer {
        public void displayResults(int helperId, Object result);
    }

    public static class LoaderHelperBuilder {
        private View mLoadingView = null;
        private View mErrorView = null;
        private View mDisplayedResultsView = null;
        private ResultsDisplayer mResultsDisplayer = null;
        private int mHelperId = -1;

        public LoaderHelperBuilder setLoadingView(View view) {
            mLoadingView = view;
            return this;
        }

        public LoaderHelperBuilder setErrorView (View view) {
            mErrorView = view;
            return this;
        }

        public LoaderHelperBuilder setDisplayedResultsView(View view) {
            mDisplayedResultsView = view;
            return this;
        }

        public LoaderHelperBuilder setResultDisplayer(ResultsDisplayer resultDisplayer) {
            mResultsDisplayer = resultDisplayer;
            return this;
        }

        public LoaderHelperBuilder setHelperId(int helperId) {
            mHelperId = helperId;
            return this;
        }

        public LoaderHelper build(Context context) {
            if (mLoadingView == null) {
                mLoadingView = new View(context);
                Log.w(LOADER_HELPER_TAG, "Loading view is not set!");
            }

            if (mErrorView == null) {
                mErrorView = new View(context);
                Log.w(LOADER_HELPER_TAG, "Error view is not set!");
            }

            if (mDisplayedResultsView == null) {
                mDisplayedResultsView = new View(context);
                Log.w(LOADER_HELPER_TAG, "View for displayed results is not set!");
            }

            if (mResultsDisplayer == null) {
                Log.w(LOADER_HELPER_TAG, "Callback for displaying the results is not set!");
            }

            if (mHelperId == -1) {
                mHelperId = 1;
                Log.w(LOADER_HELPER_TAG, "Helper id is not set, providing default value");
            }

            return new LoaderHelper(mLoadingView, mErrorView, mDisplayedResultsView,
                    mResultsDisplayer, mHelperId);
        }
    }
}
