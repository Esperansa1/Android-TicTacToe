package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class PVPMenu extends AppCompatActivity {

    EditText p1, p2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pvpmenu);

        p1 = findViewById(R.id.p1Name);
        p2 = findViewById(R.id.p2Name);


    }

    public void startGame(View view) {
        Intent intent = new Intent(this, GameMain.class);
        String p1Name = p1.getText().toString();
        String p2Name = p2.getText().toString();
        if(p1Name.equals("")){
            p1Name = "Player1";
        }
        if(p2Name.equals("")){
            p2Name = "Player2";
        }

        intent.putExtra("p1Name", p1Name);
        intent.putExtra("p2Name", p2Name);

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