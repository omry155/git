package com.biu.ap2.winder.ex4;



import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;

import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;




/**
 * MapsActivity - this is our main class to show main app
 */
public class MapsActivity extends BaseActivity implements
        GoogleApiClient.OnConnectionFailedListener, SensorEventListener, LocationListener, com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks {

    // has: map and location
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    Location lastlocation;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    // for the sweep transformer
    SensorManager sensorManager;
    boolean flag;
    //for the friends list display
    FriendsFragment ff;
    //for swipt down messages in conversation
    SwipeRefreshLayout swipeLayout;
    String currentIP;

    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    private static final int SHAKE_THRESHOLD = 6000;

    float last_x, last_y, last_z;
    private long lastUpdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        lastUpdate = System.currentTimeMillis();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ff = new FriendsFragment();
        setUpMapIfNeeded();

        //make sure you have location services
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        // to open menu fragment
        Button b = (Button) findViewById(R.id.menu_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                if (fm.getBackStackEntryCount() > 0)
                    getFragmentManager().popBackStack();
                else {
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.add(R.id.menuFragment, new MenuFragment());
                    ft.addToBackStack("menu");
                    ft.commit();
                }
            }
        });


        createLocationRequest();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // if orientation is landscape add friend list
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ft.add(R.id.friendsFragment, ff);
        } else {
            // the swipe bar will only appear in portrait mode
            // the sweep part
            ft.add(R.id.friendsFragment, ff);
            swipeLayout = (SwipeRefreshLayout) this.findViewById(R.id.sweep_surface);
            swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
            swipeLayout.setOnTouchListener(new OnSwipeTouchListener(this) {
                @Override
                public void onSwipeLeft() {
                    flag = !flag;
                    changeView();
                }

                @Override
                public void onSwipeRight() {
                    flag = !flag;
                    changeView();
                }
            });
            // the swipt part if we roll up
            swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Intent intent = new Intent(MapsActivity.this, ReloadService.class);
                    startService(intent);
                }
            });
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ReloadService.DONE);
            this.registerReceiver(reloadDone, intentFilter);

        }
        ft.commit();
        this.serviceStarter();


    }

    /**
     * serviceStarter - start the update server
     * @return true for success
     */
    private boolean serviceStarter() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        String isUpdating = sharedPref.getString("DailyUpdate", "0");
        this.currentIP = sharedPref.getString("currentIP", "project-ocdi.appspot.com");
        Thread tLoader2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MapsActivity.this, GetDailyService.class);
                intent.putExtra("time", "day");
                intent.putExtra("url", currentIP);
                startService(intent);
            }
        });
        Thread tLoader3 = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MapsActivity.this, GetUpdateService.class);
                startService(intent);
            }
        });
        //TODO remove after debug
        isUpdating = "0";
        if (isUpdating.equals("0")) {
            //tLoader2.start();
            tLoader3.start();
            editor.putString("DailyUpdate", "1");
            editor.commit();
            return true;
        }
        return false;

    }

    private BroadcastReceiver reloadDone = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            swipeLayout.setRefreshing(false);
            Toast.makeText(getApplicationContext(), "Reload is done", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * changeView - flipper
     */
    private void changeView() {
        ViewFlipper vf = (ViewFlipper) findViewById(R.id.viewFlipper);
        if (flag) {
            vf.showNext();
        } else
            vf.showPrevious();
    }


    // map activity methods
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }


    @Override
    public void onBackPressed() {
        if (this.getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else
            super.onBackPressed();
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    private void setUpFriendOnMap() {
        GateWayService gws = new GateWayService(getApplicationContext());
        List<Client> cl = gws.loadClientList();
        for (Client c : cl) {
            String name = c.name;
            LatLng pos = new LatLng(c.getLat(), c.getLon());
            float color = Float.parseFloat(c.channel_id);
            if (Float.isNaN(color)) {color = 200;}
            //set different color for each channel
            color = color % 360;
            mMap.addMarker(new MarkerOptions().position(pos).title(name).icon(BitmapDescriptorFactory.defaultMarker(color)));
        }
    }


    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(pos1).title("Marker").snippet("test"));
    }

    @Override
    public void onLocationChanged(final Location location) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // save last location info
                lastlocation = location;
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("lat", Double.toString(location.getLatitude()));
                editor.putString("lon", Double.toString(location.getLongitude()));
                editor.commit();

                mMap.clear();
                // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to Mountain View
                        .zoom(17)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("you are here!"));
                setUpFriendOnMap();
            }

        });
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Failed to connect to location updates", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(Bundle bundle) {
        //Toast.makeText(this, "Starting location updates", Toast.LENGTH_SHORT).show();
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        sensorManager.unregisterListener(this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
        setUpMapIfNeeded();
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        long curTime = System.currentTimeMillis();
        // only allow one ic_action_update every 100ms.
        if ((curTime - lastUpdate) > 100) {
            long diffTime = (curTime - lastUpdate);
            lastUpdate = curTime;

            float x = values[0];
            float y = values[1];
            float z = values[2];

            float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime;
            speed = speed * 100000;
            if (swipeLayout == null)
                return;

            if (speed > SHAKE_THRESHOLD) {
                swipeLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(false);
                    }
                }, 3000);
            }
            last_x = x;
            last_y = y;
            last_z = z;
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}



/*
                //regular alarm
                AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

                Long timeToAlert = new GregorianCalendar().getTimeInMillis() + 5000;

                Intent intent = new Intent(this, NotificationReceiver.class);
                intent.putExtra("sender", "one");


                alarmMgr.set(AlarmManager.RTC_WAKEUP, timeToAlert, PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));

                Intent intent2 = new Intent(this, NotificationReceiver.class);
                intent2.putExtra("sender", "two");
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, timeToAlert + 5000, 30000, PendingIntent.getBroadcast(this, 1, intent2, PendingIntent.FLAG_UPDATE_CURRENT));
                alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, timeToAlert + 5000, 30000, PendingIntent.getBroadcast(this, 1, intent2, PendingIntent.FLAG_UPDATE_CURRENT));

                Toast.makeText(this, "Alarms were set", Toast.LENGTH_SHORT).show();
                */







     /*
    //generate fake friends for ex4 only!
    private void setUpFakeFriend() {

        LatLng pos1, pos2, pos3, pos4;
        if (lastlocation != null) {
            pos1 = new LatLng(lastlocation.getLatitude() + 0.004, lastlocation.getLongitude() + 0.004);
            pos2 = new LatLng(lastlocation.getLatitude() + 0.004, lastlocation.getLongitude() - 0.004);
            pos3 = new LatLng(lastlocation.getLatitude() - 0.004, lastlocation.getLongitude() + 0.004);
            pos4 = new LatLng(lastlocation.getLatitude() - 0.004, lastlocation.getLongitude() - 0.004);
        } else {
            pos1 = new LatLng(32.079500, 34.848200);
            pos2 = new LatLng(32.077500, 34.845200);
            pos3 = new LatLng(32.075500, 34.849200);
            pos4 = new LatLng(32.077500, 34.850200);
        }
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker m) {
                ContextThemeWrapper cw = new ContextThemeWrapper(
                        getApplicationContext(), R.style.Transparent);
                LayoutInflater inflater = (LayoutInflater) cw
                        .getSystemService(LAYOUT_INFLATER_SERVICE);

                // Getting view from the layout file
                View v = inflater.inflate(R.layout.friend_map_marker, null);
                TextView name = (TextView) v.findViewById(R.id.marker_name);
                TextView status = (TextView) v.findViewById(R.id.marker_status);
                ImageView img = (ImageView) v.findViewById(R.id.marker_imgProfile);

                //extract data - name and status
                String data = m.getTitle();
                String[] s = data.split("-");

                //insert data to info window
                name.setText(s[0]);
                if (s[0].length() != data.length()) {
                    status.setText(s[1]);
                }
                if (m.getSnippet() != null) {
                    img.setImageResource(Integer.decode(m.getSnippet()));
                }
                return v;
            }

            @Override
            public View getInfoContents(Marker arg0) {
                return null;
            }
        });

        if (ff.friends != null) {
            Friend f1 = ff.friends.get(1);
            Friend f2 = ff.friends.get(2);
            Friend f3 = ff.friends.get(3);
            Friend f4 = ff.friends.get(ff.friends.size()-1);
            String data1 = f1.getName() + "-" + f1.getID();
            String img1 = f1.getImg();
            mMap.addMarker(new MarkerOptions().position(pos1).title(data1).snippet(img1));
            String data2 = f2.getName() + "-" + f2.getID();
            String img2 = f2.getImg();
            mMap.addMarker(new MarkerOptions().position(pos2).title(data2).snippet(img2));
            String data3 = f3.getName() + "-" + f3.getID();
            String img3 = f3.getImg();
            mMap.addMarker(new MarkerOptions().position(pos3).title(data3).snippet(img3));
            String data4 = f4.getName() + "-" + f4.getID();
            String img4 = f4.getImg();
            mMap.addMarker(new MarkerOptions().position(pos4).title(data4).snippet(img4));
        }


    }
    */

                /*
             swipeLayout.setRefreshing(true);

            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeLayout.setRefreshing(false);
                }
            }, 3000);


            /*


            swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                   // Intent intent = new Intent(getApplication(), ReloadService.class);
                    Intent intent = new Intent(this, ReloadService.class);
                    startService(intent);
                }
            });
            swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ReloadService.DONE);
            this.registerReceiver(reloadDone, intentFilter);
*/



