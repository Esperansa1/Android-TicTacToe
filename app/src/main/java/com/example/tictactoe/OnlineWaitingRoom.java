package com.example.tictactoe;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

public class OnlineWaitingRoom extends AppCompatActivity implements LocationListener {

    Button startBtn;
    Intent intent;
    String roomCode;
    private final String ROOMS = "rooms";
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference roomsReference = databaseReference.child(ROOMS);
    DatabaseReference roomReference;
    TextView p1, p2;
    TextView codeTv;
    String thisPlayer;
    String[] players;
    ValueEventListener isGameReadyVL;
    ValueEventListener isStartedVL;
    LocationManager locationManager;
    EditText phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_waiting_room);
        intent = getIntent();
        thisPlayer = intent.getStringExtra("playerDevice");

        roomCode = intent.getStringExtra("roomCode");
        codeTv = findViewById(R.id.code);
        p1 = findViewById(R.id.p1);
        p2 = findViewById(R.id.p2);
        phoneNumber = findViewById(R.id.phoneNumber);

        startBtn = findViewById(R.id.startBtn);
        codeTv.setText(roomCode);
        roomReference = roomsReference.child(roomCode);
        roomReference.child("isStarted").setValue("false");


        players = new String[2];
        addPlayerCountryToFirebase();
        isGameReady();


        // If the game starts on one phone force it to start on the other one aswell
        Intent gameMainOnline = new Intent(this, GameMainOnline.class);

        isStartedVL = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getValue(String.class) == null) return;

                if (snapshot.getValue(String.class).equals("false")) return;


                String player1 = players[0];
                String player2 = players[1]; // Sets the opponent to be the other player in the player list

                boolean isFirst = thisPlayer.equals(player1);

                gameMainOnline.putExtra("player1", player1);
                gameMainOnline.putExtra("player2", player2);
                gameMainOnline.putExtra("isFirst", isFirst);
                gameMainOnline.putExtra("roomCode", roomCode);
                roomReference.child("isStarted").removeEventListener(this);
                roomReference.child("players").removeEventListener(isGameReadyVL);
                startActivity(gameMainOnline);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

        roomReference.child("isStarted").addValueEventListener(isStartedVL);

    }

    public void isGameReady() {
        isGameReadyVL = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                players = new String[2]; // reset player array
                int playerPos = 0;
                for (DataSnapshot player : snapshot.getChildren()) {
                    players[playerPos] = player.getKey();
                    playerPos++;
                }
                updateTextview(); // Updates text view to show connected players' names
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        roomReference.child("players").addValueEventListener(isGameReadyVL);

    }

    public void updateTextview() { // Runs on player added/removed, shows which players are connected to lobby and if 2 players are connected, make

        p1.setText("Not Connected Yet"); // resets current text displayed for p1, p2 text views
        p2.setText("Not Connected Yet");

        startBtn.setVisibility(View.INVISIBLE);

        if (players[0] != null) {
            p1.setText(players[0]);
        }
        if (players[1] != null) {
            p2.setText(players[1]);

        }
        if (players[0] != null && players[1] != null) {
            startBtn.setVisibility(View.VISIBLE);
            roomReference.child("isStarted").setValue("false"); // checks if either player pressed the start game button
        }
    }


    public void startGame(View view) {
        roomReference.child("isStarted").setValue("true"); //  sets  player pressed the start game button to true
    }

    public void removePlayer() {

        roomReference.child("playersCountry").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot player : snapshot.getChildren()) {
                    if (player.getKey().equals(thisPlayer)) {
                        roomReference.child("playersCountry").child(thisPlayer).removeValue();
                        return;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        roomReference.child("players").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot player : snapshot.getChildren()) {
                    if (player.getKey().equals(thisPlayer)) {
                        roomReference.child("players").child(thisPlayer).removeValue();
                        return;
                    }
                }
                if (snapshot.getChildrenCount() == 0) {
                    roomReference.child("isStarted").removeEventListener(isStartedVL);
                    roomReference.removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }

    public String getUserCountry() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                }, 100);
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Geocoder gcd = new Geocoder(this, Locale.getDefault());

            List<Address> addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (addresses.size() > 0) {
                return addresses.get(0).getCountryName(); // returns country name
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Unable to get location", Toast.LENGTH_SHORT).show();
        }
        return null; // returns null if not found
    }

    public void addPlayerCountryToFirebase() {
        roomReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String country = getUserCountry();
                roomReference.child("playersCountry").child(thisPlayer).setValue(country);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        removePlayer();
        finish();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.SEND_SMS
            }, 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onBackPressed();
    }
    @Override
    protected void onStop() {
        super.onStop();
        onBackPressed();
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {

    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    public void sendSms(View view) {

        String phoneNumberSt = phoneNumber.getText().toString();

        if (phoneNumber.equals("")) {
            Toast.makeText(this, "Must fill phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            String message = "Hey! I am waiting for you to join my tic tac toe game! \nMy room's code is: " + roomCode;
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumberSt, null, message, null, null);
            Toast.makeText(this, "SMS Sent Successfully", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Access to feature has been denied by the user", Toast.LENGTH_SHORT).show();
        }

    }




}