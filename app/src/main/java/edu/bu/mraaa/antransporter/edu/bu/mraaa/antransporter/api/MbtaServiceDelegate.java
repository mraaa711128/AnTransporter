package edu.bu.mraaa.antransporter.edu.bu.mraaa.antransporter.api;

import org.json.JSONObject;

/**
 * Created by mraaa711128 on 11/11/14.
 */
public interface MbtaServiceDelegate {
    public void didQueryServiceSuccess(MbtaService.ServiceId serviceId, JSONObject result);
    public void didQueryServiceFail(MbtaService.ServiceId serviceId, Error error);
    public void didQueryServiceProgress(MbtaService.ServiceId serviceId, Long progress);
}
