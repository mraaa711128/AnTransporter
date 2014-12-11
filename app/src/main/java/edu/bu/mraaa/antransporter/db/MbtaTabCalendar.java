package edu.bu.mraaa.antransporter.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Debug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import edu.bu.mraaa.antransporter.R;

/**
 * Created by mraaa711128 on 12/9/14.
 */
public class MbtaTabCalendar extends Object implements DbRequestDelegate {
    private static final String TAB_NAME = "calendar";

    private static final String COL_ID = "_id";
    private static final String COL_SERVICE_ID = "service_id";
    private static final String COL_MONDAY = "monday";
    private static final String COL_TUESDAY = "tuesday";
    private static final String COL_WEDNESDAY = "wednesday";
    private static final String COL_THURSDAY = "thursday";
    private static final String COL_FRIDAY = "friday";
    private static final String COL_SATURDAY = "saturday";
    private static final String COL_SUNDAY = "sunday";
    private static final String COL_START_DATE = "start_date";
    private static final String COL_END_DATE = "end_date";

    Context mContext;
    SQLiteDatabase mDb;
    String[] mColumns = {COL_ID,COL_SERVICE_ID,COL_MONDAY,COL_TUESDAY,COL_WEDNESDAY,COL_THURSDAY,COL_FRIDAY,COL_SATURDAY,COL_SUNDAY,COL_START_DATE,COL_END_DATE};

    public MbtaTabCalendar(Context context, SQLiteDatabase db) {
        mContext = context;
        mDb = db;
    }

    public void Create() {
        String cmdCreate = "create table " + TAB_NAME + " (" + COL_ID + " integer primary key," +
                                                           COL_SERVICE_ID + " text," +
                                                           COL_MONDAY + " integer," +
                                                           COL_TUESDAY + " integer," +
                                                           COL_WEDNESDAY + " integer," +
                                                           COL_THURSDAY + " integer," +
                                                           COL_FRIDAY + " integer," +
                                                           COL_SATURDAY + " integer," +
                                                           COL_SUNDAY + " integer," +
                                                           COL_START_DATE + " text," +
                                                           COL_END_DATE + " text)";
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
                InputStream input = mContext.getResources().openRawResource(R.raw.calendar);
                BufferedReader rd = new BufferedReader(new InputStreamReader(input));
                String readline = rd.readLine();    //First Line is Column Name
                readline = rd.readLine();
                while (readline != null) {
                    String cmdInsert = "insert into " + TAB_NAME;
                    String strColumns = " (";
                    String strValues = " values(";
                    String[] row = readline.split(",");
                    if (mColumns.length == row.length + 1) {
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
                return e.getMessage();
            }
        }
    }
}
