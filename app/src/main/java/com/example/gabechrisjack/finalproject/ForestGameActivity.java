package com.example.gabechrisjack.finalproject;

/**
 * Created by Jack Taylor on 11/18/17.
 * Based on Simplified Coding "Android Game Development Tutorial"
 * url: https://www.simplifiedcoding.net/android-game-development-tutorial-1/#Building-Game-View
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;

public class ForestGameActivity extends AppCompatActivity {

    public String user;
    public String pass;
    public boolean rivergame = false;
    public boolean urbangame = false;
    public boolean forestgame = false;

    //declaring gameview
    private ForestGameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Getting display object
        Display display = getWindowManager().getDefaultDisplay();

        //Getting the screen resolution into point object
        Point size = new Point();
        display.getSize(size);

        //Initializing game view object
        //this time we are also passing the screen size to the GameView constructor
        gameView = new ForestGameView(this, size.x, size.y);

        //adding it to contentview
        setContentView(gameView);

        Intent i = getIntent();
        user =  i.getStringExtra("user");
        pass = i.getStringExtra("pass");

        Bundle extras = i.getExtras();
        if (extras != null) {
            if (extras.containsKey("rivergame")) rivergame = i.getExtras().getBoolean("rivergame");
            if (extras.containsKey("urbangame")) urbangame = i.getExtras().getBoolean("urbangame");
            if (extras.containsKey("forestgame")) forestgame = i.getExtras().getBoolean("forestgame");
        }

        if (gameView.isGameOver) {
            this.onStop();
            Intent myIntent = new Intent(this, Game.class);
            myIntent.putExtra("user", user);
            myIntent.putExtra("pass", pass);
            myIntent.putExtra("rivergame", rivergame);
            myIntent.putExtra("urbangame", urbangame);
            myIntent.putExtra("forestgame", forestgame);
            startActivity(myIntent);
        }

        if (gameView.score > 1000) {
            this.onStop();
            Intent myIntent = new Intent(this, Game.class);
            myIntent.putExtra("user", user);
            myIntent.putExtra("pass", pass);
            myIntent.putExtra("rivergame", rivergame);
            myIntent.putExtra("urbangame", urbangame);
            myIntent.putExtra("forestgame", true);
            startActivity(myIntent);
        }

    }

    //pausing the game when activity is paused
    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    //running the game when activity is resumed
    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        ForestGameView.stopMusic();
                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(startMain);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}