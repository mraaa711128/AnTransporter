package edu.bu.mraaa.antransporter;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import edu.bu.mraaa.antransporter.api.MbtaService;
import edu.bu.mraaa.antransporter.api.MbtaServiceDelegate;
import edu.bu.mraaa.antransporter.service.LocationService;

/**
 * Created by mraaa711128 on 12/12/14.
 */
public class BusRouteStopFragment extends Fragment implements MbtaServiceDelegate{

    ViewPager viewPager;
    TextView txtRouteCode;
    TextView txtRouteHeadSign;

    JSONObject mBusRoute;
    BusRouteStopPageAdapter mBusStopAdp;

    ProgressDialog progDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Bus Route Stops");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragBusRouteStop = inflater.inflate(R.layout.fragment_busroutestop,container,false);
        txtRouteCode = (TextView)fragBusRouteStop.findViewById(R.id.txtRouteCode);
        txtRouteHeadSign = (TextView)fragBusRouteStop.findViewById(R.id.txtRouteHeadSign);
        viewPager = (ViewPager)fragBusRouteStop.findViewById(R.id.viewPager);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
                try {
                    switch (i) {
                        case 0:
                            txtRouteHeadSign.setText(mBusRoute.getString("route_headsign_in"));
                            break;
                        case 1:
                            txtRouteHeadSign.setText(mBusRoute.getString("route_headsign_out"));
                            break;
                        default:
                            txtRouteHeadSign.setText("");
                            break;
                    }
                } catch (JSONException e) {
                }
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        try {
            Bundle getItem = this.getArguments();
            String strBusRoute = getItem.getString("BusRoute");
            mBusRoute = new JSONObject(strBusRoute);

            txtRouteCode.setText(mBusRoute.getString("route_id"));

            MbtaService service = MbtaService.sharedService();
            service.getStopsByRoute(mBusRoute.getString("route_id"), this);
            //service.getPredictionByRoute(mBusRoute.getString("route_id"),false,false,this);
        } catch (JSONException e) {
            mBusRoute = null;
        }
        //return super.onCreateView(inflater, container, savedInstanceState);
        return fragBusRouteStop;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void didQueryServiceBegin(MbtaService.ServiceId serviceId) {
        progDialog = ProgressDialog.show(this.getActivity(), "Load Stops Data", "Stops Data Loading ...");
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    @Override
    public void didQueryServiceSuccess(MbtaService.ServiceId serviceId, JSONObject result) {
        try {
            JSONArray routeDirection = result.getJSONArray("direction");
            mBusStopAdp = new BusRouteStopPageAdapter(this.getFragmentManager());
            mBusStopAdp.setBusRoute(mBusRoute);
            mBusStopAdp.setInBoundStops(routeDirection.getJSONObject(1));
            mBusStopAdp.setOutBoundStops(routeDirection.getJSONObject(0));
            viewPager.setAdapter(mBusStopAdp);
        } catch (JSONException e) {
            AlertDialogFragment altDialogFrag = AlertDialogFragment.newDialog("Error",e.getMessage());
            altDialogFrag.show(getFragmentManager(),"AlertDialog");
        }

        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    @Override
    public void didQueryServiceFail(MbtaService.ServiceId serviceId, Error error) {

        if (progDialog != null) {progDialog.dismiss();}
    }

    @Override
    public void didQueryServiceProgress(MbtaService.ServiceId serviceId, Long progress) {

    }

    private class BusRouteStopPageAdapter extends FragmentStatePagerAdapter {

        JSONObject mBusRoute;
        JSONObject mInStops;
        JSONObject mOutStops;

        public BusRouteStopPageAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setBusRoute(JSONObject busRoute) {
            mBusRoute = busRoute;
        }

        public void setInBoundStops(JSONObject inboundStops) {
            mInStops = inboundStops;
        }

        public void setOutBoundStops(JSONObject outboundStops) {
            mOutStops = outboundStops;
        }

        @Override
        public Fragment getItem(int i) {
            Bundle setItem = new Bundle();
            String strBusStops = "";
            switch (i) {
                case 0:
                    strBusStops = mInStops.toString();
                    break;
                case 1:
                    strBusStops = mOutStops.toString();
                    break;
                default:
                    strBusStops = "";
                    break;
            }
            setItem.putString("BusStops",strBusStops);
            setItem.putString("BusRoute",mBusRoute.toString());

            BusRouteStopListFragment busStopsFrag = new BusRouteStopListFragment();
            busStopsFrag.setArguments(setItem);

            //return null;
            return busStopsFrag;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            CharSequence resultTitle = "";
            try {
                switch (position) {
                    case 0:
                        resultTitle = mInStops.getString("direction_name");
                        break;
                    case 1:
                        resultTitle = mOutStops.getString("direction_name");
                        break;
                    default:
                        resultTitle = "";
                        break;
                }
            } catch (JSONException e) {

            }
            //return super.getPageTitle(position);
            return resultTitle;
        }


    }
}
