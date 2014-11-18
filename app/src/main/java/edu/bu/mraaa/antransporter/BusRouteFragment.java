package edu.bu.mraaa.antransporter;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by mraaa711128 on 11/7/14.
 */
public class BusRouteFragment extends ListFragment {

    public BusRouteFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.title_fragment_busroute_list);
    }

    private class BusRouteAdapter extends ArrayAdapter<BusRoute> {

        public BusRouteAdapter(ArrayList<BusRoute> busRoutes) {
            super(getActivity(),R.layout.fragment_busroute_list,busRoutes);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.fragment_busroute_list_item,null);
            }

            return convertView;
        }
    }
}
