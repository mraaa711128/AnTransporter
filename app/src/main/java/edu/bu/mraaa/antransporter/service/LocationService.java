package edu.bu.mraaa.antransporter.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import edu.bu.mraaa.antransporter.BuildConfig;


/**
 * Created by mraaa711128 on 12/13/14.
 */
public class LocationService extends Object implements LocationListener {

    static LocationService instance;

    static final long MIN_TIME_REFRESH_LOCATION = 60 * 1000;
    static final float MIN_DISTANCE_REFRESH_LOCATION = 1;

    Context mContext;
    LocationManager mLocMgr;
    Location mLocation;
    Boolean mGpsEnable;
    Boolean mCellEnable;

    public static LocationService sharedService(Context context) {
        if (instance == null) {
            instance = new LocationService(context);
        }
        return instance;
    }

    private LocationService(Context context) {
        mContext = context;
        mLocMgr = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (BuildConfig.DEBUG) {
            mLocation = new Location(LocationManager.GPS_PROVIDER);
            mLocation.setLatitude(42.348719);
            mLocation.setLongitude(-71.095105);
        }
    }

    public boolean isServiceAvailable() {
        mGpsEnable = mLocMgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
        mCellEnable = mLocMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return (mGpsEnable || mCellEnable);
    }

    public Location getLocation() {
        try {
            if (mCellEnable == true) {
                mLocMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_REFRESH_LOCATION,MIN_DISTANCE_REFRESH_LOCATION,this);
                Location newLocation = mLocMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (newLocation != null) {
                    mLocation = newLocation;
                }
            }
            if (mGpsEnable == true) {
                mLocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_REFRESH_LOCATION,MIN_DISTANCE_REFRESH_LOCATION,this);
                Location newLocation = mLocMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (newLocation != null) {
                    mLocation = newLocation;
                }
            }
            return mLocation;
        } catch (Exception e) {
            return mLocation;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mLocMgr.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            mLocation = location;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
    }

}
