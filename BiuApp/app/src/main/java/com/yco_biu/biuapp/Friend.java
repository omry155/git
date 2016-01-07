package com.biu.ap2.winder.ex4;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * class Friend
 * has: name (string), image and status and lisener, also position
 * Created by winder on 5/27/15.
 */
public class Friend { //implements Serializable {
    private String name;
    private String id;
    private String img;
    //private String status;
    View.OnClickListener listener;
    LatLng pos;

    public final static String pic_none = "iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAMAAACdt4HsAAAAe1BMVEX///8tLS22trZHR0dAQEA9PT1FRUUyMjI6OjpcXFxLS0tkZGRpaWk/Pz9ycnJsbGx0dHRQUFBTU1NZWVl6enovLy9gYGCXl5f5+fmBgYGPj482NjbR0dHv7++Hh4eAgIDd3d0eHh6mpqYlJSXCwsLT09Pl5eWdnZ3ExMSw5FevAAAB/ElEQVRYhe2W2XKjMBBF2bUioQVhoxgTj+P4/79wgEnGbA5ppyZVU5Xzfk+1tm4FwQ//lEvFCS8EJyJ6KF8bW1d1XVdVw/kFHN8JVjf740BTH8kBKtjzxt5oLNvB8k9kb8c08gQTODujMaASXthcYHkLEVSyWFBBBFIs8pZDBE4sYf+XgMglXxY40BL4ku8VkBUBgQgMW2IgAr4iAJ1CQxZxZyGCyLg5BtQQWr0UXCGCgMxLIArW0yJNpmhQO+iasjJTAYU29rOaFlAD80EglLmhgTvQ044FCnYEK4IXuKDSIxToGg6cYjcSGCphm3AQ1JpxCYWhZ0D+Gjtr1BgtBCVPn0u3r4qKQqsZzrKM7qONMb+LWB4baZmiCzS3klAc2/uFXGxOWVGItfigcN3I5CaOq9U6WpIaIZm5k36XdN8mqdLj4lAOAhvpdHyfvw7VTQ2azz5ekVfso/QMzVgix/mjJzrOPmaqUCwjt2W43GTJRn5KksQkK97ztTdpmmwxFaRJptHbgR5ChfFmfgbGeRK/zZqTxx7nm0wFqfdJEv4RKI8QTjeZ6tIuk4ftcH2ffVl6PGFbhxFCafnaC65hJygxEFSWCJfDz60KexCQPoIR7QU8fBTsfS+g5aMC73/1gjL5/BuY89w94nPz8BLC8Ny9yTb6Ene70w/fyG9C3Efp1zEvkAAAAABJRU5ErkJggg==";
    private final static String delimiter = "$";

    // constructor - with all data
    public Friend(String n, String i, String s, View.OnClickListener l) {
        name = n;
        img = i;
        id = s;
        listener = l;
        pos = null;
    }

    // constructor for ido - new used in add channel
    public Friend(String id, String name, String icon) {
        this.id = id;
        this.name = name;
        this.img = icon;
        //status = null;
        listener = null;
        pos = null;

    }
    // constructor when loading from memory
    public Friend(String s) {
        //break string into parts with given delimiter
        // extract all data from the string
        String[] a = s.split("\\$");
        name = a[0];
        img = a[1];
        id = a[2];
        String lat = a[3];
        String lon = a[4];


   /*
        int index = s.indexOf(delimiter);
        name = s.substring(0, index);
        s = s.substring(index + 1, s.length());
        index = s.indexOf(delimiter);
        String im = s.substring(0, index);
        img = im;
        s = s.substring(index + 1, s.length());
        index = s.indexOf(delimiter);
        id = s.substring(0, index);
        s = s.substring(index + 1, s.length());
        index = s.indexOf(delimiter);
        String lat = s.substring(0, index);
        s = s.substring(index + 1, s.length());
        index = s.indexOf(delimiter);
        String lon = s.substring(0, index);
*/
        pos = null;
        if (!lat.equals("none")) {
            double lat1 = Double.parseDouble(lat);
            double lon1 = Double.parseDouble(lon);
            pos = new LatLng(lat1, lon1);
        }
        listener = null;
    }

    // regular getter and setter
    public void setName(String name) { this.name = name; }
    public void setImg(String img) { this.img = img; }
   // public void setStatus(String status) {
   //     this.status = status;
   // }

    public void setLisener(View.OnClickListener l) {
        listener = l;
    }
    public View.OnClickListener getListener(){
        return this.listener;
    }

    public String getName() {
        return name;
    }
    public String getID() {
        return id;
    }
    public String getImg() {
        return img;
    }
   // public String getStatus() { return status; }

    @Override
    public String toString() {
        String im = this.img;
        String lat, lon;
        if (pos == null) {
            lat = "none";
            lon = "none";
        } else {
            lat = Double.toString(pos.latitude);
            lon = Double.toString(pos.longitude);
        }
        String s = name + delimiter + im + delimiter + id + delimiter + lat + delimiter + lon + delimiter;
        return s;
    }
}

