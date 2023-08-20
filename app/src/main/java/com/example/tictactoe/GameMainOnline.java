package com.example.tictactoe;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.HashMap;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Objects;

public class GameMainOnline extends AppCompatActivity {

    private final String ROOMS = "rooms";

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference roomReference = databaseReference.child(ROOMS);

    String turn;
    String player1, player2;
    String roomCode;
    String winner = null;
    String currentPlayer;

    String[] board;
    Button[] buttonArray = new Button[9];

    HashMap<String, String> countries;

    TextView p1, p2, currentPlayerTv, p1Country, p2Country;

    Intent intent;

    LinearLayout linearLayout;

    boolean isOver = false;
    boolean isCurrentTurn;
    boolean playerLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_main_online);

        // Initialize board and first player's turn
        board = new String[9];
        countries = new HashMap<>();

        // Initializing variables from XML by ID
        currentPlayerTv = findViewById(R.id.turn);
        p1 = findViewById(R.id.p1);
        p2 = findViewById(R.id.p2);
        p1Country = findViewById(R.id.p1Country);
        p2Country = findViewById(R.id.p2Country);

        linearLayout = findViewById(R.id.linearLayout);

        buttonArray[0] = findViewById(R.id.b1);
        buttonArray[1] = findViewById(R.id.b2);
        buttonArray[2] = findViewById(R.id.b3);
        buttonArray[3] = findViewById(R.id.b4);
        buttonArray[4] = findViewById(R.id.b5);
        buttonArray[5] = findViewById(R.id.b6);
        buttonArray[6] = findViewById(R.id.b7);
        buttonArray[7] = findViewById(R.id.b8);
        buttonArray[8] = findViewById(R.id.b9);

        // Intents
        intent = getIntent();

        player1 = intent.getStringExtra("player1");
        player2 = intent.getStringExtra("player2");
        roomCode = intent.getStringExtra("roomCode");
        isCurrentTurn = intent.getBooleanExtra("isFirst", false);

        turn = isCurrentTurn ? "X" : "O";

        currentPlayer = player1;
//        currentPlayerTv.setText(turn + "'s \nTurn");

        p1.setText(player1);
        p2.setText(player2);

        // Initialize board values to 1/2/3/4/5/6/7/8/9
        for (int a = 0; a < 9; a++) {
            board[a] = String.valueOf(a + 1);
        }

        playerLeft = false;
        roomReference.child(roomCode).child("didPlayerLeave").setValue(false);

        updateCountry();
        asyncAcrossNetwork();

    }

    // Checks if there is a winner, if a winner is found or it a draw, if the game is not finished yet, return null.
    public String checkWinner() {
        for (int a = 0; a < 8; a++) {
            String line = null;

            switch (a) {
                case 0:
                    line = board[0] + board[1] + board[2];
                    break;
                case 1:
                    line = board[3] + board[4] + board[5];
                    break;
                case 2:
                    line = board[6] + board[7] + board[8];
                    break;
                case 3:
                    line = board[0] + board[3] + board[6];
                    break;
                case 4:
                    line = board[1] + board[4] + board[7];
                    break;
                case 5:
                    line = board[2] + board[5] + board[8];
                    break;
                case 6:
                    line = board[0] + board[4] + board[8];
                    break;
                case 7:
                    line = board[2] + board[4] + board[6];
                    break;
            }
            //For X winner
            if (line.equals("XXX")) {
                return "X";
            }

            // For O winner
            else if (line.equals("OOO")) {
                return "O";
            }
        }

        for (int a = 0; a < 9; a++) {
            if (Arrays.asList(board).contains(String.valueOf(a + 1))) {
                break;
            } else if (a == 8) {
                return "draw";
            }
        }
        return null;
    }

    public void stepGame() {
        if (isOver) return;
        winner = checkWinner();
        if (winner != null) {
            isOver = true;
            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    public boolean isTaken(Button button) {
        return !button.getText().toString().equals("");

    }

    public void selectButton(int i) {
        if (isTaken(buttonArray[i]) || checkWinner() != null || !isCurrentTurn) return;


        board[i] = turn;
        buttonArray[i].setText(turn);
        addTurnToDatabase(i, turn);

        stepGame();
    }

    public void updateBoard(int i, String turn) {
        board[i] = turn;
        buttonArray[i].setText(turn);
    }

    public void updateCountry(){
        roomReference.child(roomCode).child("playersCountry").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot country : snapshot.getChildren()){
                    countries.put(country.getKey(), country.getValue(String.class));
                }

                p1Country.setText("Unavailable");
                p2Country.setText("Unavailable");

                if(countries.get(player1) != null)
                    p1Country.setText(countries.get(player1));
                if(countries.get(player2) != null)
                    p2Country.setText(countries.get(player2));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });



    }


    public void asyncAcrossNetwork() {

        roomReference.child(roomCode).child("didPlayerLeave").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getValue(Boolean.class) == null) return;

                if (snapshot.getValue(Boolean.class)) {
                    isOver = true;
                    linearLayout.setVisibility(View.VISIBLE);
                    openAlertDialog("GAME OVER", "Opponent left the game");
                    roomReference.child(roomCode).child("didPlayerLeave").removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        roomReference.child(roomCode).child("turns").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isCurrentTurn = !isCurrentTurn;
                currentPlayer = currentPlayer.equals(player1) ? player2 : player1; // alternate the names.
                currentPlayerTv.setText(currentPlayer + "'s \nTurn");

                for (DataSnapshot turnsTaken : snapshot.getChildren()) {
                    String turnXO = turnsTaken.getValue(String.class);
                    int turnPosition = Integer.parseInt(Objects.requireNonNull(turnsTaken.getKey()));
                    if (turnXO != null) {
                        updateBoard(turnPosition, turnXO);
                    }
                }
                stepGame();
                if (isOver) {
                    roomReference.child(roomCode).child("turns").removeEventListener(this);
                    linearLayout.setVisibility(View.VISIBLE);
                    removeRoomReference();
                    if(winner == null) {
                        openAlertDialog("GAME OVER", "Opponent left the game");
                    }
                    else if (winner.equals("draw")) {
                        openAlertDialog("GAME OVER", "Game ended in a draw");
                    } else {
                        openAlertDialog("GAME OVER", "The winner is " + winner);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void removeRoomReference() {
        roomReference.child(roomCode).child("players").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount() == 0)
                    roomReference.child(roomCode).removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void addTurnToDatabase(int position, String turn) {

        roomReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomReference.child(roomCode).child("turns").child(position + "").setValue(turn);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void b1(View view) {
        selectButton(0);
    }

    public void b2(View view) {
        selectButton(1);
    }

    public void b3(View view) {
        selectButton(2);
    }

    public void b4(View view) {
        selectButton(3);
    }

    public void b5(View view) {
        selectButton(4);
    }

    public void b6(View view) {
        selectButton(5);
    }

    public void b7(View view) {
        selectButton(6);
    }

    public void b8(View view) {
        selectButton(7);
    }

    public void b9(View view) {
        selectButton(8);
    }


    // Moves to main menu
    public void mainMenu(View view) {
        Intent mainMenu = new Intent(this, MainActivity.class);
        removeRoomReference();
        startActivity(mainMenu);
    }


    @Override
    public void onBackPressed() {
        if(isOver)
            removeRoomReference();
        disconnect();

    }

    public void openAlertDialog(String title, String message) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(title);
        adb.setMessage(message);
        adb.setCancelable(true);
        adb.setPositiveButton("Close", (dialogInterface, i1) -> {

        });
        adb.create().show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isOver)
            removeRoomReference();
        disconnect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isOver)
            removeRoomReference();
        disconnect();
    }

    public void disconnect(){
        roomReference.child("turns").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!isOver)
                    roomReference.child(roomCode).child("didPlayerLeave").setValue(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Intent mainMenu = new Intent(this, MainActivity.class);
        startActivity(mainMenu);
    }






}