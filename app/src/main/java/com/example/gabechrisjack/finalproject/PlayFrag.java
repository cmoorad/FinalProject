package com.example.gabechrisjack.finalproject;

/**
 * Created by Chris Moorad on 10/9/17.
 */



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class PlayFrag extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.play_frag, container, false);

        TextView usernametext = v.findViewById(R.id.hiuser);
        final String userholder = ((MainActivity)getActivity()).user1;
        final String passholder = ((MainActivity)getActivity()).pass1;
        final boolean rivergame = ((MainActivity)getActivity()).rivergame;
        final boolean urbangame = ((MainActivity)getActivity()).urbangame;
        final boolean forestgame = ((MainActivity)getActivity()).forestgame;

        //handles play button activity pointer
        Button playbutton = v.findViewById(R.id.playButton);
        playbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), Game.class);
                myIntent.putExtra("user", userholder);
                myIntent.putExtra("pass", passholder);
                myIntent.putExtra("rivergame", rivergame);
                myIntent.putExtra("urbangame", urbangame);
                myIntent.putExtra("forestgame", forestgame);
                startActivity(myIntent);
            }
        });

        //set username text
        if (userholder != null) {
            usernametext.setText("Hi, " + userholder + "!");
        }


        return v;

    }



}