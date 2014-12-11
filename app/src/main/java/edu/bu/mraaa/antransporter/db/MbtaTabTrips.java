package edu.bu.mraaa.antransporter.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.bu.mraaa.antransporter.R;

/**
 * Created by mraaa711128 on 12/9/14.
 */
public class MbtaTabTrips extends Object {
    private static final String TAB_NAME = "trips";

    private static final String COL_ID = "_id";
    private static final String COL_ROUTE_ID = "route_id";
    private static final String COL_SERVICE_ID = "service_id";
    private static final String COL_TRIP_ID = "trip_id";
    private static final String COL_TRIP_HEADSIGN = "trip_headsign";
    private static final String COL_TRIP_SHORT_NAME = "trip_short_name";
    private static final String COL_DIRECTION_ID = "direction_id";
    private static final String COL_BLOCK_ID = "block_id";
    private static final String COL_SHAPE_ID = "shape_id";

    Context mContext;
    SQLiteDatabase mDb;
    String[] mColumns = {COL_ID,COL_ROUTE_ID,COL_SERVICE_ID,COL_TRIP_ID,COL_TRIP_HEADSIGN,COL_TRIP_SHORT_NAME,COL_DIRECTION_ID,COL_BLOCK_ID,COL_SHAPE_ID};

    public MbtaTabTrips(Context context, SQLiteDatabase db) {
        mContext = context;
        mDb = db;
    }

    public void Create() {
        String cmdCreate = "create table " + TAB_NAME + " (" + COL_ID + " integer primary key," +
                                                           COL_ROUTE_ID + " text," +
                                                           COL_SERVICE_ID + " text," +
                                                           COL_TRIP_ID + " text," +
                                                           COL_TRIP_HEADSIGN + " text," +
                                                           COL_TRIP_SHORT_NAME + " text," +
                                                           COL_DIRECTION_ID + " integer," +
                                                           COL_BLOCK_ID + " text," +
                                                           COL_SHAPE_ID + " text)";
        mDb.execSQL(cmdCreate);
        loadData();
    }

    public void reCreate() {
        String cmdDrop = "drop table if exists " + TAB_NAME;
        mDb.execSQL(cmdDrop);
        Create();
    }

    public String getRouteHeadSign(String route_id, Integer direction_id) {
        try {
            String valHeadSign;
            String[] selCols = {"*"};
            String selString = COL_ROUTE_ID + "= ? AND " + COL_DIRECTION_ID + "= ?";
            String[] selArgs = {route_id,direction_id.toString()};
            Cursor cur = mDb.query(TAB_NAME,selCols,selString,selArgs,"","","","1");
            if (cur.moveToFirst()) {
                int colIndex = cur.getColumnIndex(COL_TRIP_HEADSIGN);
                valHeadSign = cur.getString(colIndex);
            } else {
                valHeadSign = "";
            }
            if (cur != null && cur.isClosed()==false) {
                cur.close();
            }
            return valHeadSign;
        } catch (SQLiteException e) {
            return "";
        }
    }

    private void loadData() {
        loadData ld = new loadData();
        ld.execute("");
    }

    private class loadData extends AsyncTask<String,Long,String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                InputStream input = mContext.getResources().openRawResource(R.raw.trips);
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
                System.out.print(e.getMessage());
                return e.getMessage();
            } catch (SQLiteException e) {
                System.out.print(e.getMessage());
                return e.getMessage();
            }
        }
    }
}
