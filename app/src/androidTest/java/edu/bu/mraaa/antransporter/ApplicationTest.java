package edu.bu.mraaa.antransporter;

import android.app.Application;
import android.test.ApplicationTestCase;

import org.json.JSONObject;

import edu.bu.mraaa.antransporter.edu.bu.mraaa.antransporter.api.MbtaService;
import edu.bu.mraaa.antransporter.edu.bu.mraaa.antransporter.api.MbtaServiceDelegate;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testMbtaService() {
        MbtaService service = MbtaService.sharedService();

        assertNotNull(service);

        Delegater del = new Delegater();

        service.getRoutes(del);

        while (!del.isFinished) {
            // Do Nothing Just Wait
        }
    }

    private class Delegater implements MbtaServiceDelegate {
        boolean isFinished = false;

        @Override
        public void didQueryServiceSuccess(MbtaService.ServiceId serviceId, JSONObject result) {
            assertTrue(serviceId == MbtaService.ServiceId.routes);
            assertEquals(JSONObject.class.toString(),result.getClass().toString());
            isFinished = true;
        }

        @Override
        public void didQueryServiceFail(MbtaService.ServiceId serviceId, Error error) {
            assertTrue(serviceId == MbtaService.ServiceId.routes);
            assertEquals(Error.class.toString(),error.getClass().toString());
            isFinished = true;
        }

        @Override
        public void didQueryServiceProgress(MbtaService.ServiceId serviceId, Long progress) {
            assertTrue(serviceId == MbtaService.ServiceId.routes);
            isFinished = true;
        }
    }
}