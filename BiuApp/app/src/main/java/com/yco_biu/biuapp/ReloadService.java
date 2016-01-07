package com.biu.ap2.winder.ex4;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.io.FileOutputStream;
import java.util.List;


/**
 * ReloadService - this class is for servise of reload - one we can preform behind the sense
 */
public class ReloadService extends IntentService {
    public static final String DONE = "com.biu.ap2.winder.ex4.Services.ReloadService.DONE";
    //constructor
    public ReloadService() {
        super(ReloadService.class.getName());
    }
    //constructor
    public ReloadService(String name) {
        super(name);
    }

    /**
     * onHandleIntent of the service
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intent i = new Intent(DONE);
        this.sendBroadcast(i);
    }

}
