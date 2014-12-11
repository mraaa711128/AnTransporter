package edu.bu.mraaa.antransporter.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import edu.bu.mraaa.antransporter.R;

/**
 * Created by mraaa711128 on 12/9/14.
 */
public class MbtaTabStops extends Object implements DbRequestDelegate {
    private static final String TAB_NAME = "stops";

    private static final String COL_ID = "_id";
    private static final String COL_STOP_ID = "stop_id";
    private static final String COL_STOP_CODE = "stop_code";
    private static final String COL_STOP_NAME = "stop_name";
    private static final String COL_STOP_DESC = "stop_desc";
    private static final String COL_STOP_LAT = "stop_lat";
    private static final String COL_STOP_LON = "stop_lon";
    private static final String COL_ZONE_ID = "zone_id";
    private static final String COL_STOP_URL = "stop_url";
    private static final String COL_LOCATION_TYPE = "location_type";
    private static final String COL_PARENT_STATION = "parent_station";

    Context mContext;
    SQLiteDatabase mDb;
    String[] mColumns = {COL_ID,COL_STOP_ID,COL_STOP_CODE,COL_STOP_NAME,COL_STOP_DESC,COL_STOP_LAT,COL_STOP_LON,COL_ZONE_ID,COL_STOP_URL,COL_LOCATION_TYPE,COL_PARENT_STATION};

    public MbtaTabStops (Context context, SQLiteDatabase db) {
        mContext = context;
        mDb = db;
    }

    public void Create () {
        String cmdCreate = "create table " + TAB_NAME + " (" + COL_ID + " integer primary key," +
                                                           COL_STOP_ID + " text," +
                                                           COL_STOP_CODE + " text," +
                                                           COL_STOP_NAME + " text," +
                                                           COL_STOP_DESC + " text," +
                                                           COL_STOP_LAT + " text," +
                                                           COL_STOP_LON + " text," +
                                                           COL_ZONE_ID + " text," +
                                                           COL_STOP_URL + " text," +
                                                           COL_LOCATION_TYPE + " integer," +
                                                           COL_PARENT_STATION + " text)";
        mDb.execSQL(cmdCreate);
        loadData();
    }

    public void reCreate() {
        String cmdDrop = "drop table if exists " + TAB_NAME;
        mDb.execSQL(cmdDrop);
        Create();
    }

    @Override
    public void requestFinished(DbRequest request) {

    }

    @Override
    public void requestFailed(DbRequest request) {

    }

    @Override
    public void requestProgressing(DbRequest request, Long progress) {

    }

    private void loadData() {
        loadData ld = new loadData();
        ld.execute("");
    }

    private class loadData extends AsyncTask<String,Long,String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                InputStream input = mContext.getResources().openRawResource(R.raw.stops);
                BufferedReader rd = new BufferedReader(new InputStreamReader(input));
                String readline = rd.readLine();    //First Line is Column Name
                readline = rd.readLine();
                while (readline != null) {
                    String cmdInsert = "insert into " + TAB_NAME;
                    String strColumns = " (";
                    String strValues = " values(";
                    String[] row = readline.split(",");
                    if (mColumns.length >= row.length + 1) {
                        for (int i = 0; i < row.length; i++) {
                            if (row[i].contentEquals("")) {
                                row[i] = "\"\"";
                            }
                            if (i == 0) {
                                strColumns = strColumns + mColumns[i + 1];
                                strValues = strValues + row[i];
                            } else {
                                strColumns = strColumns + "," + mColumns[i + 1];
                                strValues = strValues + "," + row[i];
                            }
                        }
                        strColumns = strColumns + ")";
                        strValues = strValues + ")";
                    }
                    cmdInsert = cmdInsert + strColumns + strValues;
                    mDb.execSQL(cmdInsert);
                    readline = rd.readLine();
                }
                return "";
            } catch (IOException e) {
                return e.getMessage();
            }
        }

    }
}

