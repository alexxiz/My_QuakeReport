package com.example.android.quakereport;

import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Created by Alexxiz on 22/08/2017.
 */

public class Word {
    //place holder variable for each word
    private  String mPlace;
    /** Time of the earthquake */
    private long mTimeInMilliseconds;
    /** Magnitude of the earthquake */
    private double mMagnitude;
    // url of the website
    private  String mUrl;



    //constructor for the Word class
    public Word (double mag, String place,long timeInMilliseconds, String url) {
        mMagnitude = mag;
        mPlace = place;
        mTimeInMilliseconds = timeInMilliseconds;
        mUrl = url;
    }
    //getting the words contained in each method
    public double getmMag() {
        return mMagnitude;
    }
    public String getPlace() {
        return mPlace;
    }
    public long getTimeInMilliseconds() {
        return mTimeInMilliseconds;
    }
    public String getUrl() {return mUrl;}
}
