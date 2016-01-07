package com.biu.ap2.winder.ex4;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

/**
 * GetDailyService - service for update the app once a day
 */
public class GetDailyService extends IntentService {

    public static final String DailyUpdate = "com.biu.ap2.winder.ex4.action.DailyUpdate";
    private static final String OnRequestUpdate = "com.biu.ap2.winder.ex4.action.OnRequestUpdate";
    String url;
    SharedPreferences sharedPref;
    Context c;

    //constructor
    public GetDailyService() {super("GetDailyService");}

    /**
     * onHandleIntent - update the app
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        this.url = sharedPref.getString("currentIP", "project-ocdi.appspot.com");
        Bundle extras = intent.getExtras();
        String time;
        if (extras == null) return;
        //get the server ip and add request

        c = getApplication().getApplicationContext();
        //get ic_action_update time  (day or now)
        time = extras.getString("time");
        if (time.equals("now")) {
            GetServersParser gsp = new GetServersParser(c);
            GetChannelsParser gcp = new GetChannelsParser(c);

            gsp.execute(this.url + "/getServers");
            gcp.execute(this.url + "/getChannels");



        } else if (time.equals("day")) {
            this.getDailyUpdate();
        }

    }

    /**
     * getDailyUpdate - run in seperate thread: sleep for a day and update the app
     */
    private void getDailyUpdate() {
        Thread tLoader = new Thread(new Runnable() {
            @Override
            public void run() {
                int cnt = 0;
                while (true) {
                    cnt++;
                    if (cnt == 3) return; //just for caution
                    String url = sharedPref.getString("currentIP", "project-ocdi.appspot.com");
                    GetServersParser gsp = new GetServersParser(c);
                    GetChannelsParser gcp = new GetChannelsParser(c);
                    gsp.execute("http://" + url + "/getServers/");
                    gcp.equals("http://" + url + "/getChannels");
                    Intent i = new Intent(DailyUpdate);
                    sendBroadcast(i);
                    //sleep for 24 hours
                    try {
                        Thread.sleep(86400000);
                    } catch (Exception e) {e.printStackTrace();}

                }
            }
        });
        tLoader.start();
    }
    /**
     *
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionDailyUpdate(String param1, String param2) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionOnRequestUpdate(String param1, String param2) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
