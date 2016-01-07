package com.biu.ap2.winder.ex4;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


/**
 * Created by ido on 6/21/15.
 */

/*
 * class GetChannelsParser
 * operate the method getChannels, parse the output and update with GateWayService
 */
public class GetChannelsParser extends AsyncTask<String, String, String> {
    public static final String OnRequestUpdate = "com.biu.ap2.winder.ex4.action.OnRequestUpdate";
    Context c;
    //constructor
    public GetChannelsParser(Context con) {
        this.c = con;
    }

    /**
     * getASCIIContentFromEntity
     * @param entity
     * @return string- the json object of the answer from the server
     * @throws IllegalStateException
     * @throws IOException
     */
    protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
        InputStream in = entity.getContent();
        StringBuffer out = new StringBuffer();
        int n = 1;
        while (n>0) {
            byte[] b = new byte[4096];
            n =  in.read(b);
            if (n>0) out.append(new String(b, 0, n));
        }
        return out.toString();
    }

    /**
     * doInBackground
     * @param params - the url of the server
     * @return the json object of the answer from the server
     */
    @Override
    protected String doInBackground(String... params) {
        HttpClient httpClient = LoginActivity.httpClient;
        HttpContext localContext = new BasicHttpContext();
        HttpGet httpGet = new HttpGet("http://" + params[0]);  // as url
        String text = null;
        try {
            HttpResponse response = httpClient.execute(httpGet, localContext);
            HttpEntity entity = response.getEntity();
            text = getASCIIContentFromEntity(entity);
        } catch (Exception e) {}
        return text;
    }

    /**
     * onPostExecute
     * @param result - the json object of the answer from the server
     * function operation: parse the json object and update with GateWayService
     */
    protected void onPostExecute(String result) {
        ArrayList<Friend> cl = new ArrayList<Friend>();
        JSONObject obj = null;
        try {
            obj = new JSONObject(result);

            JSONArray channels = obj.getJSONArray("channels");
            for (int i = 0; i < channels.length(); i++) {
                String id = channels.getJSONObject(i).getString("id");
                String name = channels.getJSONObject(i).getString("name");
                String icon = channels.getJSONObject(i).getString("icon");
                Friend f = new Friend(id, name,icon);
                cl.add(f);
            }
            //channel list is ready to use
            GateWayService gws = new GateWayService(this.c);
            //update the channelList
            gws.saveChannelList(cl);
            Intent i = new Intent(OnRequestUpdate);
            c.sendBroadcast(i);

        } catch (JSONException e) {
            Toast.makeText(c, "ERROR in Channel ic_action_update", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        //return messagesList;
    }
}