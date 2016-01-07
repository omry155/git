package com.biu.ap2.winder.ex4;

/**
 * Created by winder on 6/22/15.
 */


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;



/**
 * GetCookie - using the token to get from the server an auth cookie
 */
public class GetCookie extends AsyncTask<String, Void, Boolean> {

    String appId;
    HttpParams params;
    private HttpResponse response;
    Context context;
    private DefaultHttpClient httpclient;

    //constructor
    public GetCookie(DefaultHttpClient httpclient, String appId, Context context)
    {
        this.httpclient = httpclient;
        params = httpclient.getParams();
        this.appId = appId;
        this.context = context.getApplicationContext();
    }

    /**
     * doInBackground
     * @param tokens
     * @return the auth cookie
     */
    protected Boolean doInBackground(String... tokens) {

        try {

            // Don't follow redirects
            params.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
            //HttpGet httpGet = new HttpGet("http://" + appId + ".appspot.com/_ah/login?continue=http://" + appId + ".appspot.com/&auth=" + tokens[0]);
            HttpGet httpGet = new HttpGet("http://" + appId + "/_ah/login?continue=http://" + appId + "/&auth=" + tokens[0]);
            response = httpclient.execute(httpGet);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            response.getEntity().writeTo(out);
            out.close();

            if(response.getStatusLine().getStatusCode() != 302){
                // Response should be a redirect
                return false;
            }

            //check if we received the ACSID or the SACSID cookie, depends on http or https request
            for(Cookie cookie : httpclient.getCookieStore().getCookies()) {
                if(cookie.getName().equals("ACSID") || cookie.getName().equals("SACSID")){
                    return true;
                }
            }

        }  catch (Exception e) {
            e.printStackTrace();
            cancel(true);
        } finally {
            params.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
        }
        return false;
    }

    /**
     * onPostExecute
     * @param result - the auth cookie
     */
    protected void onPostExecute(Boolean result)
    {
        if (result) {
            Intent i = new Intent(context, MapsActivity.class);

            LoginTask lt = new LoginTask(context);
            lt.execute(appId + "/login");

            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }


    }
}
