package com.biu.ap2.winder.ex4;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;



/**
 * ChatFragment - this class for fragment for the chat option
 */
public class ChatFragment extends Fragment {
    // list of all message in conversation
    List<Message> msgs;
    MessageAdapter msgadapter;
    ListView lstMsg;
    //for swipt down messages in conversation
    SwipeRefreshLayout swipeLayout;
    String chatName;
    GateWayService gws;
    String txt;
    Activity act;

    private static final String IPaddress = "192.168.43.151"; //pc address of winder computer
    private static final int port = 12345;

    //constructor
    public ChatFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        setRetainInstance(true);
        lstMsg = (ListView) view.findViewById(R.id.messages);
        act = getActivity();
        gws = new GateWayService(act.getApplicationContext());

        //check with whom do you speak
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            chatName = getActivity().getIntent().getExtras().getString("chat");
        }
        //create a new list (unnecessrey if we didnt initiate the conversation
        // for future app
        if (msgs == null && chatName != null) {
            msgs = gws.loadMsgs(chatName);
        } else {
            msgs = new ArrayList<>();
        }

        msgadapter = new MessageAdapter(getActivity(), msgs);
        lstMsg.setAdapter(msgadapter);

        // if list is change
        msgadapter.notifyDataSetChanged();

        // the swipe part if we roll up
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.chat_message_screen);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent intent = new Intent(getActivity(), ReloadService.class);
                getActivity().startService(intent);
            }
        });
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ReloadService.DONE);
        getActivity().registerReceiver(reloadDone, intentFilter);

        return view;
    }

    private BroadcastReceiver reloadDone = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            swipeLayout.setRefreshing(false);
            Toast.makeText(getActivity(), "Reload is done", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * addMsg
     * add new given message (m) to conversation
     * @param m
     */
    public void addMsg(Message m) {
        if (msgs == null) {
            msgs = new ArrayList<>();
        }
        msgs.add(m);
        gws.saveMsgs(msgs, chatName);
        //active map-reduce part if channel is movies
        if (chatName.equals("movie")) {
            txt = m.getMsg();
            // get sort
            getClassification();
        }
        if (msgadapter !=null) {
            msgadapter.notifyDataSetChanged();
        } else {
            msgadapter = new MessageAdapter(getActivity(), msgs);
            msgadapter.notifyDataSetChanged();

        }
    }

    /**
     * addMsgFromOutside
     * @param m - massage
     * @param chatname - chat name
     * @param c - context
     */
    public void addMsgFromOutside(Message m, String chatname, Context c) {
        GateWayService gws = new GateWayService(c);
        List<Message> msgs = gws.loadMsgs(chatname);
        msgs.add(m);
        gws.saveMsgs(msgs, chatname);
        //active map-reduce part if channel is movies
        if (chatname.equals("movie")) {
            txt = m.getMsg();
            // get sort
            getClassification();
            //so we have time to load txt
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void setData(List<Message> data) {
        this.msgs = data;
    }
    public List<Message> getData() {
        return msgs;
    }
    public String getName() {
        return chatName;
    }

    /**
     * getClassification
     * in separate thread- get movie review what sent to the channel "movie",
     * Analyze the review and write the answer: "positive" or "negitive"
     *
     */
    private void getClassification() {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket skt = null;
                DataInputStream in = null;
                DataOutputStream out = null;
                try {
                    String request = txt + "\n";

                    skt = new Socket();
                    skt.connect(new InetSocketAddress(IPaddress, port));

                    in = new DataInputStream(skt.getInputStream());
                    out = new DataOutputStream(skt.getOutputStream());

                    out.writeBytes(request);
                    String response = in.readLine();
                    //TODO what now with the classafication???
                    Toast.makeText(act.getApplicationContext(), response, Toast.LENGTH_LONG).show();
                    /*
                    if (response.equals("positive")) {
                        Toast.makeText(c, "movie review is positive", Toast.LENGTH_SHORT).show();
                    } else if (response.equals("negative")) {
                        Toast.makeText(c, "movie review is negative", Toast.LENGTH_SHORT).show();
                    }
                    */

                } catch (Exception e) {
                } finally {
                    try {in.close();} catch (Exception e) {}
                    try {out.close();} catch (Exception e) {}
                    try {skt.close();} catch (Exception e) {}
                }

            }
        });

        t.start();

    }

}

