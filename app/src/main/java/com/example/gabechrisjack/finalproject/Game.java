package com.example.gabechrisjack.finalproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.location.LocationListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.LongToIntFunction;

/**
 * Created by Chris Moorad on 11/11/17.
 */


public class Game extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    //Variable declarations
    public String user1;
    public String pass1;
    public View view;


    //new game
    public boolean riversuccess = false;
    public boolean forestsuccess = false;
    public boolean urbansuccess = false;
    public List<LatLng> riverpoly;
    public List<LatLng> urbanpoly;
    public List<LatLng> forestpoly;

    private GoogleMap mMap;
    private boolean permCheck = false;
    private LocationManager mgr;
    private LatLng loc;
    private SupportMapFragment mapFragment;
    private Marker current;

    public Game() {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_fragment);

        view = (LinearLayout) findViewById(R.id.gameview);

        //grabs username and password from intent
        Intent i = getIntent();
        user1 = i.getStringExtra("user");
        pass1 = i.getStringExtra("pass");

        Bundle extras = i.getExtras();
        if (extras != null) {
            if (extras.containsKey("gametype")) {
                String type = i.getStringExtra("gametype");
                boolean success = i.getExtras().getBoolean("success");
                if (type == "river") {
                    riversuccess = success;
                } else if (type == "forest") {
                    forestsuccess = success;
                } else if (type == "urban") {
                    urbansuccess = success;
                }
            }
        }

        checkPermissions();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        permCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        //notify user if GPS permission doesn't go through
        if (!permCheck) {
            Toast.makeText(this, "GPS permission FAILED", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "GPS permission OK", Toast.LENGTH_LONG).show();

            mgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 5f, this);
        }

        //Check to see if complete game success
        if (riversuccess && forestsuccess && urbansuccess) {
            gameSuccess();
        }

    }

    //Updates current location
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onLocationChanged(Location location) {

        Log.d("LOCATION", "CHANGED: " + location.getLatitude() + " " + location.getLongitude());

        loc = new LatLng(location.getLatitude(), location.getLongitude());

        current.remove();

        current = mMap.addMarker(new MarkerOptions().position(loc).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));


    }

    //SETS UP THE MAP INITIALLY, WITH APPROPRIATE ZOOM AND POLYGONS
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Set default zoom marker
        Double x = Double.parseDouble(getString(R.string.theGreen_x));
        Double y = Double.parseDouble(getString(R.string.theGreen_y));
        LatLng hanover = new LatLng(x, y);

        Location l = null; // remains null if Location is disabled in the phone
        try {
            //if the permissions are there, continue
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            l = mgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (l != null) {
                mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(l.getLatitude(), l.getLongitude()) , 16.0f) );
                Log.d("HERES THE LOCATION", l.toString());
            }
        }
        catch(SecurityException e){
            Log.d("PERM", "Security Exception getting last known location. Using Hanover.");
        }

        //if phone location is enabled, set current location to the phone location
        if (l != null)  loc = new LatLng(l.getLatitude(), l.getLongitude());
        else loc = hanover;
        Log.d("Coords", loc.latitude + " " + loc.longitude);

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        current = mMap.addMarker(new MarkerOptions().position(loc).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16f)); // buildings-level

        //handles welcome message
        String comp = getNumberGamesCompleted();
        TextView openingmsg = view.findViewById(R.id.userscore);
        openingmsg.setText(user1 + ", you have completed" + comp + " out of 3 minigames!");



        //THE WAY THESE ARE CREATED SHOULD BE ACCESSIBLE LATER
        riverpoly = new ArrayList<LatLng>();
        riverpoly.add(new LatLng(43.710714, -72.290854));
        riverpoly.add(new LatLng(43.703548, -72.298322));
        riverpoly.add(new LatLng(43.704448, -72.301755));
        riverpoly.add(new LatLng(43.712544, -72.292786));

        //DRAW RIVER POLYGON
        Polygon polygon = mMap.addPolygon( new PolygonOptions()
            .addAll(riverpoly)
            .strokeColor(Color.BLUE)
            .fillColor(getColor(R.color.transblue)));


        urbanpoly = new ArrayList<LatLng>();
        urbanpoly.add(new LatLng(43.705907, -72.288247));
        urbanpoly.add(new LatLng(43.704853, -72.288172));
        urbanpoly.add(new LatLng(43.704853, -72.289438));
        urbanpoly.add(new LatLng(43.706000, -72.289481));


        Polygon polygon1 = mMap.addPolygon( new PolygonOptions()
            .addAll(urbanpoly)
            .strokeColor(Color.BLACK)
            .fillColor(getColor(R.color.transgrey)));

        forestpoly = new ArrayList<LatLng>();
        forestpoly.add(new LatLng(43.707711, -72.283280));
        forestpoly.add(new LatLng(43.704640, -72.283709));
        forestpoly.add(new LatLng(43.704066, -72.284975));
        forestpoly.add(new LatLng(43.704749, -72.286112));
        forestpoly.add(new LatLng(43.707603, -72.284911));

        Polygon polygon2 = mMap.addPolygon( new PolygonOptions()
                .addAll(forestpoly)
                .strokeColor(Color.GREEN)
                .fillColor(getColor(R.color.transgreen)));

    }

    //checks number of games completed so far
    public String getNumberGamesCompleted() {
        int x = 0;
        if (riversuccess) x+=1;
        if (forestsuccess) x+=1;
        if (urbansuccess) x+=1;
        return Integer.toString(x);
    }

    //Check to ensure permissions
    private void checkPermissions() {
        if(Build.VERSION.SDK_INT < 23)
            return;

        if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.INTERNET}, 0);
        }
    }

    //post method = for example purposes
    /**
    //support methods for post - error caught or draw the cat markers
    private void postPetResults(final String res){
        dl.post(new Runnable() {
            @Override
            public void run() {
                if(res.contains("ERROR")) {
                    if (res.contains("NO_ID")) {
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ctx);
                        dlgAlert.setTitle("Whoops!");
                        dlgAlert.setPositiveButton("OK", null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dlgAlert.setMessage("Please select a cat");
                        dlgAlert.create().show();
                    }
                    else {
                        Log.d("RESPONSE", res);
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ctx);
                        dlgAlert.setTitle("Uh oh...");
                        dlgAlert.setPositiveButton("OK", null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dlgAlert.setMessage("Cat is too far to be petted!");
                        dlgAlert.create().show();
                    }
                }
                else {
                    Log.d("GOT CATPET RESPONSE", res);

                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ctx);
                    dlgAlert.setTitle("Congrats!");
                    dlgAlert.setPositiveButton("OK", null);
                    dlgAlert.setCancelable(true);
                    dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dlgAlert.setMessage("You pet the cat!");
                    dlgAlert.create().show();

                    checkAllPetted();

                    if (allpetted) {
                        Intent myIntent = new Intent(Game.this, Success.class);
                        myIntent.putExtra("user", user1);
                        myIntent.putExtra("pass", pass1);
                        startActivity(myIntent);
                    }
                }

            }
        });
    }
    */

    //distance support method = for example
    public String CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult;
        double meter = valueResult*1000;

        Log.d("METERS AWAY", String.valueOf(meter));

        if (km <= .1) {
            String meterstring = String.valueOf(meter);
            String actualmeters = meterstring.substring(0,2) + " meters";
            return actualmeters;
        }
        else {
            String kmstring = String.valueOf(km);
            String actualkm = kmstring.substring(0,3) + " kilometeres";
            return actualkm;
        }

    }

    public void playMinigame(View v) {

        Double lat = loc.latitude;
        Double lng = loc.longitude;

        //LNGS ARE NEGATIVE!!!

        boolean inriver = PolyUtil.containsLocation(loc, riverpoly, false);
        boolean inurban = PolyUtil.containsLocation(loc, urbanpoly, false);
        boolean inforest = PolyUtil.containsLocation(loc, forestpoly, false);

        if (inriver) {
            Log.d("AYYYY LET'S GOOO", "we did it");
            Intent myIntent = new Intent(this, GameComplete.class);
            myIntent.putExtra("user", user1);
            myIntent.putExtra("pass", pass1);
            startActivity(myIntent);
        }

        else if (inurban) {
            Log.d("AYYYY LET'S GOOO", "we did it");
            Intent myIntent = new Intent(this, GameComplete.class);
            myIntent.putExtra("user", user1);
            myIntent.putExtra("pass", pass1);
            startActivity(myIntent);
        }

        else if (inforest) {
            Log.d("AYYYY LET'S GOOO", "we did it");
            Intent myIntent = new Intent(this, GameComplete.class);
            myIntent.putExtra("user", user1);
            myIntent.putExtra("pass", pass1);
            startActivity(myIntent);
        }

        else {

            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
            dlgAlert.setTitle("Whoops!");
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.setCancelable(true);
            dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            dlgAlert.setMessage("You need to be in one of the minigame zones before " +
                    "you can play a minigame!");
            dlgAlert.create().show();

        }
    }

    public void gameSuccess() {
        Intent myIntent = new Intent(this, GameComplete.class);
        myIntent.putExtra("user", user1);
        myIntent.putExtra("pass", pass1);
        startActivity(myIntent);
    }

    public void quitClicked(View v) {
        Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.putExtra("user", user1);
        myIntent.putExtra("pass", pass1);
        startActivity(myIntent);
    }

    public void resetGames(View v) {
        riversuccess = false;
        forestsuccess = false;
        urbansuccess = false;
    }

    //following three are required methods for api
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

}
