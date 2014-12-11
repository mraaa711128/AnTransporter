package edu.bu.mraaa.antransporter.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by mraaa711128 on 12/10/14.
 */
public class MbtaDbService implements MbtaDbStoreDelegate {

    static MbtaDbService instance;

    ArrayList<MbtaDbServiceDelegate> serviceDelegates;

    Context mContext;
    SQLiteDatabase mDatabase;
    MbtaDbServiceDelegate mDelegate;

    public synchronized static MbtaDbService sharedService(Context context) {
        if (instance == null) {
            instance = new MbtaDbService(context);
        }
        return instance;
    }

    private MbtaDbService(Context context) {
        serviceDelegates = new ArrayList<MbtaDbServiceDelegate>();
        mContext = context;
    }

    public void initial() {
        MbtaDbStore dbStore = new MbtaDbStore(mContext);
        dbStore.setDelegate(this);
        mDatabase = dbStore.getReadableDatabase();
    }

    public void setDelegte(MbtaDbServiceDelegate delegate) {
        mDelegate = delegate;
    }

    public SQLiteDatabase getDatabase() {
        return mDatabase;
    }

    public String[] getRouteHeadSign(String route_id) {
        String[] result = new String[2];
        MbtaTabTrips tabTrips = new MbtaTabTrips(mContext,mDatabase);
        result[0] = tabTrips.getRouteHeadSign(route_id,0);
        result[1] = tabTrips.getRouteHeadSign(route_id,1);
        return result;
    }

    @Override
    public void dbPreCreate() {
        if (mDelegate != null) {
            mDelegate.dbPreCreate();
        }
    }

    @Override
    public void dbCreateProgressing(Long progressValue, Long maxValue) {
        System.out.print("Progress = (" + progressValue.toString() + "/" + maxValue.toString() + ")");
        if (mDelegate != null) {
            mDelegate.dbCreateProgressing(progressValue,maxValue);
        }
    }

    @Override
    public void dbCreateSuccess() {
        if (mDelegate != null) {
            mDelegate.dbCreateSuccess();
        }
    }

    @Override
    public void dbCreateFail() {
        if (mDelegate != null) {
            mDelegate.dbCreateFail();
        }
    }
}

