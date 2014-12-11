package edu.bu.mraaa.antransporter.db;

/**
 * Created by mraaa711128 on 12/10/14.
 */
public interface MbtaDbServiceDelegate  {

    public void dbPreCreate();
    public void dbCreateProgressing(Long progressValue, Long maxValue);
    public void dbCreateSuccess();
    public void dbCreateFail();
}
