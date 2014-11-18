package edu.bu.mraaa.antransporter.edu.bu.mraaa.antransporter.api;

/**
 * Created by mraaa711128 on 11/9/14.
 */
public interface HttpRequestDelegate {
    public void requestFinished(HttpRequest request);
    public void requestFailed(HttpRequest request);
    public void requestProgressing(HttpRequest request, Long progress);
}
