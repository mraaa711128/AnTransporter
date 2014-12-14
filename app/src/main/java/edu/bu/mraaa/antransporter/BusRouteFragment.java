package edu.bu.mraaa.antransporter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.bu.mraaa.antransporter.api.MbtaService;
import edu.bu.mraaa.antransporter.api.MbtaServiceDelegate;
import edu.bu.mraaa.antransporter.db.MbtaDbService;

/**
 * Created by mraaa711128 on 11/7/14.
 */
public class BusRouteFragment extends ListFragment implements MbtaServiceDelegate {

    ProgressDialog progDialog;

    BusRouteAdapter busAdapter;
    ArrayList<JSONObject> busRoutes;

    ListView viewBusRouteList;

    public BusRouteFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.title_fragment_busroute_list);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragBusRouteList = inflater.inflate(R.layout.fragment_busroute_list, container, false);
        viewBusRouteList = (ListView)fragBusRouteList.findViewById(android.R.id.list);

/*
        MbtaService service = MbtaService.sharedService();
        MbtaDbService dbService = MbtaDbService.sharedService(this.getActivity().getApplicationContext());

        Bundle getItem = getArguments();
        JSONObject objNearStop = null;
        try {
            if (getItem == null) {
                service.getRoutes(this);
            } else {
                busRoutes.clear();
                objNearStop = new JSONObject(getItem.getString("NearStop"));
                JSONArray arrStopRoutes = objNearStop.getJSONArray("route");
                for (int i = 0; i< arrStopRoutes.length(); i++) {
                    String routeId = arrStopRoutes.getJSONObject(i).getString("route_id");
                    String[] headSign = dbService.getRouteHeadSign(routeId);
                    arrStopRoutes.getJSONObject(i).put("route_headsign_out",headSign[0]);
                    arrStopRoutes.getJSONObject(i).put("route_headsign_in",headSign[1]);
                    busRoutes.add(arrStopRoutes.getJSONObject(i));
                }
            }
        } catch (JSONException e) {
        }

        busAdapter = new BusRouteAdapter(busRoutes);
        setListAdapter(busAdapter);
*/

        return fragBusRouteList;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MbtaService service = MbtaService.sharedService();
        MbtaDbService dbService = MbtaDbService.sharedService(this.getActivity().getApplicationContext());

        Bundle getItem = getArguments();
        JSONObject objNearStop = null;
        try {
            if (getItem == null) {
                if (busRoutes == null) {
                    busRoutes = new ArrayList<JSONObject>();
                    service.getRoutes(this);
                }
            } else {
                if (busRoutes == null) {
                    busRoutes = new ArrayList<JSONObject>();
                    objNearStop = new JSONObject(getItem.getString("NearStop"));
                    JSONArray arrStopRoutes = objNearStop.getJSONArray("route");
                    for (int i = 0; i < arrStopRoutes.length(); i++) {
                        String routeId = arrStopRoutes.getJSONObject(i).getString("route_id");
                        String[] headSign = dbService.getRouteHeadSign(routeId);
                        arrStopRoutes.getJSONObject(i).put("route_headsign_out", headSign[0]);
                        arrStopRoutes.getJSONObject(i).put("route_headsign_in", headSign[1]);
                        busRoutes.add(arrStopRoutes.getJSONObject(i));
                    }
                }
            }
        } catch (JSONException e) {
        }

        busAdapter = new BusRouteAdapter(busRoutes);
        busAdapter.setNotifyOnChange(true);
        setListAdapter(busAdapter);

        viewBusRouteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle setItem = new Bundle();
                String busRoute = busRoutes.get(position).toString();
                setItem.putString("BusRoute",busRoute);

                FragmentManager fm = getFragmentManager();
                Fragment fragment = new BusRouteStopFragment();
                fragment.setArguments(setItem);
                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.anim.fragment_slide_in_from_right,R.anim.fragment_slide_out_to_left);
                ft.replace(R.id.container,fragment,"fragmentBusRouteStop");
                ft.addToBackStack("fragmentBusRouteStop");
                ft.commit();
            }
        });
    }

    @Override
    public void didQueryServiceBegin(MbtaService.ServiceId serviceId) {
        progDialog = ProgressDialog.show(this.getActivity(),"Loading Data","Bus Route Data Loading ...");
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
     }

    @Override
    public void didQueryServiceSuccess(MbtaService.ServiceId serviceId, JSONObject result) {
        //progDialog.setMessage("Load Finished ...");
        //progDialog.dismiss();
        new AsyncTask<String, Long, Integer>() {
            @Override
            protected Integer doInBackground(String... params) {
                try {
                    MbtaDbService dbService = MbtaDbService.sharedService(getActivity().getApplicationContext());
                    JSONObject result = new JSONObject(params[0]);
                    busRoutes.clear();
                    JSONArray arrMode = result.getJSONArray("mode");
                    for (int i = 0; i< arrMode.length(); i++) {
                        String routeType = arrMode.getJSONObject(i).getString("route_type");
                        if (routeType.equals("3")) {
                            JSONArray arrRoutes = arrMode.getJSONObject(i).getJSONArray("route");
                            for (int j = 0; j< arrRoutes.length(); j++) {
                                String routeId = arrRoutes.getJSONObject(j).getString("route_id");
                                String[] headSign = dbService.getRouteHeadSign(routeId);
                                arrRoutes.getJSONObject(j).put("route_headsign_out",headSign[0]);
                                arrRoutes.getJSONObject(j).put("route_headsign_in",headSign[1]);

                                if (arrRoutes.getJSONObject(j).has("route_hide")) {
                                    String routeHide = arrRoutes.getJSONObject(j).getString("route_hide");
                                    if (routeHide.equals("true")) {
                                        //Not add in busRoutes
                                    } else {
                                        busRoutes.add(arrRoutes.getJSONObject(j));
                                    }
                                } else {
                                    busRoutes.add(arrRoutes.getJSONObject(j));
                                }
                            }
                            break;
                        }
                    }
                } catch (JSONException e) {

                }
                return 0;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                BusRouteAdapter busAdp = new BusRouteAdapter(busRoutes);
                busAdp.setNotifyOnChange(true);
                setListAdapter(busAdp);
                viewBusRouteList.setVisibility(View.VISIBLE);
                if (progDialog != null) {progDialog.dismiss();}
            }
        }.execute(result.toString());

/*
        MbtaDbService dbService = MbtaDbService.sharedService(this.getActivity().getApplicationContext());
        try {
            busRoutes.clear();
            JSONArray arrMode = result.getJSONArray("mode");
            for (int i = 0; i< arrMode.length(); i++) {
                String routeType = arrMode.getJSONObject(i).getString("route_type");
                if (routeType.equals("3")) {
                    JSONArray arrRoutes = arrMode.getJSONObject(i).getJSONArray("route");
                    for (int j = 0; j< arrRoutes.length(); j++) {
                        String routeId = arrRoutes.getJSONObject(j).getString("route_id");
                        String[] headSign = dbService.getRouteHeadSign(routeId);
                        arrRoutes.getJSONObject(j).put("route_headsign_out",headSign[0]);
                        arrRoutes.getJSONObject(j).put("route_headsign_in",headSign[1]);

                        if (arrRoutes.getJSONObject(j).has("route_hide")) {
                            String routeHide = arrRoutes.getJSONObject(j).getString("route_hide");
                            if (routeHide.equals("true")) {
                                //Not add in busRoutes
                            } else {
                                busRoutes.add(arrRoutes.getJSONObject(j));
                            }
                        } else {
                            busRoutes.add(arrRoutes.getJSONObject(j));
                        }
                    }
                    break;
                }
            }
        } catch (JSONException e) {

        }
        BusRouteAdapter busAdp = new BusRouteAdapter(busRoutes);
        busAdp.setNotifyOnChange(true);
        setListAdapter(busAdp);
        viewBusRouteList.setVisibility(View.VISIBLE);
        if (progDialog != null) {progDialog.dismiss();}
*/
    }

    @Override
    public void didQueryServiceFail(MbtaService.ServiceId serviceId, Error error) {
        busRoutes = new ArrayList<JSONObject>();
        BusRouteAdapter busAdp = new BusRouteAdapter(busRoutes);
        setListAdapter(busAdp);
        if (progDialog != null) {progDialog.dismiss();}
    }

    @Override
    public void didQueryServiceProgress(MbtaService.ServiceId serviceId, Long progress) {

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    }


    private class BusRouteAdapter extends ArrayAdapter<JSONObject> {

        public BusRouteAdapter(ArrayList<JSONObject> busRoute) {
            super(getActivity(),R.layout.fragment_busroute_list,busRoute);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.fragment_busroute_list_item,null);
            }

            JSONObject busRoute = getItem(position);
            TextView txtRouteCode = (TextView)convertView.findViewById(R.id.txtRouteCode);
            //TextView txtRouteDesc = (TextView)convertView.findViewById(R.id.txtRouteDesc);
            TextView txtInHeadSign = (TextView)convertView.findViewById(R.id.txtInHeadSign);
            TextView txtOutHeadSIgn = (TextView)convertView.findViewById(R.id.txtOutHeadSign);
            // Set View Property Here !!
            try {
                txtRouteCode.setText(busRoute.getString("route_id"));
                //txtRouteDesc.setText(busRoute.getString("route_name"));
                txtInHeadSign.setText(busRoute.getString("route_headsign_in"));
                txtOutHeadSIgn.setText(busRoute.getString("route_headsign_out"));
            } catch (JSONException e) {
                txtRouteCode.setText("");
                //txtRouteDesc.setText("");
                txtInHeadSign.setText("");
                txtOutHeadSIgn.setText("");
            }

            return convertView;
        }

    }
}
