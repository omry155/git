package com.biu.ap2.winder.ex4;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/*
 * this class will be activity for the add-friend screen
 * winder david - 020615
 */
public class AddFriendActivity extends BaseActivity {
    List<Friend> nfl;
    String url;
    List<String> picliststring;
    ArrayAdapter<String> picAdapter;
    Spinner spinner;
    public final static String pic_none = "iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAMAAACdt4HsAAAAe1BMVEX///8tLS22trZHR0dAQEA9PT1FRUUyMjI6OjpcXFxLS0tkZGRpaWk/Pz9ycnJsbGx0dHRQUFBTU1NZWVl6enovLy9gYGCXl5f5+fmBgYGPj482NjbR0dHv7++Hh4eAgIDd3d0eHh6mpqYlJSXCwsLT09Pl5eWdnZ3ExMSw5FevAAAB/ElEQVRYhe2W2XKjMBBF2bUioQVhoxgTj+P4/79wgEnGbA5ppyZVU5Xzfk+1tm4FwQ//lEvFCS8EJyJ6KF8bW1d1XVdVw/kFHN8JVjf740BTH8kBKtjzxt5oLNvB8k9kb8c08gQTODujMaASXthcYHkLEVSyWFBBBFIs8pZDBE4sYf+XgMglXxY40BL4ku8VkBUBgQgMW2IgAr4iAJ1CQxZxZyGCyLg5BtQQWr0UXCGCgMxLIArW0yJNpmhQO+iasjJTAYU29rOaFlAD80EglLmhgTvQ044FCnYEK4IXuKDSIxToGg6cYjcSGCphm3AQ1JpxCYWhZ0D+Gjtr1BgtBCVPn0u3r4qKQqsZzrKM7qONMb+LWB4baZmiCzS3klAc2/uFXGxOWVGItfigcN3I5CaOq9U6WpIaIZm5k36XdN8mqdLj4lAOAhvpdHyfvw7VTQ2azz5ekVfso/QMzVgix/mjJzrOPmaqUCwjt2W43GTJRn5KksQkK97ztTdpmmwxFaRJptHbgR5ChfFmfgbGeRK/zZqTxx7nm0wFqfdJEv4RKI8QTjeZ6tIuk4ftcH2ffVl6PGFbhxFCafnaC65hJygxEFSWCJfDz60KexCQPoIR7QU8fBTsfS+g5aMC73/1gjL5/BuY89w94nPz8BLC8Ny9yTb6Ene70w/fyG9C3Efp1zEvkAAAAABJRU5ErkJggg==";

    /**
     * onCreate
     * @param savedInstanceState - Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        this.url = sharedPref.getString("currentIP", "project-ocdi.appspot.com");
        //create the button to add a friend. transfer data through intent
        Button b = (Button) findViewById(R.id.addfriend_addbtn);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the data from the text field
                EditText n1 = (EditText) findViewById(R.id.addfriend_name);
                EditText i1 = (EditText) findViewById(R.id.addfriend_id);
                String name = n1.getText().toString();
                String id = i1.getText().toString();
                String icon;

                String selectedpic = picliststring.get(spinner.getSelectedItemPosition());
                switch(selectedpic) {
                    case "movie":
                        icon = SettingActivity.pic64_movie;
                        break;
                    case "work":
                        icon = SettingActivity.pic64_work;
                        break;
                    case "home":
                        icon = SettingActivity.pic64_home;
                        break;
                    case "project":
                        icon = SettingActivity.pic64_project;
                        break;
                    case "university":
                        icon = SettingActivity.pic64_university;
                        break;
                    default:
                        icon = AddFriendActivity.pic_none;
                }
                // ic_action_update the server of the new channel
                new AddChannelRequst(getApplicationContext()).execute(url, id, name, icon);

                Intent i = new Intent(AddFriendActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });


        picliststring = new ArrayList<String>();
        picliststring.add("movie");
        picliststring.add("work");
        picliststring.add("home");
        picliststring.add("project");
        picliststring.add("university");
        spinner = (Spinner) findViewById(R.id.addfriend_pics);
        picAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, picliststring);
        picAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(picAdapter);


    }

    /**
     * onCreateOptionsMenu
     * @param menu
     * @return ture for success
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_friend, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * class AddChannelRequst- operate the method getChannels,
     * parse the output and update with GateWayService
     */
    private class AddChannelRequst extends AsyncTask<String, String, String> {
        String text = null;
        Context c;
        Friend f;

        public AddChannelRequst(Context context) {
            this.c = context;
        }

        /**
         * doInBackground
         * @param params- url of the server, id, name, icon of the added channel
         * @return string: the json object of the answer from the server
         */
        protected String doInBackground(String... params) {
            try {
                HttpContext localContext = new BasicHttpContext();
                HttpClient httpClient = LoginActivity.httpClient;
                f = new Friend(params[1], params[2], params[3]);

                HttpPost httpPost = new HttpPost("http://" + params[0] + "/addChannel");
                ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
                parameters.add(new BasicNameValuePair("id", params[1]));
                parameters.add(new BasicNameValuePair("name", params[2]));
                parameters.add(new BasicNameValuePair("icon", params[3]));
                httpPost.setEntity(new UrlEncodedFormEntity(parameters));

                HttpResponse response = httpClient.execute(httpPost, localContext);
                HttpEntity entity = response.getEntity();
                text = getASCIIContentFromEntity(entity);

                return text;

            } catch (Exception e) {}

            return null;
        }

        /**
         * onPostExecute
         * @param result - the json object of the answer from the server
         * function operation: parse the json object and update with GateWayService
         */
        protected void onPostExecute(String result) {
            if (result == null)
                Toast.makeText(c, "Channel not added - ERROR", Toast.LENGTH_LONG).show();
            String msg = LoginTask.parserOfAnswer(result);
            if (msg == null) {
                Toast.makeText(c, "Channel added", Toast.LENGTH_LONG).show();
                Thread tLoader = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(AddFriendActivity.this, GetDailyService.class);
                        intent.putExtra("time", "now");
                        startService(intent);
                        //update GateWayService
                        GateWayService gws = new GateWayService(c);
                        //load channels from GateWayService
                        nfl = gws.loadFriendList();
                        nfl.add(f);
                        gws.saveFriendList(nfl);

                    }
                });
                tLoader.start();
            } else {
                Toast.makeText(c, "Channel not added: " + msg, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * getASCIIContentFromEntity
     * @param entity
     * @return string- the json object of the answer from the server
     * @throws IllegalStateException
     * @throws IOException
     */
    protected String getASCIIContentFromEntity(HttpEntity entity)
            throws IllegalStateException, IOException {
        InputStream in = entity.getContent();
        StringBuffer out = new StringBuffer();
        int n = 1;
        while (n > 0) {
            byte[] b = new byte[4096];
            n = in.read(b);
            if (n > 0)
                out.append(new String(b, 0, n));
        }
        return out.toString();
    }






}
