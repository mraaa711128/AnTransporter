package edu.bu.mraaa.antransporter.api;

//import android.util.ArrayMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import edu.bu.mraaa.antransporter.BuildConfig;

/**
 * Created by mraaa711128 on 11/9/14.
 */

public class MbtaService implements HttpRequestDelegate {
    static final String mbtaBaseUrl = "http://realtime.mbta.com/developer/api/v2/";
    static final String mbtaDevApiKey = "wX9NwuHnZU2ToO7GmGR9uw";
    static final String mbtaRelApiKey = "ITM31mkVl0imSepyg9NsjA";
    static final Integer IndexNotFound = -1;

    static MbtaService instance;

    String mbtaApiKey;

    ArrayList<HttpRequest> serviceRequests;
    ArrayList<MbtaServiceDelegate> serviceDelegates;

    public enum ServiceId {
        routes,
        routesbystop,
        stopsbyroute,
        stopsbylocation,
        schedulebystop,
        schedulebyroute,
        schedulebytrip,
        predictionsbystop,
        predictionsbyroute,
        vehiclesbyroute,
        predictionsbytrip,
        vehiclesbytrip,
        alerts,
        alertsbyroute,
        alertsbystop,
        alertbyid,
        alertsheaders,
        alertheadersbyroute,
        alertheadersbystop,
        servertime
    }


    public synchronized static MbtaService sharedService() {
        if (instance == null) {
            instance = new MbtaService();
         }
        return instance;
    }

    private MbtaService() {
        if(BuildConfig.DEBUG) {
            mbtaApiKey =mbtaDevApiKey;
        } else {
            mbtaApiKey = mbtaRelApiKey;
        }
        serviceRequests = new ArrayList<HttpRequest>();
        serviceDelegates = new ArrayList<MbtaServiceDelegate>();
    }

    public synchronized void getRoutes(MbtaServiceDelegate delegte) {
        URL reqRoutesUrl = getRequestUrl(ServiceId.routes);
        HttpRequest reqRoutes = new HttpRequest();
        reqRoutes.setServiceId(ServiceId.routes);
        reqRoutes.setDelegate(this);
        serviceRequests.add(reqRoutes);
        serviceDelegates.add(delegte);
        reqRoutes.execute(reqRoutesUrl);
    }

    public synchronized void getRoutesByStop(String stop, MbtaServiceDelegate delegate) {
        URL reqRoutesByStopUrl = getRequestUrl(ServiceId.routesbystop,stop);
        HttpRequest reqRoutesByStop = new HttpRequest();
        reqRoutesByStop.setServiceId(ServiceId.routesbystop);
        reqRoutesByStop.setDelegate(this);
        serviceRequests.add(reqRoutesByStop);
        serviceDelegates.add(delegate);
        reqRoutesByStop.execute(reqRoutesByStopUrl);
    }

    public synchronized void getStopsByRoute(String route, MbtaServiceDelegate delegate) {
        URL reqStopsByRouteUrl = getRequestUrl(ServiceId.stopsbyroute, route);
        HttpRequest reqStopsByRoute = new HttpRequest();
        reqStopsByRoute.setServiceId(ServiceId.stopsbyroute);
        reqStopsByRoute.setDelegate(this);
        serviceRequests.add(reqStopsByRoute);
        serviceDelegates.add(delegate);
        reqStopsByRoute.execute(reqStopsByRouteUrl);
    }

    public synchronized void getStopsByLocation(double latitude, double lontitue, MbtaServiceDelegate delegate) {
        URL reqStopsByLocUrl = getRequestUrl(ServiceId.stopsbylocation,latitude,lontitue);
        HttpRequest reqStopsByLoc = new HttpRequest();
        reqStopsByLoc.setServiceId(ServiceId.stopsbylocation);
        reqStopsByLoc.setDelegate(this);
        serviceRequests.add(reqStopsByLoc);
        serviceDelegates.add(delegate);
        reqStopsByLoc.execute(reqStopsByLocUrl);
    }

    public synchronized void getScheduleByStop(String stop, String route, String direction,
                                               Long datetime, Integer maxtime, Integer maxtrips, MbtaServiceDelegate delegate) {
        URL reqSchByStopUrl = getRequestUrl(ServiceId.schedulebystop,stop,route,direction,datetime,maxtime,maxtrips);
        HttpRequest reqSchByStop = new HttpRequest();
        reqSchByStop.setServiceId(ServiceId.schedulebystop);
        reqSchByStop.setDelegate(this);
        serviceRequests.add(reqSchByStop);
        serviceDelegates.add(delegate);
        reqSchByStop.execute(reqSchByStopUrl);
    }

    public synchronized void getScheduleByRoute(String route, String direction, Long datetime,
                                                Integer maxtime, Integer maxtrips, MbtaServiceDelegate delegate) {
        URL reqSchByRouteUrl = getRequestUrl(ServiceId.schedulebyroute,route,direction,datetime,maxtime,maxtrips);
        HttpRequest reqSchByRoute = new HttpRequest();
        reqSchByRoute.setServiceId(ServiceId.schedulebyroute);
        reqSchByRoute.setDelegate(this);
        serviceRequests.add(reqSchByRoute);
        serviceDelegates.add(delegate);
        reqSchByRoute.execute(reqSchByRouteUrl);
    }

    public synchronized void getScheduleByTrip(String trip, Integer datetime, MbtaServiceDelegate delegate) {
        URL reqSchByTripUrl = getRequestUrl(ServiceId.schedulebytrip,trip,datetime);
        HttpRequest reqSchByTrip = new HttpRequest();
        reqSchByTrip.setServiceId(ServiceId.schedulebytrip);
        reqSchByTrip.setDelegate(this);
        serviceRequests.add(reqSchByTrip);
        serviceDelegates.add(delegate);
        reqSchByTrip.execute(reqSchByTripUrl);
    }

    public synchronized void getPredictionsByStop(String stop, boolean includeaccessalerts,
                                                 boolean includeservicealerts, MbtaServiceDelegate delegate) {
        URL reqPredByStopUrl = getRequestUrl(ServiceId.predictionsbystop,stop,includeaccessalerts,includeservicealerts);
        HttpRequest reqPredByStop = new HttpRequest();
        reqPredByStop.setServiceId(ServiceId.predictionsbystop);
        reqPredByStop.setDelegate(this);
        serviceRequests.add(reqPredByStop);
        serviceDelegates.add(delegate);
        reqPredByStop.execute(reqPredByStopUrl);
    }

    public synchronized void getPredictionsByRoute(String route, Boolean includeaccessalerts,
                                                  Boolean includeservicealerts, MbtaServiceDelegate delegate) {
        URL reqPredByRouteUrl = getRequestUrl(ServiceId.predictionsbyroute,route,includeaccessalerts,includeservicealerts);
        HttpRequest reqPredByRoute = new HttpRequest();
        reqPredByRoute.setServiceId(ServiceId.predictionsbyroute);
        reqPredByRoute.setDelegate(this);
        serviceRequests.add(reqPredByRoute);
        serviceDelegates.add(delegate);
        reqPredByRoute.execute(reqPredByRouteUrl);
    }

    public synchronized void getPredictionsByTrip(String trip, MbtaServiceDelegate delegate) {
        URL reqPredByTripUrl = getRequestUrl(ServiceId.predictionsbytrip,trip);
        HttpRequest reqPredByTrip = new HttpRequest();
        reqPredByTrip.setServiceId(ServiceId.predictionsbytrip);
        reqPredByTrip.setDelegate(this);
        serviceRequests.add(reqPredByTrip);
        serviceDelegates.add(delegate);
        reqPredByTrip.execute(reqPredByTripUrl);
    }

    public synchronized void getVehiclesByRoute(String route, MbtaServiceDelegate delegate) {
        URL reqVehicleByRouteUrl = getRequestUrl(ServiceId.vehiclesbyroute,route);
        HttpRequest reqVehicleByRoute = new HttpRequest();
        reqVehicleByRoute.setServiceId(ServiceId.vehiclesbyroute);
        reqVehicleByRoute.setDelegate(this);
        serviceRequests.add(reqVehicleByRoute);
        serviceDelegates.add(delegate);
        reqVehicleByRoute.execute(reqVehicleByRouteUrl);
    }

    public synchronized void getVehiclesByTrip(String trip, MbtaServiceDelegate delegate) {
        URL reqVehicleByTripUrl = getRequestUrl(ServiceId.vehiclesbytrip,trip);
        HttpRequest reqVehicleByTrip = new HttpRequest();
        reqVehicleByTrip.setServiceId(ServiceId.vehiclesbytrip);
        reqVehicleByTrip.setDelegate(this);
        serviceRequests.add(reqVehicleByTrip);
        serviceDelegates.add(delegate);
        reqVehicleByTrip.execute(reqVehicleByTripUrl);
    }

    @Override
    public void requestReady(HttpRequest request) {
        int idxRequest = serviceRequests.indexOf(request);
        if (idxRequest != IndexNotFound) {
            serviceDelegates.get(idxRequest).didQueryServiceBegin(request.serviceId);
        }
    }

    @Override
    public synchronized void requestFinished(HttpRequest request) {
        int idxRequest = serviceRequests.indexOf(request);
        if (idxRequest != IndexNotFound) {
            String strResponse = request.ResponseData();
            try {
                JSONObject jsonObj = new JSONObject(strResponse);
                if (jsonObj.has("error")) {
                    JSONObject jsonErr = jsonObj.getJSONObject("error");
                    String errId = jsonErr.getString("error_id");
                    String errMsg = jsonErr.getString("error_msg");
                    Error err = new Error(errId + "-" + errMsg);
                    serviceDelegates.get(idxRequest).didQueryServiceFail(request.serviceId,err);
                } else {
                    serviceDelegates.get(idxRequest).didQueryServiceSuccess(request.serviceId,jsonObj);
                }
            } catch (JSONException e) {
                Error err = new Error("996-" + e.getMessage());
                serviceDelegates.get(idxRequest).didQueryServiceFail(request.serviceId,err);
            } finally {
                serviceRequests.remove(idxRequest);
                serviceDelegates.remove(idxRequest);
            }
        }
    }

    @Override
    public synchronized void requestFailed(HttpRequest request) {
        int idxRequest = serviceRequests.indexOf(request);
        if (idxRequest != IndexNotFound) {
            String strResponse = request.ResponseData();
            try {
                JSONObject jsonObj = new JSONObject(strResponse);
                if (jsonObj.has("error")) {
                    JSONObject jsonErr = jsonObj.getJSONObject("error");
                    String errId = jsonErr.getString("error_id");
                    String errMsg = jsonErr.getString("error_msg");
                    Error err = new Error(errId + "-" + errMsg);
                    serviceDelegates.get(idxRequest).didQueryServiceFail(request.serviceId,err);
                } else {
                    serviceDelegates.get(idxRequest).didQueryServiceSuccess(request.serviceId,jsonObj);
                }
            } catch (JSONException e) {
                Error err = new Error("996-" + e.getMessage());
                serviceDelegates.get(idxRequest).didQueryServiceFail(request.serviceId,err);
            } finally {
                serviceRequests.remove(idxRequest);
                serviceDelegates.remove(idxRequest);
            }
        }
    }

    @Override
    public synchronized void requestProgressing(HttpRequest request, Long progress) {

    }

    private URL getRequestUrl(ServiceId serviceId, Object... values) {
        String mbtaQueryName = getMbtaQueryName(serviceId);
        String mbtaQueryParas = getMbtaQueryParameters(serviceId, values);
        String reqestUrl = mbtaBaseUrl + mbtaQueryName + "?api_key=" + mbtaApiKey + "&" + mbtaQueryParas + "format=json";
        URL returnUrl;
        try {
            returnUrl = new URL(reqestUrl);
        } catch (MalformedURLException e) {
            returnUrl = null;
        }
        return returnUrl;
    }

    private String getMbtaQueryName(ServiceId serviceId) {
        String strReturn = "";
        switch (serviceId) {
            case routes:
                strReturn = "routes";
                break;
            case routesbystop:
                strReturn = "routesbystop";
                break;
            case stopsbyroute:
                strReturn = "stopsbyroute";
                break;
            case stopsbylocation:
                strReturn = "stopsbylocation";
                break;
            case schedulebystop:
                strReturn = "schedulebystop";
                break;
            case schedulebyroute:
                strReturn = "schedulebyroute";
                break;
            case schedulebytrip:
                strReturn = "schedulebytrip";
                break;
            case predictionsbystop:
                strReturn = "predictionsbystop";
                break;
            case predictionsbyroute:
                strReturn = "predictionsbyroute";
                break;
            case predictionsbytrip:
                strReturn = "predictionsbytrip";
                break;
            case vehiclesbyroute:
                strReturn = "vehiclesbyroute";
                break;
            case vehiclesbytrip:
                strReturn = "vehiclesbytrip";
                break;
            default:
                strReturn = "";
                break;
        }
        return strReturn;
    }

    private String getMbtaQueryParameters(ServiceId serviceId, Object... values) {
        //ArrayMap<String, String> paras = new ArrayMap<String, String>();
        ArrayList<String> paras = new ArrayList<String>();
        String strReturn = "";
        switch (serviceId) {
            case routes:
                break;
            case routesbystop:
                //paras.put("stop",values[0]);
                paras.add("stop");
                break;
            case stopsbyroute:
                //paras.put("route",values[0]);
                paras.add("route");
                break;
            case stopsbylocation:
                //paras.put("lat",values[0]);
                //paras.put("lon",values[1]);
                paras.add("lat");
                paras.add("lon");
                break;
            case schedulebystop:
                //paras.put("stop",values[0]);
                //paras.put("route",values[1]);
                //paras.put("direction",values[2]);
                //paras.put("datetime",values[3]);
                //paras.put("max_time",values[4]);
                //paras.put("max_trips",values[5]);
                paras.add("stop");
                paras.add("route");
                paras.add("direction");
                paras.add("datetime");
                paras.add("max_time");
                paras.add("max_trip");
                break;
            case schedulebyroute:
                //paras.put("route",values[0]);
                //paras.put("direction",values[1]);
                //paras.put("datetime",values[2]);
                //paras.put("max_time",values[3]);
                //paras.put("max_trips",values[4]);
                paras.add("route");
                paras.add("direction");
                paras.add("datetime");
                paras.add("max_time");
                paras.add("max_trip");
                break;
            case schedulebytrip:
                //paras.put("trip",values[0]);
                //paras.put("datetime",values[1]);
                paras.add("trip");
                paras.add("datetime");
                break;
            case predictionsbystop:
                //paras.put("stop",values[0]);
                //paras.put("include_access_alerts",values[1]);
                //paras.put("include_service_alerts",values[2]);
                paras.add("stop");
                paras.add("include_access_alerts");
                paras.add("include_service_alerts");
                break;
            case predictionsbyroute:
                //paras.put("route",values[0]);
                //paras.put("include_access_alerts",values[1]);
                //paras.put("include_service_alerts",values[2]);
                paras.add("route");
                paras.add("include_access_alerts");
                paras.add("include_service_alerts");
                break;
            case predictionsbytrip:
                //paras.put("trip",values[0]);
                paras.add("trip");
                break;
            case vehiclesbyroute:
                //paras.put("route",values[0]);
                paras.add("route");
                break;
            case vehiclesbytrip:
                //paras.put("trip",values[0]);
                paras.add("trip");
                break;
            default:
                break;
        }
        for (int i = 0; i < paras.size(); i++) {
            if (values[i] != null) {
                String strValue = "";
                try {
                    strValue = URLEncoder.encode(String.valueOf(values[i]), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                }
                strReturn = strReturn + paras.get(i) + "=" + strValue + "&";
            }
        }
        return strReturn;
    }
}
