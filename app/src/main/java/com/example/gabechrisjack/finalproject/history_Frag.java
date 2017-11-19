package com.example.gabechrisjack.finalproject;

/**
 * Created by ucla201 on 10/9/17.
 */

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class history_Frag extends Fragment {

    String user;
    String pass;
    protected View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.history_frag, container, false);

        user = ((MainActivity)getActivity()).user1;
        pass = ((MainActivity)getActivity()).pass1;

        boolean rivergame = ((MainActivity)getActivity()).rivergame;
        boolean urbangame = ((MainActivity)getActivity()).urbangame;
        boolean forestgame = ((MainActivity)getActivity()).forestgame;

        successSet(v, rivergame, R.id.rivergame);
        successSet(v, urbangame, R.id.urbangame);
        successSet(v, forestgame, R.id.forestgame);

        return v;
    }

    public void successSet(View v, boolean b, int id) {
        ImageView img = v.findViewById(id);
        if (b) {
            img.setImageResource(R.drawable.checkmark);
        }
        else {
            img.setImageResource(R.drawable.redx);
        }
    }

}