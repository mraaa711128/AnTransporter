package edu.bu.mraaa.antransporter;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.test.ApplicationTestCase;

import org.json.JSONObject;

import edu.bu.mraaa.antransporter.api.MbtaService;
import edu.bu.mraaa.antransporter.api.MbtaServiceDelegate;
import edu.bu.mraaa.antransporter.db.MbtaDbService;
import edu.bu.mraaa.antransporter.db.MbtaDbStore;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

/*
    public void testMbtaService() {
        MbtaService service = MbtaService.sharedService();

        assertNotNull(service);

        Delegater del = new Delegater();

        service.getRoutes(del);

        while (!del.isFinished) {
            // Do Nothing Just Wait
        }
    }
*/

    public void testMbtaDbStore() {
        //assertTrue(db != null);
        MbtaDbService service = MbtaDbService.sharedService(this.getContext());
        SQLiteDatabase db = service.getDatabase();

        //int i = 0;
        while (true) {
            //Do Nothing
            assertTrue(db != null);
            //i++;
            //System.out.printf("%d \n",i);
        }


    }

    private class Delegater implements MbtaServiceDelegate {
        boolean isFinished = false;

        @Override
        public void didQueryServiceBegin(MbtaService.ServiceId serviceId) {

        }

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