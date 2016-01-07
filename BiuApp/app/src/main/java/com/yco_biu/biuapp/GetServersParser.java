package com.biu.ap2.winder.ex4;



/**
 * Created by ido on 6/14/15.
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
import org.json.JSONObject;
/*
 * class GetServersParser
 * operate the method getServer, parse the output and update with GateWayService
 */
public class GetServersParser extends AsyncTask<String, String, String> {
    Context c;
    //constructor
    public GetServersParser(Context con) {
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
        //HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpGet httpGet = new HttpGet("http://"+params[0]);  // as url "http://project-ocdi.appspot.com/getServers"
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
        ArrayList<String> serversList = new ArrayList<String>();

        try {
            JSONObject obj = new JSONObject(result);
            String servers = obj.getString("server");
            //cut the [] from the first and last index
            servers = servers.substring(1, servers.length() -1);
            //split by ","
            String[] splitServers = servers.split(",");
            for (int i = 0; i < splitServers.length; i++) {
                //cut the "" from the first and last index
                if (isLegal(splitServers[i])) {
                    servers = splitServers[i].substring(1, splitServers[i].length() - 1);
                    serversList.add(servers);
                }
            }
            //serversList is ready to use
            GateWayService gws = new GateWayService(this.c);
            gws.saveIP(serversList);
        } catch (Exception e) {
            Toast.makeText(c, "ERROR in Servers ic_action_update", Toast.LENGTH_LONG).show();
            e.printStackTrace();}
    }

    /**
     * isLegal
     * @param s- string of massage
     * @return true for legal massage, false for illegal.
     */
    private boolean isLegal(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != ' ' || s.charAt(i) != '"') {
                return true;
            }
        }
        return false;
    }
    //in the code------------->new GetServersParser().execute(url);//url- server ip


}