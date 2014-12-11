package edu.bu.mraaa.antransporter.db;

/**
 * Created by mraaa711128 on 12/9/14.
 */
public interface DbRequestDelegate {
    public void requestFinished(DbRequest request);
    public void requestFailed(DbRequest request);
    public void requestProgressing(DbRequest request, Long progress);
}
