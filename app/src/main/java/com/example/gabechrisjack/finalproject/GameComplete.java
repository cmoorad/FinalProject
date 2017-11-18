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


        Button quitbutton = view.findViewById(R.id.quit);
        quitbutton.setBackgroundColor(getResources().getColor(R.color.red));

        quitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), MainActivity.class);
                myIntent.putExtra("user", user);
                myIntent.putExtra("pass", pass);
                startActivity(myIntent);
            }
        });


        Button returnbutton = view.findViewById(R.id.returntogame);
        returnbutton.setBackgroundColor(getResources().getColor(R.color.green));

        returnbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), Game.class);
                myIntent.putExtra("user", user);
                myIntent.putExtra("pass", pass);
                startActivity(myIntent);
            }
        });



    }


    public Context getActivity() {
        return this;
    }
}
