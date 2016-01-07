package com.biu.ap2.winder.ex4;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * this class represent single message
 * has: message, time, boolean true if message from me and false if came from friend
 * Created by winder on 6/1/15.
 */
public class Message implements Serializable {
    String msg;
    boolean fromMe;
    private String time;
    Double longtitude;
    Double latitude;
    String channel_id;
    String sender = null;

    //constructor
    public Message(String m, boolean from) {
        msg = m;
        fromMe = from;
        time = Long.toString(System.currentTimeMillis());
        sender = "Me";
    }

    //ido constructor
    public Message(String channel_id, String user_id, String text,
                   String data_time,String longtitude ,String latitude) {
        this.channel_id = channel_id;
        fromMe = false;
        this.sender = user_id;
        this.msg = text;
        this.time = data_time;
        this.longtitude = new Double(Double.parseDouble(longtitude));
        this.latitude = new Double(Double.parseDouble(latitude));
    }
    //improve constructor for me
    public Message(String user_id, String text, String data_time) {
        fromMe = false;
        this.sender = user_id;
        this.msg = text;
        this.time = data_time;
    }

    //standard getters
    public String getMsg() { return msg;}
    public boolean getFrom() { return fromMe;}
    public String getTime() {return time;}
    public LatLng getPos() {
        LatLng pos = new LatLng(this.latitude, this.longtitude);
        return pos;
    }
    public String getSender() { return this.sender;}
    public String getChannel() {return channel_id;}
}
