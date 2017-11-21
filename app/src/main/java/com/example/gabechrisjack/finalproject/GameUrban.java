package com.example.gabechrisjack.finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by BuddhistWalrus on 11/20/17.
 */

public class GameUrban extends AppCompatActivity {


    public View view;
    public String user;
    public String pass;

    public Boolean urbansuccess;
    public Boolean forestsuccess;
    public Boolean riversuccess;
    public Boolean gameFinished;

    public GameUrban() {
    }

    protected void onCreate(Bundle savedInstanceState) {

        EditText letter1 = findViewById(R.id.editL1);
        EditText letter2 = findViewById(R.id.editL2);
        EditText letter3 = findViewById(R.id.editL3);



        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_urban);

        view = (LinearLayout) findViewById(R.id.gameview);

        Intent i = getIntent();
        user = i.getStringExtra("user");
        pass = i.getStringExtra("pass");
        Bundle extras = i.getExtras();
        if (extras != null) {
            if (extras.containsKey("rivergame")) riversuccess = i.getExtras().getBoolean("rivergame");
            if (extras.containsKey("urbangame")) urbansuccess = i.getExtras().getBoolean("urbangame");
            if (extras.containsKey("forestgame")) forestsuccess = i.getExtras().getBoolean("urbangame");
            if (extras.containsKey("gameFinished")) gameFinished = true;
        }

    }
    //quits the game, passing out all game info to main activity
    public void quitGame(View v) {
        Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.putExtra("user", user);
        myIntent.putExtra("pass", pass);
        myIntent.putExtra("rivergame", riversuccess);
        myIntent.putExtra("urbangame", urbansuccess);
        myIntent.putExtra("forestgame", forestsuccess);
        startActivity(myIntent);
    }

    //returns to the game, passing game info back to the game
    //NOTE: this time we do pass the gameFinished boolean so that we don't come back to the
    //game complete activity every time we start the game
    public void returnToGame(View v) {
        Intent myIntent = new Intent(this, Game.class);
        myIntent.putExtra("user", user);
        myIntent.putExtra("pass", pass);
        myIntent.putExtra("rivergame", riversuccess);
        myIntent.putExtra("urbangame", urbansuccess);
        myIntent.putExtra("forestgame", forestsuccess);
        myIntent.putExtra("gameFinished", true);
        startActivity(myIntent);
    }

    public void urbanClearButtonClicked (View v) {
        EditText letter1 = findViewById(R.id.editL1);
        EditText letter2 = findViewById(R.id.editL2);
        EditText letter3 = findViewById(R.id.editL3);
        EditText letter4 = findViewById(R.id.editL4);
        EditText letter5 = findViewById(R.id.editL5);
        EditText letter6 = findViewById(R.id.editL6);
        EditText letter7 = findViewById(R.id.editL7);
        EditText letter8 = findViewById(R.id.editL8);
        EditText letter9 = findViewById(R.id.editL9);

        letter1.setText("");
        letter2.setText("");
        letter3.setText("");
        letter4.setText("");
        letter5.setText("");
        letter6.setText("");
        letter7.setText("");
        letter8.setText("");
        letter9.setText("");

    }

    public void urbanSubmitButtonClicked (View v) {

        // grab all current inputs
        EditText letter1 = findViewById(R.id.editL1);
        EditText letter2 = findViewById(R.id.editL2);
        EditText letter3 = findViewById(R.id.editL3);
        EditText letter4 = findViewById(R.id.editL4);
        EditText letter5 = findViewById(R.id.editL5);
        EditText letter6 = findViewById(R.id.editL6);
        EditText letter7 = findViewById(R.id.editL7);
        EditText letter8 = findViewById(R.id.editL8);
        EditText letter9 = findViewById(R.id.editL9);

        //concatenate words from input boxes
        String wr1 = letter1.getText().toString()+letter2.getText().toString()+letter3.getText().toString();
        String wr2 = letter4.getText().toString()+letter5.getText().toString()+letter6.getText().toString();
        String wr3 = letter7.getText().toString()+letter8.getText().toString()+letter9.getText().toString();
        String wc1 = letter1.getText().toString()+letter4.getText().toString()+letter7.getText().toString();
        String wc2 = letter2.getText().toString()+letter5.getText().toString()+letter8.getText().toString();
        String wc3 = letter3.getText().toString()+letter6.getText().toString()+letter9.getText().toString();
        String wd1 = letter1.getText().toString()+letter5.getText().toString()+letter9.getText().toString();
        String wd2 = letter7.getText().toString()+letter5.getText().toString()+letter3.getText().toString();

        // check for all inputs
        if(!wr1.equals("")&&!wr2.equals("")&&!wr3.equals("")){

            //verify all rows
        if(wr1.equals(new StringBuilder(wr1).reverse().toString())) {
            if(wr2.equals(new StringBuilder(wr2).reverse().toString())) {
                if(wr3.equals(new StringBuilder(wr3).reverse().toString())) {
                    // verify all columns
                    if(wc1.equals(new StringBuilder(wc1).reverse().toString())) {
                        if(wc2.equals(new StringBuilder(wc2).reverse().toString())) {
                            if(wc3.equals(new StringBuilder(wc3).reverse().toString())) {
                                // verify both diags
                                if(wd1.equals(new StringBuilder(wd1).reverse().toString())) {
                                    if(wd2.equals(new StringBuilder(wd2).reverse().toString())) {

                                        Intent myIntent = new Intent(this, GameComplete.class);
                                        myIntent.putExtra("user", user);
                                        myIntent.putExtra("pass", pass);

                                        myIntent.putExtra("rivergame", riversuccess);
                                        myIntent.putExtra("urbangame", true);
                                        myIntent.putExtra("forestgame", forestsuccess);

                                        startActivity(myIntent);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }}

    //getter
    public Context getActivity() {
        return this;
    }
}

