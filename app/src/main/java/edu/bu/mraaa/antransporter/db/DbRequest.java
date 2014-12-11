package edu.bu.mraaa.antransporter.db;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

/**
 * Created by mraaa711128 on 12/9/14.
 */
public class DbRequest extends AsyncTask<String, Long, String> {
    SQLiteDatabase mDb;
    DbRequestDelegate mDelegate;
    String mResultData;

    public void SetDb(SQLiteDatabase db) {
        this.mDb = db;
    }

    public void SetDelegate(DbRequestDelegate delegate) {
        this.mDelegate = delegate;
    }

    public String ResultData() {
        return mResultData;
    }

    @Override
    protected String doInBackground(String... params) {
        return null;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

}
