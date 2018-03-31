package com.example.android.quakereport;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.quakereport.EarthquakeActivity.LOG_TAG;

/**
 * Created by Alexxiz on 26/03/2018.
 */

public class EarthquakeLoader extends AsyncTaskLoader <ArrayList<Word>> {

    // We hold a reference to the Loader’s data here.
    private ArrayList<Word> mData;
    /** Query URL */
    private String mUrls;
    private static final String LOG_TAG = EarthquakeLoader.class.getName();
    public EarthquakeLoader(Context context, String url) {
        super(context);
        // TODO: Finish implementing this constructor
        mUrls = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i( LOG_TAG, "TEST: calling onStartLoading()...");
        forceLoad();
    }

    @Override
    public ArrayList<Word> loadInBackground() {
        Log.i( LOG_TAG, "TEST: calling loadInBackground()...");
        // Don't perform the request if there are no URLs, or the first URL is null.
        if (mUrls == null) {
            return null;
        }
        ArrayList<Word> result = QueryUtils.fetchEarthquakeData(mUrls);
        return result;
    }

    }

