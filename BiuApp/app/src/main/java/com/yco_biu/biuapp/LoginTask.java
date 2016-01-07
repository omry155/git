package com.biu.ap2.winder.ex4;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * LoginTask - activate, parse and update the output
 *              of login and logoff method
 */

public class LoginTask extends AsyncTask<String, String, String> {
    Context c;
    String url;
    public LoginTask(Context con) {
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
        this.url = params[0];
        HttpContext localContext = new BasicHttpContext();
        HttpGet httpGet = new HttpGet("http://"+params[0]);  // as url "http:// + url + /login or logoff"
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
        String message = parserOfAnswer(result);
        Toast.makeText(c, "done: " + url + "answer: " + result, Toast.LENGTH_LONG).show();

        if (message == null) return;
        else {
            // do something
            // of save in SharedPreferences
        }
    }


    /**
     * parserOfAnswer
     * @param result - the result from the server
     * @return the answer
     */
    public static String parserOfAnswer(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            String status = obj.getString("status");
            String message = obj.getString("message");
            if (status.equals("1")) return null;
            else return message;
        } catch (Exception e) {e.printStackTrace();}
        return null;
    }
}