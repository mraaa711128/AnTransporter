package edu.bu.mraaa.antransporter.api;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by mraaa711128 on 11/9/14.
 */

public class HttpRequest extends AsyncTask<URL, Long, String> {
    HttpRequestDelegate delegate;
    MbtaService.ServiceId serviceId;
    String responseString;

    public void setDelegate(HttpRequestDelegate delegate) {
        this.delegate = delegate;
    }

    public void setServiceId(MbtaService.ServiceId id) {
        this.serviceId = id;
    }

    public String ResponseData () {
        return responseString;
    }

    @Override
    public String doInBackground(URL... urls) {
        HttpClient reqHttp = new DefaultHttpClient();
        HttpResponse resHttp;
        String strResponse;
        try {
            resHttp = reqHttp.execute(new HttpGet(urls[0].toURI()));
            StatusLine reqStatus = resHttp.getStatusLine();
            if (reqStatus.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                resHttp.getEntity().writeTo(outStream);
                outStream.close();
                strResponse = outStream.toString();
            } else {
                resHttp.getEntity().getContent().close();
                throw new IOException(reqStatus.getReasonPhrase());
            }
        } catch (URISyntaxException e) {
            strResponse = JsonErrorEncode("999",e.getMessage());
        } catch (ClientProtocolException e) {
            strResponse = JsonErrorEncode("998",e.getMessage());
        } catch (IOException e) {
            strResponse = JsonErrorEncode("997",e.getMessage());
        }
        return strResponse;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        delegate.requestReady(this);
    }

    @Override
    public void onProgressUpdate(Long... progress) {
        super.onProgressUpdate(progress);
        delegate.requestProgressing(this, progress[0]);
    }

    @Override
    public void onPostExecute(String result) {
        super.onPostExecute(result);
        responseString = result;
        delegate.requestFinished(this);
    }

    @Override
    public void onCancelled(String result) {
        super.onCancelled(result);
        responseString = JsonErrorEncode("995","Request Canceled");
        delegate.requestFailed(this);
    }

    private String JsonErrorEncode(String errorId, String errorMsg) {
        JSONObject jsonReturn;
        String strReturn;
        try {
            JSONObject jsonError = new JSONObject();
            jsonError.put("error_id", errorId);
            jsonError.put("error_msg",errorMsg);
            jsonReturn = new JSONObject();
            jsonReturn.put("error",jsonError);
            strReturn = jsonReturn.toString();
        } catch (JSONException e) {
            strReturn = "{\"error\":{\"error_id\":\"" + errorId + "\",\"error_msg\":\"" + errorMsg + "\"}}";
        }
        return strReturn;
    }
}
