package com.example.tictactoe;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver mNetworkReceiver;

    protected ServiceConnection mServerConn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mServerConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                Log.d("Main Activity", "onServiceConnected");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d("Main Activity", "onServiceDisconnected");
            }
        };
        mNetworkReceiver = new MyReceiver();

        registerNetworkChanges();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.SEND_SMS
            }, 1);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.FOREGROUND_SERVICE
            }, 2);
        }

        startMyService();

    }


    public void startMyService(){
        Intent serviceIntent = new Intent(this, OnClearFromRecentService.class);
        startService(serviceIntent);
        bindService(serviceIntent, mServerConn, Context.BIND_AUTO_CREATE);
        Log.w("SERVICE", "SERVICE STARTED AND BOUND");

    }

    private void registerNetworkChanges() {
        try {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }catch (Exception e){
        }
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d("DESTROY", "MainActivity Destroyed");
        super.onDestroy();
//        if(isClosed) {
//            isClosed = false;
//            return;
//        }
//        isClosed = true;
        unregisterNetworkChanges();
        Intent serviceIntent = new Intent(this, OnClearFromRecentService.class);
        unbindService(mServerConn);
        stopService(serviceIntent);

    }

    public boolean isOnline() {

        try {
            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();

        registerNetworkChanges();
        startMyService();
    }

    // Sends to Corresponding activity with button onClick

    public void goPvC(View view) {
        Intent intent = new Intent(this, PVBMenu.class);
        startActivity(intent);

    }
    public void goPvP(View view) {
        Intent intent = new Intent(this, PVPMenu.class);
        startActivity(intent);
    }
    public void goOnlineGameMenu(View view) {
        if(isOnline()) {
            Intent intent = new Intent(this, OnlineGameMenu.class);
            startActivity(intent);
        }else{
            Toast.makeText(getApplicationContext(), "Internet connection is unavailable.\n To play in this mode, please search for internet connection", Toast.LENGTH_SHORT).show();
        }
    }


    public void goHowToPlay(View view) {
        Intent intent = new Intent(this, HowToPlay.class);
        startActivity(intent);
    }
}