package edu.bu.mraaa.antransporter;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import edu.bu.mraaa.antransporter.api.MbtaService;
import edu.bu.mraaa.antransporter.api.MbtaServiceDelegate;
import edu.bu.mraaa.antransporter.db.MbtaTabCalendar;
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
            service.getPredictionsByRoute(mBusRoute.getString("route_id"), null, null, this);
/*
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
*/

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
        if (serviceId == MbtaService.ServiceId.predictionsbyroute) {
            progDialog = ProgressDialog.show(this.getActivity(), "Load Stops", "Bus Stops Loading ...");
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
    }

    @Override
    public void didQueryServiceSuccess(MbtaService.ServiceId serviceId, JSONObject result) {
        try {
            MbtaService service = MbtaService.sharedService();

            if (serviceId == MbtaService.ServiceId.predictionsbyroute) {
                HashMap<String, JSONObject> hashStops = new HashMap<String,JSONObject>();

                JSONArray predDirections = result.getJSONArray("direction");
                JSONObject predDirection = null;
                if (predDirections.length() == 2) {
                    if (mBusStops.getString("direction_id").contentEquals("0")) {
                        predDirection = result.getJSONArray("direction").getJSONObject(0);
                    } else {
                        predDirection = result.getJSONArray("direction").getJSONObject(1);
                    }
                } else if (predDirections.length() == 1) {
                    predDirection = predDirections.getJSONObject(0);
                    if ((predDirection.getString("direction_id").contentEquals(mBusStops.getString("direction_id"))) == false) {
                        if (BuildConfig.DEBUG) {
                            Time now = new Time();
                            now.setToNow();
                            Time tomorrow = new Time();
                            tomorrow.set(now.second,now.minute,now.hour,now.monthDay + 1,now.month,now.year);
                            Long epchotime = tomorrow.toMillis(false) / 1000L;
                            service.getScheduleByRoute(mBusRoute.getString("route_id"), mBusStops.getString("direction_id"),epchotime,null,null,this);
                            return;
                        } else {
                            service.getScheduleByRoute(mBusRoute.getString("route_id"), mBusStops.getString("direction_id"),null,null,null,this);
                            return;
                        }
                    }
                } else {
                    if (BuildConfig.DEBUG) {
                        Time now = new Time();
                        now.setToNow();
                        Time tomorrow = new Time();
                        tomorrow.set(now.second,now.minute,now.hour,now.monthDay + 1,now.month,now.year);
                        Long epchotime = tomorrow.toMillis(false) / 1000L;
                        service.getScheduleByRoute(mBusRoute.getString("route_id"), mBusStops.getString("direction_id"),epchotime,null,null,this);
                        return;
                    } else {
                        service.getScheduleByRoute(mBusRoute.getString("route_id"), mBusStops.getString("direction_id"),null,null,null,this);
                        return;
                    }
                }

                mStopList.clear();

                JSONArray predTrips = predDirection.getJSONArray("trip");
                for (int i = predTrips.length() - 1; i >= 0; i--) {
                    JSONObject predTrip = predTrips.getJSONObject(i);
                    JSONArray predStops = predTrip.getJSONArray("stop");
                    for (int j = 0; j < predStops.length(); j++) {
                        JSONObject predStop = predStops.getJSONObject(j);
                        if (hashStops.containsKey(predStop.getString("stop_id"))) {
                            JSONObject hashStop = hashStops.get(predStop.getString("stop_id"));
                            String pred_pre_away = predStop.getString("pre_away");
                            String hash_pre_away = hashStop.getString("pre_away");
                            if (Integer.valueOf(pred_pre_away) < Integer.valueOf(hash_pre_away)) {
                                hashStop.put("sch_arr_dt", predStop.getString("sch_arr_dt"));
                                hashStop.put("sch_dep_dt", predStop.getString("sch_dep_dt"));
                                hashStop.put("pre_dt", predStop.getString("pre_dt"));
                                hashStop.put("pre_away", predStop.getString("pre_away"));
                                if (mStopList.contains(hashStop)) {
                                    int idx = mStopList.indexOf(hashStop);
                                    mStopList.get(idx).put("sch_arr_dt", predStop.getString("sch_arr_dt"));
                                    mStopList.get(idx).put("sch_dep_dt", predStop.getString("sch_dep_dt"));
                                    mStopList.get(idx).put("pre_dt", predStop.getString("pre_dt"));
                                    mStopList.get(idx).put("pre_away", predStop.getString("pre_away"));
                                }
                            }
                        } else {
                            hashStops.put(predStop.getString("stop_id"), predStop);
                            mStopList.add(predStop);
                        }
                    }
                }
                if (mStopList.size() > 0) {
                    BusRouteStopAdapter busRouteAdp = new BusRouteStopAdapter(mStopList);
                    busRouteAdp.setNotifyOnChange(true);
                    setListAdapter(busRouteAdp);
                    viewBusRouteStopList.setVisibility(View.VISIBLE);
                } else {
                    if (BuildConfig.DEBUG) {
                        Time now = new Time();
                        now.setToNow();
                        Time tomorrow = new Time();
                        tomorrow.set(now.second,now.minute,now.hour,now.monthDay + 1,now.month,now.year);
                        Long epchotime = tomorrow.toMillis(false) / 1000L;
                        service.getScheduleByRoute(mBusRoute.getString("route_id"), mBusStops.getString("direction_id"),epchotime,null,null,this);
                    } else {
                        service.getScheduleByRoute(mBusRoute.getString("route_id"), mBusStops.getString("direction_id"),null,null,null,this);
                    }
                }
            } else if (serviceId == MbtaService.ServiceId.schedulebyroute) {
                JSONArray schDirections = result.getJSONArray("direction");
                JSONObject schDirection = null;

                if (schDirections.length() == 2) {
                    if (mBusStops.getString("direction_id").contentEquals("0")) {
                        schDirection = schDirections.getJSONObject(0);
                    } else {
                        schDirection = schDirections.getJSONObject(1);
                    }
                } else if (schDirections.length() == 1) {
                    schDirection = schDirections.getJSONObject(0);
                    if ((schDirection.getString("direction_id").contentEquals(mBusStops.getString("direction_id"))) == false) {
                        service.getStopsByRoute(mBusRoute.getString("route_id"),this);
                        return;
                    }
                } else {
                    service.getStopsByRoute(mBusRoute.getString("route_id"),this);
                    return;
                }
                mStopList.clear();
                JSONArray predTrips = schDirection.getJSONArray("trip");
                if (predTrips.length() > 0) {
                    JSONObject predTrip = predTrips.getJSONObject(0);
                    JSONArray predStops = predTrip.getJSONArray("stop");
                    for (int j = 0; j < predStops.length(); j++) {
                        JSONObject predStop = predStops.getJSONObject(j);
                        predStop.put("pre_dt",predStop.getString("sch_arr_dt"));
                        predStop.put("pre_away","-1");
                        mStopList.add(predStop);
                    }
                }
                if (mStopList.size() > 0) {
                    BusRouteStopAdapter busRouteAdp = new BusRouteStopAdapter(mStopList);
                    busRouteAdp.setNotifyOnChange(true);
                    setListAdapter(busRouteAdp);
                    viewBusRouteStopList.setVisibility(View.VISIBLE);
                } else {
                    service.getStopsByRoute(mBusRoute.getString("route_id"),this);
                }
            } else if (serviceId == MbtaService.ServiceId.stopsbyroute) {
                JSONArray routeDirections = result.getJSONArray("direction");
                JSONObject routeDirection = null;

                if (routeDirections.length() == 2) {
                    if (mBusStops.getString("direction_id").contentEquals("0")) {
                        routeDirection = routeDirections.getJSONObject(0);
                    } else {
                        routeDirection = routeDirections.getJSONObject(1);
                    }
                } else if (routeDirections.length() == 1) {
                    routeDirection = routeDirections.getJSONObject(0);
                    if ((routeDirection.getString("direction_id").contentEquals(mBusStops.getString("direction_id"))) == false) {
                        throw new Exception("Bus Stops are not available currently !");
                    }
                } else {
                    throw new Exception("Bus Stops are not available currently !");
                }
                mStopList.clear();
                JSONArray routeStops = routeDirection.getJSONArray("stop");
                for (int i = 0; i< routeStops.length(); i++) {
                    JSONObject routeStop = routeStops.getJSONObject(i);
                    routeStop.put("sch_arr_dt","0");
                    routeStop.put("pre_dt","0");
                    routeStop.put("pre_away","-1");
                    mStopList.add(routeStop);
                }
                BusRouteStopAdapter busRouteAdp = new BusRouteStopAdapter(mStopList);
                busRouteAdp.setNotifyOnChange(true);
                setListAdapter(busRouteAdp);
                viewBusRouteStopList.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            showAlertDialogFragment("Error", e.getMessage());
        } catch (Exception e) {
            showAlertDialogFragment("Error", e.getMessage());
        } finally {
            if (progDialog != null) {progDialog.dismiss();}
        }
    }

    private void showAlertDialogFragment(String title, String message) {
        if (progDialog != null) { progDialog.dismiss();}
        AlertDialogFragment altFrag = AlertDialogFragment.newDialog(title,message);
        altFrag.show(getFragmentManager(),"AlertDialog");
    }
    @Override
    public void didQueryServiceFail(MbtaService.ServiceId serviceId, Error error) {
        if (serviceId == MbtaService.ServiceId.predictionsbyroute) {
            try {
                MbtaService service = MbtaService.sharedService();
                if (BuildConfig.DEBUG) {
                    Time now = new Time();
                    now.setToNow();
                    Time tomorrow = new Time();
                    tomorrow.set(now.second,now.minute,now.hour,now.monthDay + 1,now.month,now.year);
                    Long epchotime = tomorrow.toMillis(false) / 1000L;
                    service.getScheduleByRoute(mBusRoute.getString("route_id"), mBusStops.getString("direction_id"),epchotime,null,null,this);
                } else {
                    service.getScheduleByRoute(mBusRoute.getString("route_id"), mBusStops.getString("direction_id"),null,null,null,this);
                }
            } catch (JSONException e) {
                showAlertDialogFragment("Error",e.getMessage());
            }
        } else {
            if (progDialog != null) {
                progDialog.dismiss();
            }
            showAlertDialogFragment("Error", error.getMessage());
        }
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
            TextView txtSchedule = (TextView)convertView.findViewById(R.id.txtSchedule);
            TextView txtETA = (TextView)convertView.findViewById(R.id.txtETA);
            TextView txtNextBus = (TextView)convertView.findViewById(R.id.txtNextBus);
            ImageView imgBus = (ImageView)convertView.findViewById(R.id.imgBusSign);

            JSONObject busRouteStop = getItem(position);

            try {
                txtStopName.setText(busRouteStop.getString("stop_name"));


                Long schDt = busRouteStop.getLong("sch_arr_dt");
                if (schDt > 0) {
                    Date timeSchedule = new Date(schDt * 1000L);
                    DateFormat formatSchedule = new SimpleDateFormat("HH:mm");
                    txtSchedule.setText(formatSchedule.format(timeSchedule));
                } else {
                    txtSchedule.setText("");
                }

                Long preDt = busRouteStop.getLong("pre_dt");
                if (preDt > 0) {
                    Date timeETA = new Date(preDt * 1000L);
                    DateFormat formatETA = new SimpleDateFormat("HH:mm");
                    txtETA.setText(formatETA.format(timeETA));
                } else {
                    txtETA.setText("");
                }

                Long preAway = busRouteStop.getLong("pre_away");
                if (preAway >= 0) {
                    Date timeBus = new Date(preAway * 1000L);
                    DateFormat formatBus = new SimpleDateFormat("mm:ss");
                    txtNextBus.setText(formatBus.format(timeBus));
                } else {
                    txtNextBus.setText("");
                }

                if (preAway >= 0 && preAway <= 60) {
                    imgBus.setImageResource(R.drawable.imgfindmybus);
                } else {
                    imgBus.setImageBitmap(null);
                }
            } catch (JSONException e) {
                txtStopName.setText("");
                txtSchedule.setText("");
                txtETA.setText("");
                txtNextBus.setText("");
                imgBus.setImageBitmap(null);
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
