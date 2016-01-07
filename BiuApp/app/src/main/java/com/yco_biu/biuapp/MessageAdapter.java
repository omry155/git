package com.biu.ap2.winder.ex4;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import static com.biu.ap2.winder.ex4.R.drawable.bubble_1;

/**
 * this class adapter to show messages in conversation
 * Created by winder on 6/1/15.
 */
public class MessageAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Message> msgs;

    //constructor
    public MessageAdapter(Activity activity, List<Message> msgsItem) {
        this.activity = activity;
        this.msgs = msgsItem;
    }

    // implement methods as adapter
    @Override
    public int getCount() {
        return msgs.size();
    }
    @Override
    public Object getItem(int position) {
        return msgs.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_item_message, null);

        //connect the variables to xml field
        LinearLayout ll = (LinearLayout) convertView.findViewById(R.id.one_msg);
        TextView msg = (TextView) convertView.findViewById(R.id.msg_msg);
        TextView sender = (TextView) convertView.findViewById(R.id.msg_sender);
        TextView time = (TextView) convertView.findViewById(R.id.msg_time);

        //get message data and insert to xml
        Message item = msgs.get(position);
        boolean from = item.getFrom();
        msg.setText(item.getMsg());
        sender.setText(item.getSender());
        time.setText(item.getTime());


        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.FILL_PARENT);
        //define gravity as start (left) if message came not from me and end otherwise
        if (from) {
            params.gravity = Gravity.END;
            ll.setBackgroundResource(R.drawable.bubble_1);
            msg.setPadding(30, 0, 30, 30);
        } else {
            params.gravity = Gravity.START;
            msg.setText(item.getMsg());
            ll.setBackgroundResource(R.drawable.bubble_2);  //android:paddingTop="20dip"
            msg.setPadding(30,20,30,0);
            sender.setText(item.getSender());
        }

        ll.setLayoutParams(params);
        return convertView;
    }

    public void addMessage(Message m) {
        msgs.add(m);
    }
}
