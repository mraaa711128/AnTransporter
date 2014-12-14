package edu.bu.mraaa.antransporter;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import edu.bu.mraaa.antransporter.api.MbtaService;
import edu.bu.mraaa.antransporter.api.MbtaServiceDelegate;
import edu.bu.mraaa.antransporter.service.LocationService;

/**
 * Created by mraaa711128 on 12/13/14.
 */
public class BusRouteStopListFragment extends ListFragment implements MbtaServiceDelegate {

    JSONObject mBusRoute;
    JSONObject mBusStops;
    JSONObject mFoundTrip;
    JSONObject mRouteSchedule;

    Location mCurrentLocation;

    ArrayList<JSONObject> mStopList = new ArrayList<JSONObject>();

    ListView viewBusRouteStopList;
    ProgressDialog progDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragBusRouteStop = inflater.inflate(R.layout.fragment_busroutestop_list,container,false);
        viewBusRouteStopList = (ListView)fragBusRouteStop.findViewById(android.R.id.list);

        try {
            Bundle getItem = this.getArguments();
            String strBusRoute = getItem.getString("BusRoute");
            mBusRoute = new JSONObject(strBusRoute);
            String strBusStops = getItem.getString("BusStops");
            mBusStops = new JSONObject(strBusStops);

            MbtaService service = MbtaService.sharedService();
            LocationService locService = LocationService.sharedService(this.getActivity());
            if (locService.isServiceAvailable()) {
                Location newLoc = locService.getLocation();
                if (newLoc != null) {
                    mCurrentLocation = newLoc;
                    service.getStopsByLocation(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude(),this);
                } else {
                    if (BuildConfig.DEBUG) {
                        Time now = new Time();
                        now.setToNow();
                        Time tomorrow = new Time();
                        tomorrow.set(now.second,now.minute,now.hour,now.monthDay + 1,now.month,now.year);
                        Long epchotime = tomorrow.toMillis(true) / 1000L;
                        service.getScheduleByRoute(mBusRoute.getString("route_id"), mBusStops.getString("direction_id"),epchotime,null,null,this);
                    } else {
                        service.getScheduleByRoute(mBusRoute.getString("route_id"), mBusStops.getString("direction_id"),null,null,null,this);
                    }
                }
            } else {
                throw new Exception("Location Service is Unavailable !");
            }

        } catch (JSONException e) {
            showAlertDialogFragment("Error",e.getMessage());
        } catch (Exception e) {
            showAlertDialogFragment("Error",e.getMessage());
        }
        //return super.onCreateView(inflater, container, savedInstanceState);
        return fragBusRouteStop;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void didQueryServiceBegin(MbtaService.ServiceId serviceId) {
        if (serviceId == MbtaService.ServiceId.stopsbylocation) {
            progDialog = ProgressDialog.show(this.getActivity(), "Load Stops", "Bus Stops Loading ...");
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
    }

    @Override
    public void didQueryServiceSuccess(MbtaService.ServiceId serviceId, JSONObject result) {
        MbtaService service = MbtaService.sharedService();
        if (serviceId == MbtaService.ServiceId.stopsbylocation) {
            try {
                JSONObject currentStop = null;
                JSONArray nearByStops = result.getJSONArray("stop");
                JSONArray routeStops = mBusStops.getJSONArray("stop");
                for (int i = 0;i< nearByStops.length();i++) {
                    JSONObject nearByStop = nearByStops.getJSONObject(i);
                    for (int j = 0; j < routeStops.length();j ++) {
                        JSONObject routeStop = routeStops.getJSONObject(j);
                        if (routeStop.getString("stop_id").contentEquals(nearByStop.getString("stop_id"))) {
                            currentStop = routeStop;
                            if (currentStop != null) {
                                if (BuildConfig.DEBUG) {
                                    Time now = new Time();
                                    now.setToNow();
                                    Time tomorrow = new Time();
                                    tomorrow.set(now.second,now.minute,now.hour,now.monthDay + 1,now.month,now.year);
                                    Long epchotime = tomorrow.toMillis(true) / 1000L;
                                    service.getScheduleByStop(currentStop.getString("stop_id"), mBusRoute.getString("route_id"),
                                            mBusStops.getString("direction_id"),epchotime, null, null, this);
                                } else {
                                    service.getScheduleByStop(currentStop.getString("stop_id"), mBusRoute.getString("route_id"),
                                            mBusStops.getString("direction_id"), null, null, null, this);
                                }
                            } else {
                                throw new Exception("Can't find Stops around here !");
                            }
                            //break;
                            return;
                        }
                    }
                }
                if (BuildConfig.DEBUG) {
                    Time now = new Time();
                    now.setToNow();
                    Time tomorrow = new Time();
                    tomorrow.set(now.second,now.minute,now.hour,now.monthDay + 1,now.month,now.year);
                    Long epchotime = tomorrow.toMillis(true) / 1000L;
                    service.getScheduleByRoute(mBusRoute.getString("route_id"), mBusStops.getString("direction_id"),epchotime,null,null,this);
                } else {
                    service.getScheduleByRoute(mBusRoute.getString("route_id"), mBusStops.getString("direction_id"),null,null,null,this);
                }

/*
                if (currentStop != null) {
                    if (BuildConfig.DEBUG) {
                        Time now = new Time();
                        now.setToNow();
                        Time tomorrow = new Time();
                        tomorrow.set(now.second,now.minute,now.hour,now.monthDay + 1,now.month,now.year);
                        Long epchotime = tomorrow.toMillis(true) / 1000L;
                        service.getScheduleByStop(currentStop.getString("stop_id"), mBusRoute.getString("route_id"),
                                mBusStops.getString("direction_id"),epchotime, null, null, this);
                    } else {
                        service.getScheduleByStop(currentStop.getString("stop_id"), mBusRoute.getString("route_id"),
                                mBusStops.getString("direction_id"), null, null, null, this);
                    }
                } else {
                    throw new Exception("Can't find Stops around here !");
                }
*/
            } catch (JSONException e) {
                showAlertDialogFragment("Error",e.getMessage());
            } catch (Exception e) {
                showAlertDialogFragment("Error",e.getMessage());
            }
        } else if (serviceId == MbtaService.ServiceId.schedulebystop) {
            try {
                JSONObject currentTrip = null;
                JSONArray schModes = result.getJSONArray("mode");
                for (int i = 0; i < schModes.length(); i++) {
                    JSONObject schMode = schModes.getJSONObject(i);
                    if (schMode.getString("route_type").contentEquals("3")) {
                        JSONArray schRoutes = schMode.getJSONArray("route");
                        for (int j = 0; j < schRoutes.length(); j++) {
                            JSONObject schRoute = schRoutes.getJSONObject(j);
                            if (schRoute.getString("route_id").contentEquals(mBusRoute.getString("route_id"))) {
                                JSONArray schDirections = schRoute.getJSONArray("direction");
                                for (int k = 0; k < schDirections.length(); k++) {
                                    JSONObject schDirection = schDirections.getJSONObject(k);
                                    if (schDirection.getString("direction_id").contentEquals(mBusStops.getString("direction_id"))) {
                                        currentTrip = schDirection.getJSONArray("trip").getJSONObject(0);
                                        if (currentTrip != null) {
                                            mFoundTrip = currentTrip;
                                            service.getScheduleByTrip(mFoundTrip.getString("trip_id"), null, this);
                                        } else {
                                            throw new Exception("Schedule is Unavailable currently !");
                                        }
                                        //break;
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
/*
                if (currentTrip != null) {
                    mFoundTrip = currentTrip;
                    service.getScheduleByTrip(mFoundTrip.getString("trip_id"), null, this);
                } else {
                    throw new Exception("Schedule is Unavailable currently !");
                }
*/
            } catch (JSONException e) {
                showAlertDialogFragment("Error", e.getMessage());
            } catch (Exception e) {
                showAlertDialogFragment("Error", e.getMessage());
            }
        } else if (serviceId == MbtaService.ServiceId.schedulebyroute) {
            try {
                JSONObject currentTrip = null;
                JSONArray arrDirection = result.getJSONArray("direction");
                for (int i = 0; i < arrDirection.length(); i++) {
                    JSONObject schDirection = arrDirection.getJSONObject(i);
                    if (schDirection.getString("direction_id").contentEquals(mBusStops.getString("direction_id"))) {
                        currentTrip = schDirection.getJSONArray("trip").getJSONObject(0);
                        if (currentTrip != null) {
                            mFoundTrip = currentTrip;
                            service.getScheduleByTrip(mFoundTrip.getString("trip_id"), null, this);
                        } else {
                            throw new Exception("Schedule is Unavailable currently !");
                        }
                        //break;
                        return;
                    }
                }
                throw new Exception("Schedule is Unavailable currently !");
/*
                if (currentTrip != null) {
                    mFoundTrip = currentTrip;
                    service.getScheduleByTrip(mFoundTrip.getString("trip_id"), null, this);
                } else {
                    throw new Exception("Schedule is Unavailable currently !");
                }
*/
            } catch (JSONException e) {
                showAlertDialogFragment("Error", e.getMessage());
            } catch (Exception e) {
                showAlertDialogFragment("Error", e.getMessage());
            }
        } else if (serviceId == MbtaService.ServiceId.schedulebytrip) {
            try {
                mStopList.clear();

                mRouteSchedule = result;
                JSONArray schStops = mRouteSchedule.getJSONArray("stop");
                for (int i = 0; i < schStops.length(); i++) {
                    mStopList.add(schStops.getJSONObject(i));
                }

                BusRouteStopAdapter busRtStopAdp = new BusRouteStopAdapter(mStopList);
                busRtStopAdp.setNotifyOnChange(true);
                setListAdapter(busRtStopAdp);
                viewBusRouteStopList.setVisibility(View.VISIBLE);
                if (progDialog != null) {progDialog.dismiss();}
            } catch (JSONException e) {
                showAlertDialogFragment("Error",e.getMessage());
            } catch (Exception e) {
                showAlertDialogFragment("Error",e.getMessage());
            }
        }
    }

    private void showAlertDialogFragment(String title, String message) {
        if (progDialog != null) { progDialog.dismiss();}
        AlertDialogFragment altFrag = AlertDialogFragment.newDialog(title,message);
        altFrag.show(getFragmentManager(),"AlertDialog");
    }
    @Override
    public void didQueryServiceFail(MbtaService.ServiceId serviceId, Error error) {
        if (progDialog != null) {progDialog.dismiss();}
    }

    @Override
    public void didQueryServiceProgress(MbtaService.ServiceId serviceId, Long progress) {

    }

    private class BusRouteStopAdapter extends ArrayAdapter<JSONObject> {

        public BusRouteStopAdapter(ArrayList<JSONObject> busRouteStops) {
            super(getActivity(), R.layout.fragment_busroutestop_list, busRouteStops);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.fragment_busroutestop_list_item,parent,false);
            }
            TextView txtStopName = (TextView)convertView.findViewById(R.id.txtStopName);
            TextView txtETA = (TextView)convertView.findViewById(R.id.txtETA);
            TextView txtNextBus = (TextView)convertView.findViewById(R.id.txtNextBus);

            JSONObject busRouteStop = getItem(position);

            try {
                txtStopName.setText(busRouteStop.getString("stop_name"));

                Date timeETA = new Date(busRouteStop.getLong("sch_arr_dt") * 1000L);
                DateFormat formatETA = new SimpleDateFormat("HH:mm");
                txtETA.setText(formatETA.format(timeETA));

                if (busRouteStop.has("bus_arr_dt")) {
                    Date timeBus = new Date(busRouteStop.getLong("bus_arr_dt") * 1000L);
                    DateFormat formatBus = new SimpleDateFormat("mm:ss");
                    txtNextBus.setText(formatBus.format(timeBus));
                }
            } catch (JSONException e) {

            }
            //return super.getView(position, convertView, parent);
            return convertView;
        }
    }

/*
    private class FindTripId extends AsyncTask<String, Long, String> implements MbtaServiceDelegate {

        Context mContext;

        JSONObject mBusRoute;
        JSONObject mBusStops;
        JSONObject mNextTrip;

        Location mCurLocation;

        Boolean mFinished = false ;

        public void setContext(Context context) {
            mContext = context;
        }

        public void setBusRoute(JSONObject busRoute) {
            mBusRoute = busRoute;
        }

        public void setBusStops(JSONObject busStops) {
            mBusStops = busStops;
        }

        public void setLocation(Location location) {
            mCurLocation = location;
        }

        @Override
        protected String doInBackground(String... params) {

            while (mFinished == false) {
                try {
                    wait(1000);
                } catch (InterruptedException e) {

                }
            }
            String strNextTrip = "";
            if (mNextTrip != null) { strNextTrip = mNextTrip.toString();}

            //return null;
            return strNextTrip;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressDialog progDialog = ProgressDialog.show(mContext, "Load Stops", "Bus Stops Loading ...");
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!s.contentEquals("")) {
                try {
                    mFoundTrip = new JSONObject(s);
                } catch (JSONException e) {

                }
            }
        }

        @Override
        public void didQueryServiceBegin(MbtaService.ServiceId serviceId) {

        }

        @Override
        public void didQueryServiceSuccess(MbtaService.ServiceId serviceId, JSONObject result) {
        }

        @Override
        public void didQueryServiceFail(MbtaService.ServiceId serviceId, Error error) {
            mFinished = true;
        }

        @Override
        public void didQueryServiceProgress(MbtaService.ServiceId serviceId, Long progress) {

        }
    }
*/
}
