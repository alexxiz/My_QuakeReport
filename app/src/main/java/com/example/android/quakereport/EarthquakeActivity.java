/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


import static com.example.android.quakereport.R.layout.earthquake_activity;

public class EarthquakeActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<Word>>,
        SwipeRefreshLayout.OnRefreshListener {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private static final int EARTHQUAKE_LOADER_ID = 1;
    /**
     * URL for earthquake data from the USGS dataset
     */
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=6&limit=10";
    private SwipeRefreshLayout refreshLayout;
    private ProgressBar spinner;
    private TextView earthquakeListView;
    /**
     * Adapter for the list of earthquakes
     */
    private WordAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(earthquake_activity);
        Log.i(LOG_TAG, "Test: EarthquakeActivity is being created");
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.mySwipe);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,

                android.R.color.holo_orange_light,

                android.R.color.holo_red_light);
        // Kick off an {@link AsyncTask} to perform the network request
        //EarthquakeAsyncTask task = new EarthquakeAsyncTask();
        // task.execute(USGS_REQUEST_URL);

        // Find a reference to the {@link ListView} in the layout
        final ListView earthquakeListView = (ListView) findViewById(R.id.list);


        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new WordAdapter(this, new ArrayList<Word>());


        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getSupportLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        Log.i(LOG_TAG, "TEST: calling initloader()");

        ConnectivityManager check = (ConnectivityManager)
                EarthquakeActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = check.getAllNetworkInfo();
        for (int i = 0; i < info.length; i++) {
            if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                Toast.makeText(EarthquakeActivity.this, "Internet is Connected", Toast.LENGTH_SHORT).show();
            }
        }

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);


        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                Word currentWordAdapter = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(currentWordAdapter.getUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
    }


    // checking for connection
    private boolean checkInternetConnection() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec
                = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() ==
                android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
            Toast.makeText(this, "Connected to Internet ", Toast.LENGTH_SHORT).show();
            return true;
        } else if (
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() ==
                                android.net.NetworkInfo.State.DISCONNECTED) {
            Toast.makeText(this, " Can't connect to Internet, please check your settings and try again ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // this restarts the oncreatLoader method i.e. making a new connection
                getSupportLoaderManager().restartLoader(EARTHQUAKE_LOADER_ID, null, EarthquakeActivity.this);
                refreshLayout.setRefreshing(false);
            }
        }, 5000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // this boolean is used to handle action bar content
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            //noinspection SimplifiableIfStatement
            case R.id.action_menu:
                Intent i = new Intent(this, AboutActivity.class);
                this.startActivity(i);
                return true;
            case R.id.menu_refresh:
                refreshLayout.setRefreshing(true);
                onRefresh();
                // getting the loader to restart connection
                getSupportLoaderManager().restartLoader(EARTHQUAKE_LOADER_ID, null, this);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public Loader<ArrayList<Word>> onCreateLoader(int i, Bundle bundle) {

        // TODO: Create a new loader for the given URL
        Log.i(LOG_TAG, "Test: calling onCreateLoader()");
        return new EarthquakeLoader(this, USGS_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Word>> loader, ArrayList<Word> earthquakes) {
        Log.i(LOG_TAG, "Loading as been completed");
        checkInternetConnection();
        // TODO: Update the UI with the result
        // Clear the adapter of previous earthquake data
        mAdapter.clear();
        // setting the list view to empty
        earthquakeListView = (TextView) findViewById(R.id.earthquake_empty_listView);
        spinner = (ProgressBar) findViewById(R.id.myProgress);
        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earthquakes != null && !earthquakes.isEmpty()) {
            mAdapter.addAll(earthquakes);
            // removing the progressbar when the earthquake data appears
            spinner.setVisibility(View.GONE);
            earthquakeListView.setVisibility(TextView.GONE);
        } else {

            earthquakeListView.setVisibility(TextView.VISIBLE);
        }
    }


    @Override
    public void onLoaderReset(Loader<ArrayList<Word>> loader) {
        // TODO: Loader reset, so we can clear out our existing data.

        Log.i(LOG_TAG, "Reset loader");
        mAdapter.clear();
    }
}

