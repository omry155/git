package com.biu.ap2.winder.ex4;

/**
 * Created by ido on 6/21/15.
 *
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
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
/***
 * class GetUpdatesParser
 *  send method to the server get in return the output
 *  parse the output,
 *
 */
public class GetUpdatesParser extends AsyncTask<String, String, String> {
    Context c;
    //constructor
    public GetUpdatesParser(Context con) {
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
        HttpGet httpGet = new HttpGet("http://" + params[0] + "/getUpdates");
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
     * @param result- the json object of the answer from the server
     * function operation: parse the json object and update with GateWayService
     */
    protected void onPostExecute(String result) {
        if (result == null) {
            Toast.makeText(c, "result is null", Toast.LENGTH_LONG).show();
            return;
        }
        JSONObject obj = null;
        try {
            GateWayService gws = new GateWayService(this.c);
            obj = new JSONObject(result);
            //check if we need to change server and update
            String change_server = obj.getString("change_server");
            if (change_server.length() > "appspot.com".length()) {
                // and update
                gws.ChangeServer(change_server);
            }
            JSONArray messages = obj.getJSONArray("messages");
            for (int i = 0; i < messages.length(); i++) {
                //get data of message of update with GateWayService
                String channel_id = messages.getJSONObject(i).getString("channel_id");
                String user_id = messages.getJSONObject(i).getString("user_id");
                String text = messages.getJSONObject(i).getString("text");
                String data_time = messages.getJSONObject(i).getString("date_time");
                String longtitude = messages.getJSONObject(i).getString("longtitude");
                String latitude = messages.getJSONObject(i).getString("latitude");
                gws.GotMail(channel_id, user_id, text, data_time, longtitude, latitude);
                Toast.makeText(c, "got mail!", Toast.LENGTH_LONG).show();
            }
            //Toast.makeText(c, "done update", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            //Toast.makeText(c, "ERROR", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }
}