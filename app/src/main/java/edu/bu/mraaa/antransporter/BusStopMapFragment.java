package edu.bu.mraaa.antransporter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import javax.security.auth.callback.Callback;

import edu.bu.mraaa.antransporter.api.MbtaService;
import edu.bu.mraaa.antransporter.api.MbtaServiceDelegate;
import edu.bu.mraaa.antransporter.service.LocationService;

/**
 * Created by mraaa711128 on 12/13/14.
 */
public class BusStopMapFragment extends Fragment implements MbtaServiceDelegate, OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    GoogleMap mMap;
    MapView mMapView;
    ProgressDialog progDialog;

    HashMap<Marker,JSONObject> mMarkers;
    HashMap<String,JSONObject> mNearStops;

    Location mCurrentLocation;

    Bundle mSavedState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) { mMapView.onResume();}
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {mMapView.onPause();}
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) { mMapView.onDestroy();}
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSavedState = savedInstanceState;

        View fragBusStopMap = inflater.inflate(R.layout.fragment_map,container,false);

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            Toast.makeText(getActivity(),"Return Code = " + String.valueOf(resultCode),Toast.LENGTH_LONG);
            GooglePlayServicesUtil.getErrorDialog(resultCode,getActivity(),1);
        }

/*
        //MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
        }
        mapFragment.getMapAsync(this);
*/
        try {
//            InitMap mapInit = new InitMap(fragBusStopMap,savedInstanceState);
//            mapInit.execute(this);
            //MapsInitializer.initialize(getActivity());
            progDialog = ProgressDialog.show(getActivity(),"Prepare Map","Map View Preparing ...");
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mMapView = (MapView) fragBusStopMap.findViewById(R.id.map);
            mMapView.onCreate(mSavedState);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        } catch (Exception e) {
            showAlertDialogFragment("Error",e.getMessage());
        }

        //return super.onCreateView(inflater, container, savedInstanceState);
        return fragBusStopMap;
    }

/*
    private class InitMap extends AsyncTask<OnMapReadyCallback,Long,Integer> {

        View fragment;
        Bundle savedState;
        OnMapReadyCallback mCallback;

        public InitMap(View v, Bundle savedInstanceState) {
            fragment = v;
            savedState = savedInstanceState;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDialog = ProgressDialog.show(getActivity(),"Prepare Map","Map View Preparing ...");
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        @Override
        protected Integer doInBackground(OnMapReadyCallback... params) {
            try {
                mCallback = params[0];
                MapsInitializer.initialize(getActivity());
                mMapView = (MapView) fragment.findViewById(R.id.map);
                mMapView.onCreate(savedState);
                return 0;
            } catch (Exception e) {
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (progDialog != null) {progDialog.dismiss();}
            if (integer == 0) {
                mMapView.getMapAsync(mCallback);
                progDialog = ProgressDialog.show(getActivity(),"Prepare Map","Map View Preparing ...");
                progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            } else {
                showAlertDialogFragment("Error","Map View Initialize Fail !");
            }
        }
    }
*/

    private void showAlertDialogFragment(String title, String message) {
        if (progDialog != null) { progDialog.dismiss();}
        AlertDialogFragment altFrag = AlertDialogFragment.newDialog(title,message);
        altFrag.show(getFragmentManager(),"AlertDialog");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //mMapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            if (progDialog != null) { progDialog.dismiss();}
            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.setOnInfoWindowClickListener(this);

            mMarkers = new HashMap<Marker, JSONObject>();
            mNearStops = new HashMap<String, JSONObject>();

            LocationService locService = LocationService.sharedService(this.getActivity());
            if (locService.isServiceAvailable()) {
                Location newLoc = locService.getLocation();
                if (newLoc != null) {
                    mCurrentLocation = newLoc;
                    MbtaService service = MbtaService.sharedService();
                    service.getStopsByLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), this);
                } else {
                    throw new Exception("Can't find Current Location !");
                }
            } else {
                throw new Exception("Location Service is Unavailable !");
            }
        } catch (Exception e) {
            showAlertDialogFragment("Error",e.getMessage());
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        JSONObject nearStop = mMarkers.get(marker);
        String strNearStop = nearStop.toString();
        Bundle setItem = new Bundle();
        setItem.putString("NearStop",strNearStop);

        FragmentManager fm = getFragmentManager();
        Fragment fragment = new BusRouteFragment();
        fragment.setArguments(setItem);
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_slide_in_from_right,R.anim.fragment_slide_out_to_left);
        ft.replace(R.id.container, fragment, "fragmentNearStopBusRoutes");
        ft.addToBackStack("fragmentNearStopBusRoutes");
        ft.commit();
    }

    @Override
    public void didQueryServiceBegin(MbtaService.ServiceId serviceId) {
        if (serviceId == MbtaService.ServiceId.stopsbylocation) {
            progDialog = ProgressDialog.show(this.getActivity(), "Load Map Stops", "Map Stops Loading ...");
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
    }

    @Override
    public void didQueryServiceSuccess(MbtaService.ServiceId serviceId, JSONObject result) {
        MbtaService service = MbtaService.sharedService();
        if (serviceId == MbtaService.ServiceId.stopsbylocation) {
            try {
                mNearStops.clear();
                mMarkers.clear();
                JSONArray arrStops = result.getJSONArray("stop");
                for (int i = 0; i< arrStops.length(); i++) {
                    JSONObject objStop = arrStops.getJSONObject(i);
                    String objStopId = objStop.getString("stop_id");
                    mNearStops.put(objStopId,objStop);
                    service.getRoutesByStop(objStopId,this);
                }
                if (arrStops.length() <= 0) {
                    showAlertDialogFragment("Warning","There isn't any Bus Stops around here !");
                }
            } catch (JSONException e) {
                showAlertDialogFragment("Error",e.getMessage());
            }
        } else if (serviceId == MbtaService.ServiceId.routesbystop) {
            try {
                JSONObject objRoutes = result;
                String objStopId = objRoutes.getString("stop_id");
                JSONArray arrModes = objRoutes.getJSONArray("mode");
                for (int i = 0; i < arrModes.length(); i++) {
                    if (arrModes.getJSONObject(i).getString("route_type").contentEquals("3")) {
                        JSONObject objStop = mNearStops.get(objStopId);
                        JSONArray arrRoutes = arrModes.getJSONObject(i).getJSONArray("route");
                        LatLngBounds.Builder boundBuilder = new LatLngBounds.Builder();
                        String infoTitle = objStop.getString("stop_name") + "\n";
                        for (int j = 0; j < arrRoutes.length(); j++) {
                            infoTitle = infoTitle +
                                        arrRoutes.getJSONObject(j).getString("route_id") + "\n";
                        }
                        objStop.put("route",arrRoutes);
                        double lat = objStop.getDouble("stop_lat");
                        double lon = objStop.getDouble("stop_lon");
                        LatLng pos = new LatLng(lat,lon);
                        MarkerOptions opt = new MarkerOptions().position(pos).title(infoTitle);
                        Marker mk = mMap.addMarker(opt);
                        mMarkers.put(mk,objStop);
                        boundBuilder.include(mk.getPosition());
                        LatLngBounds mapBound = boundBuilder.build();
                        //CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(mapBound,10);
                        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(mapBound.getCenter(),14);
                        mMap.moveCamera(cu);
                    }
                }
                if (progDialog != null) {progDialog.dismiss();}
            } catch (JSONException e) {
                showAlertDialogFragment("Error",e.getMessage());
            } catch (Exception e) {
                showAlertDialogFragment("Error",e.getMessage());
            }
        } else {

        }
    }

    @Override
    public void didQueryServiceFail(MbtaService.ServiceId serviceId, Error error) {

    }

    @Override
    public void didQueryServiceProgress(MbtaService.ServiceId serviceId, Long progress) {

    }

}
