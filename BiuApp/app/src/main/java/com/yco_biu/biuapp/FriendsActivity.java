package com.biu.ap2.winder.ex4;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;



/**
 * FriendsActivity - this class activity to show the list of friends
 */
public class FriendsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.add(R.id.friendsFragment, new FriendsFragment());
        ft.commit();
    }
}
