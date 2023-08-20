package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class HowToPlay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.how_to_play);
    }

    public void gameRules(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fv1, GameRulesFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack("Game Rules")
                .commit();


    }

    public void appManual(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fv1, ApplicationManual.class, null)
                .setReorderingAllowed(true)
                .addToBackStack("Game Rules")
                .commit();


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