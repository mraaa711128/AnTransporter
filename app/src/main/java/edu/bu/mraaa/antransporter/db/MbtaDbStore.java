package edu.bu.mraaa.antransporter.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.concurrent.Semaphore;

import edu.bu.mraaa.antransporter.R;

/**
 * Created by mraaa711128 on 12/1/14.
 */
public class MbtaDbStore extends SQLiteOpenHelper {
    private static final int DB_VER = 1;
    private static final String DB_NAME = "MbtaDbStore.db";
    private static final String DB_PATH = "/data/data/edu.bu.mraaa.antransporter/databases/";
    private static final String DB_APK_PATH = "/assets/";

    private Context mContext;
    private boolean mDbCreated = false;
    private MbtaDbStoreDelegate mDelegate;

/*
    public synchronized static MbtaDbStore sharedDbStore(Context context) {
        if (dbStore == null) {
            dbStore = new MbtaDbStore(context);
        }
        return dbStore;
    }
*/

    public MbtaDbStore(Context context) {
        super(context,DB_NAME,null,DB_VER);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        copyDbFileFromAssets();
/*
        MbtaTabCalendar tabCalendar = new MbtaTabCalendar(mContext, db);
        tabCalendar.Create();
        MbtaTabStops tabStops = new MbtaTabStops(mContext, db);
        tabStops.Create();
        MbtaTabTrips tabTrips = new MbtaTabTrips(mContext,db);
        tabTrips.Create();
*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        copyDbFileFromAssets();
/*
        MbtaTabCalendar tabCalendar = new MbtaTabCalendar(mContext,db);
        tabCalendar.reCreate();
        MbtaTabStops tabStops = new MbtaTabStops(mContext, db);
        tabStops.reCreate();
        MbtaTabTrips tabTrips = new MbtaTabTrips(mContext,db);
        tabTrips.reCreate();
*/
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        //copyDbFileFromAssets();
    }

    public void setDelegate(MbtaDbStoreDelegate delegate) {
        mDelegate = delegate;
    }

    private void copyDbFileFromAssets() {
        mDbCreated=false;
        new AsyncTask<String, Long, Integer>() {
            @Override
            protected Integer doInBackground(String... params) {
                try {

/*
                    FileChannel srcChannel = new FileInputStream(mContext.getAssets().openFd(DB_NAME).getFileDescriptor()).getChannel();
                    FileChannel desChannel = new FileOutputStream(DB_PATH + DB_NAME).getChannel();
                    desChannel.transferFrom(srcChannel,0,srcChannel.size());
                    srcChannel.close();
                    desChannel.close();
*/
                    InputStream dbInput = mContext.getAssets().open(DB_NAME);
                    OutputStream fileOutput = new FileOutputStream(DB_PATH + DB_NAME);

                    byte[] buffer = new byte[1024];
                    int length = 0;
                    Long totalLength = new Long(0);

                    while ((length = dbInput.read(buffer)) > 0) {
                        fileOutput.write(buffer,0,length);
                        totalLength = totalLength + length;
                        this.publishProgress(totalLength,new Long(dbInput.available()));
                    }

                    fileOutput.flush();
                    fileOutput.close();
                    dbInput.close();

                    return 0;
                } catch (IOException e) {
                    System.out.print(e.getMessage());
                    return -1;
                }
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (mDelegate != null) {
                    mDelegate.dbPreCreate();
                }
            }

            @Override
            protected void onProgressUpdate(Long... values) {
                super.onProgressUpdate(values);
                //System.out.print("Current Copying ... (" + values[0].toString() + ")");
                if (mDelegate != null) {
                    mDelegate.dbCreateProgressing(values[0],values[1]);
                }
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                System.out.printf("copy db file return value = %d", integer);
                mDbCreated = true;
                if (mDelegate != null) {
                    if (integer == 0) {
                        mDelegate.dbCreateSuccess();
                    } else {
                        mDelegate.dbCreateFail();
                    }
                }
            }
        }.execute("");

/*
        while (mDbCreated==false) {
            //Do Nothing
        }
*/
    }
}
