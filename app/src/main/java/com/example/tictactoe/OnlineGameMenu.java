package com.example.tictactoe;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class OnlineGameMenu extends AppCompatActivity {

    private final String ROOMS = "rooms";

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference roomReference = databaseReference.child(ROOMS);
    EditText roomCodeJoin, roomCodeCreate, playerName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_game_menu);
        roomCodeCreate = findViewById(R.id.roomCodeCreate);
        roomCodeJoin = findViewById(R.id.roomCodeJoin);
        playerName = findViewById(R.id.playerName);

    }


    public void joinRoom(View view) {

        String playerStr = playerName.getText().toString();

        String roomCode = roomCodeJoin.getText().toString();
        if (roomCode.equals("") || playerStr.equals("")) return;

        roomReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot room : snapshot.getChildren()) {
                    if (Objects.requireNonNull(room.getKey()).equals(roomCode)) {
                        if (room.child("players").getChildrenCount() >= 2) {
                            Toast.makeText(getApplicationContext(), "No room in lobby", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (DataSnapshot player : room.child("players").getChildren()) {
                            if (player.getKey().equals(playerStr)) {
                                Toast.makeText(getApplicationContext(), "Player name is already taken", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        roomReference.child(roomCode).child("players").child(playerStr).setValue("");
                        roomReference.removeEventListener(this);
                        moveToWaiting(roomCode, playerStr);
                        return;
                    }
                }
                Toast.makeText(getApplicationContext(), "Room does not exist", Toast.LENGTH_SHORT).show(); // Only calls if room does not exist as it is not going to waiting nor it is full -> no room
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void createRoom(View view) {
        String playerStr = playerName.getText().toString();

        String roomCode = roomCodeCreate.getText().toString();
        if (roomCode.equals("") || playerStr.equals("")) return;

        roomReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot room : snapshot.getChildren()) {
                    if (Objects.requireNonNull(room.getKey()).equals(roomCode)) {
                        Toast.makeText(getApplicationContext(), "Room already exists", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                roomReference.child(roomCode).child("players").child(playerStr).setValue("");
                Toast.makeText(getApplicationContext(), "Created room " + roomCode, Toast.LENGTH_LONG).show();
                roomReference.removeEventListener(this);
                moveToWaiting(roomCode, playerStr);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    public void moveToWaiting(String roomCode, String player) {
        if(!isOnline()){
            Toast.makeText(getApplicationContext(), "Internet connection is unavailable.\n To play in this mode, please search for internet connection", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent onlineWaitingRoom = new Intent(this, OnlineWaitingRoom.class);
        onlineWaitingRoom.putExtra("roomCode", roomCode);
        onlineWaitingRoom.putExtra("playerDevice", player);
        startActivity(onlineWaitingRoom);
    }


}