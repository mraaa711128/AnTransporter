package edu.bu.mraaa.antransporter;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.TextView;

import edu.bu.mraaa.antransporter.db.MbtaDbService;
import edu.bu.mraaa.antransporter.db.MbtaDbServiceDelegate;


public class MainActivity extends Activity implements MbtaDbServiceDelegate{

    ProgressDialog progDiag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        MbtaDbService dbService = MbtaDbService.sharedService(this);
        dbService.setDelegte(this);
        dbService.initial();
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void dbPreCreate() {
        progDiag = ProgressDialog.show(this,"Prepare Data","Data Preparing ... ");
        progDiag.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDiag.setProgressNumberFormat("(%1d/%2d)");
    }

    @Override
    public void dbCreateProgressing(Long progressValue, Long maxValue) {
        if (progDiag != null) {
            progDiag.setMax(maxValue.intValue());
            progDiag.setProgress(progressValue.intValue());
        }
    }

    @Override
    public void dbCreateSuccess() {
        if (progDiag != null) {
            progDiag.setMessage("Success !");
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {

            }
            progDiag.dismiss();
        }
    }

    @Override
    public void dbCreateFail() {
        if (progDiag != null) {
            progDiag.setMessage("Fail !");
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {

            }
            progDiag.dismiss();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private TextView btnFindMyBus;
        private TextView btnFindMyStop;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            btnFindMyBus = (TextView) rootView.findViewById(R.id.btnFindMyBus);
            btnFindMyBus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //create BusRouteFragment
                    System.out.println("Find My Bus Clicked !!");

                    FragmentManager fm = getActivity().getFragmentManager();
                    Fragment fragment = new BusRouteFragment();
                    FragmentTransaction ft = fm.beginTransaction();
                    //ft.setCustomAnimations(R.anim.fragment_animation_in,R.anim.fragment_animation_out);
                    ft.setCustomAnimations(R.anim.fragment_slide_in_from_right,R.anim.fragment_slide_out_to_left);
                    ft.replace(R.id.container, fragment, "fragmentBusRoutes");
                    ft.addToBackStack("fragmentBusRoutes");
                    ft.commit();
                }
            });

            btnFindMyStop = (TextView) rootView.findViewById(R.id.btnFindMyStop);
            btnFindMyStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //create MapFragment
                    System.out.println("Find My Stop Clicked !!");
                }
            });

            return rootView;
        }
    }
}
