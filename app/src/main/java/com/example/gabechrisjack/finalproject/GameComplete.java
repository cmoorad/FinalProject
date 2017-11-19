package com.example.gabechrisjack.finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;



/**
 * Created by ucla201 on 11/18/17.
 */

public class GameComplete extends AppCompatActivity {

    public View view;
    public String user;
    public String pass;

    public GameComplete() {
    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_complete);

        view = (LinearLayout) findViewById(R.id.gameview);

        Intent i = getIntent();
        user = i.getStringExtra("user");
        pass = i.getStringExtra("pass");

        TextView congrats = findViewById(R.id.congrats);
        congrats.setText("Congratulations " + user + "!");
    }

    public void quitGame(View v) {
        Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.putExtra("user", user);
        myIntent.putExtra("pass", pass);
        myIntent.putExtra("rivergame", true);
        myIntent.putExtra("urbangame", true);
        myIntent.putExtra("forestgame", true);
        startActivity(myIntent);
    }

    public void returnToGame(View v) {
        Intent myIntent = new Intent(this, Game.class);
        myIntent.putExtra("user", user);
        myIntent.putExtra("pass", pass);
        myIntent.putExtra("rivergame", true);
        myIntent.putExtra("urbangame", true);
        myIntent.putExtra("forestgame", true);
        myIntent.putExtra("gameFinished", true);
        startActivity(myIntent);
    }


    public Context getActivity() {
        return this;
    }
}
