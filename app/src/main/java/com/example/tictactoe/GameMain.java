package com.example.tictactoe;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class GameMain extends AppCompatActivity {

    String[] board;
    String turn;
    Button[] buttonArray = new Button[9];
    String winner = null;
    TextView p1, p2, currentPlayerTv;
    Intent intent;
    LinearLayout linearLayout;
    boolean isBot;
    int randomChance;
    boolean isOver = false;
    BotBrain bot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_main);

        board = new String[9];
        turn = "X";
        currentPlayerTv = findViewById(R.id.turn);
        currentPlayerTv.setText(turn + "'s \nTurn");

        p1 = findViewById(R.id.p1);
        p2 = findViewById(R.id.p2);
        linearLayout = findViewById(R.id.linearLayout);

        intent = getIntent();

        p1.setText(intent.getStringExtra("p1Name"));
        p2.setText(intent.getStringExtra("p2Name"));

        if (intent.getStringExtra("p2Name").equals("Computer")) {
            isBot = true;
            randomChance = intent.getIntExtra("randomChance", 100);
        }


        bot = new BotBrain(this, isBot, randomChance);

        for (int a = 0; a < 9; a++) {
            board[a] = String.valueOf(a + 1);
        }

        buttonArray[0] = findViewById(R.id.b1);
        buttonArray[1] = findViewById(R.id.b2);
        buttonArray[2] = findViewById(R.id.b3);
        buttonArray[3] = findViewById(R.id.b4);
        buttonArray[4] = findViewById(R.id.b5);
        buttonArray[5] = findViewById(R.id.b6);
        buttonArray[6] = findViewById(R.id.b7);
        buttonArray[7] = findViewById(R.id.b8);
        buttonArray[8] = findViewById(R.id.b9);


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
            if (Arrays.asList(board).contains(
                    String.valueOf(a + 1))) {
                break;
            } else if (a == 8) {
                return "draw";
            }
        }

        return null;
    }

    public String getCurrentPlayerName(){
        if(turn.equals("X")){
            return p1.getText().toString();
        }
        return p2.getText().toString();

    }


    public void stepGame() {
        if (isOver) return;
        winner = checkWinner();
        if (winner == null) {
            turn = turn.equals("X") ? "O" : "X";
            currentPlayerTv.setText(turn + "'s \nTurn");
        } else {
            isOver = true;
            linearLayout.setVisibility(View.VISIBLE);
            if (winner.equals("draw")) {
                openAlertDialog("GAME OVER", "Game ended in a draw");
            } else {
                openAlertDialog("GAME OVER", "The winner is " + winner + "\nWinner's Nickname: " + getCurrentPlayerName() );
            }
        }

        bot.botMove(); // only works if bot is selected
    }

    public boolean isTaken(Button button) {
        return !button.getText().toString().equals("");

    }

    public void selectButton(int i) {

        if (isTaken(buttonArray[i]) || checkWinner() != null) return;
        board[i] = turn;
        buttonArray[i].setText(turn);
        stepGame();
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


    public void playAgain(View view) {
        board = new String[9];
        turn = "X";
        currentPlayerTv = findViewById(R.id.turn);
        currentPlayerTv.setText(turn + "'s \nTurn");

        p1 = findViewById(R.id.p1);
        p2 = findViewById(R.id.p2);

        intent = getIntent();

        p1.setText(intent.getStringExtra("p1Name"));
        p2.setText(intent.getStringExtra("p2Name"));
        linearLayout.setVisibility(View.INVISIBLE);

        for (int a = 0; a < 9; a++) {
            board[a] = String.valueOf(a + 1);
        }
        for (int i = 0; i < 9; i++) {
            buttonArray[i].setText("");
        }
        isOver = false;

    }


    // Moves to main menu
    public void mainMenu(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}