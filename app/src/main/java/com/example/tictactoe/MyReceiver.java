package com.example.tictactoe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(isOnline(context)) Toast.makeText(context, "Connected to network!\n You can play with friends!", Toast.LENGTH_SHORT).show();
        else {
            Toast.makeText(context, "Disconnected from network\n You can not play in multiplayer😢", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isOnline(Context context) {

        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

}