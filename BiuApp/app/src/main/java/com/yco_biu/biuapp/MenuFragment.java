package com.biu.ap2.winder.ex4;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;



/**
 * MenuFragment - for showing the menu
 */
public class MenuFragment extends Fragment {

    //constructor
    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        ListView lstMenu = (ListView) view.findViewById(R.id.lstMenu);

        List<MenuItem> menuItems = new ArrayList<MenuItem>() ;

        // add all menu field for app menu
        /*
        menuItems.add(new MenuItem("Map", R.mipmap.ic_action_place, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                startActivity(intent);
            }
        }));

        menuItems.add(new MenuItem("Chat", R.mipmap.ic_action_chat, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                startActivity(intent);
            }
        }));
        */
        menuItems.add(new MenuItem(getString(R.string.menu_friends) ,R.mipmap.ic_action_friends, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FriendsActivity.class);
                startActivity(intent);
            }
        }));

        menuItems.add(new MenuItem(getString(R.string.menu_setting), R.mipmap.ic_action_setting, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        }));
        menuItems.add(new MenuItem(getString(R.string.menu_addfriend), R.mipmap.ic_action_addfriend, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                startActivity(intent);
            }
        }));
        menuItems.add(new MenuItem(getString(R.string.menu_home), R.drawable.home, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = getActivity().getApplicationContext();
                GateWayService gws = new GateWayService(c);
                gws.ChangeServer("project-ocdi.appspot.com");
            }
        }));
        menuItems.add(new MenuItem(getString(R.string.menu_update), R.mipmap.ic_action_update, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = getActivity().getApplicationContext();
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
                String url = sharedPref.getString("currentIP", "project-ocdi.appspot.com");
                GetUpdatesParser gup = new GetUpdatesParser(c);
                gup.execute(url);
            }
        }));

        MenuAdapter menuAdapter = new MenuAdapter(getActivity(), menuItems);
        lstMenu.setAdapter(menuAdapter);

        return view;
    }



}
