package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class PVBMenu extends AppCompatActivity {


    EditText p1;
    RadioGroup difficultyGroup;
    RadioButton difficultyButton;
    int randomChance = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pvbmenu);
        p1 = findViewById(R.id.p1Name);
        difficultyGroup = findViewById(R.id.difficultyGroup);

    }

    public void startGame(View view) {
        Intent intent = new Intent(this, GameMain.class);
        String p1Name = p1.getText().toString();

        if(p1Name.equals("")){
            p1Name = "Player1";
        }


        difficultyButton = findViewById(difficultyGroup.getCheckedRadioButtonId());
        if(difficultyButton == null){
            Toast.makeText(getApplicationContext(), "You must select computer difficulty", Toast.LENGTH_SHORT).show();
            return;
        }
        String difficulty = difficultyButton.getText().toString();
        switch (difficulty){
            case "Easy":
                randomChance = 70;
                break;
            case "Medium":
                randomChance = 40;
                break;
            case "Hard":
                randomChance = 20;
                break;

            case "Unbeatable":
                randomChance = 0;
                break;
        }


        intent.putExtra("p1Name", p1Name);
        intent.putExtra("p2Name", "Computer");
        intent.putExtra("randomChance", randomChance);

        startActivity(intent);
    }
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item){

        int id = item.getItemId();
        if(id == R.id.back){
            finish();
        }

        if(id == R.id.htp){
            Intent intent = new Intent(this, HowToPlay.class);
            startActivity(intent);
        }

        if(id == R.id.pvp){
            Intent intent = new Intent(this, PVPMenu.class);
            startActivity(intent);
        }
        if(id == R.id.pvb){
            Intent intent = new Intent(this, PVBMenu.class);
            startActivity(intent);
        }

        if(id == R.id.onlineGameMenu){
            Intent intent = new Intent(this, OnlineGameMenu.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);


    }
}