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
import java.util.Objects;
import java.util.function.LongToIntFunction;

/**
 * Created by team JCOG (Chris Moorad and Jack Taylor) on 11/6/17.
 */


//Lab 4 Summary - Team JCOG
/**

History Fragment: Our history fragment is fully functional, and updates appropriately.

Notification + Tracking : We went with an updating silent notification for when you are far away
from the cat, and a sounds notification when you are close to the cat (within 100 meters) rather
than a notification bar. Excluding this slight change, the tracking component is fully functional :)

Map Fragment: We successfully added the two buttons, distinguishing between petting and tracking

Camera Function:

Tab Change: We removed the ranking fragment from the tab layout

*/

public class Game extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    //Variable declarations
    public String user1;
    public String pass1;
    public Bitmap image;
    public JSONArray pubcat;
    public View view;
    public String caturl;
    public String currid;
    public boolean allpetted = false;
    public boolean tracking = false;
    public double trackingDistance;

    private GoogleMap mMap;
    private boolean permCheck = false;
    private LocationManager mgr;
    private LatLng loc;
    private SupportMapFragment mapFragment;
    private static final int MY_PERMISSIONS_REQUEST = 301;
    private Marker current;

    private String req;
    private Handler dl;
    private Activity ctx;
    RequestQueue queue;

    public Game() {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_fragment);

        view = (LinearLayout) findViewById(R.id.gameview);

        requestPermissions();

        //grabs username and password from intent
        Intent i = getIntent();
        user1 = i.getStringExtra("user");
        pass1 = i.getStringExtra("pass");

        dl = new Handler();
        ctx = this;
        queue = Volley.newRequestQueue(this);

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

        //get all the cats for the map
        getCats(mapFragment.getView());

    }

    //Updates current location
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onLocationChanged(Location location) {

        if (tracking) {
            getTracking();
        }

        Log.d("LOCATION", "CHANGED: " + location.getLatitude() + " " + location.getLongitude());

        loc = new LatLng(location.getLatitude(), location.getLongitude());

        current.remove();

        current = mMap.addMarker(new MarkerOptions().position(loc).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

    }

    //SETS UP THE MAP INITIALLY, WITH APPROPRIATE ZOOM AND CATS
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

        TextView name = view.findViewById(R.id.catname);
        name.setText("Try to click the pointers!");

        TextView dist = view.findViewById(R.id.catdist);
        dist.setText("");

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                turnTrackingOff();
                currid = null;
            }
        });


        //DRAW RIVER POLYGON
        Polygon polygon = mMap.addPolygon( new PolygonOptions()
            .add(new LatLng(43.710714, -72.290854), new LatLng(43.703548, -72.298322),
                    new LatLng(43.704448, -72.301755), new LatLng(43.712544, -72.292786))
            .strokeColor(Color.BLUE)
            .fillColor(Color.parseColor("#0044cc")));

    }

    //support method for above
    public static LatLng fromLocationToLatLng(Location location){
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    //Check to ensure permissions
    private void checkPermissions() {
        if(Build.VERSION.SDK_INT < 23)
            return;

        if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.INTERNET}, 0);
        }
    }

    //Get catlist - request
    public void getCats(View v){

        req = buildHTML();

        new Thread(new Runnable() {

            @Override
            public void run() {
                String url =  "http://cs65.cs.dartmouth.edu/catlist.pl?" + req;

                Log.d("URL", url);
                StringRequest req = new StringRequest (Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String res) {
                                postResultsToUI(res);
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("THE ERROR", error.toString());
                        postResultsToUI("error" + error.toString());
                    }
                });


                // Add the request to the RequestQueue.
                queue.add(req);
            }
        }).start();
    }

    // builds string for retrieving cat list
    private String buildHTML(){

        String s = "name=" + user1 + "&password=" + pass1 + "&mode=easy";

        return s;
    }

    //support methods for post - error caught or draw the cat markers
    private void postResultsToUI(final String res){
        dl.post(new Runnable() {
            @Override
            public void run() {
                if(res.contains("error")) {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ctx);
                    dlgAlert.setTitle("Error Message...");
                    dlgAlert.setPositiveButton("OK", null);
                    dlgAlert.setCancelable(true);
                    dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dlgAlert.setMessage("Could not connect to server");
                    dlgAlert.create().show();
                }
                else {
                    Log.d("GOT CATLIST", res);
                    drawCats(res);
                }

            }
        });
    }

    //success support method
    private void checkAllPetted() {

        Boolean all = true;

        try {

            for (int i = 0; i < pubcat.length(); i++) {

                JSONObject cat = pubcat.getJSONObject(i);
                String pettedtf = cat.get("petted").toString();
                Boolean petted = Boolean.valueOf(pettedtf);

                if (!petted) {
                    all = false;
                }
            }

            if (all) {
                allpetted = true;
            }

        }
        catch (JSONException je) {
                Log.d("Error", "JSON CREATION ERROR");
            }


    }

    //Pet cat - request
    public void petCat(View v){

        req = buildPetHTML();

        new Thread(new Runnable() {

            @Override
            public void run() {
                String url = "http://cs65.cs.dartmouth.edu/pat.pl?" + req;

                Log.d("URL", url);
                StringRequest req = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        postPetResults(res);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("THE ERROR", error.toString());
                        postPetResults("ERROR" + error.toString());
                    }
                });


                // Add the request to the RequestQueue.
                queue.add(req);
            }
        }).start();

    }

    // builds string for retrieving cat list
    private String buildPetHTML(){
        String s = "name=" + user1 + "&password=" + pass1 + "&catid=" + currid + "&lat=" + loc.latitude + "&lng=" + loc.longitude;
        return s;
    }

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



    //NEW TRACKING METHODS

    //The support methods are also called by user actions that trigger tracking on/off
    //tracking off support method
    public void turnTrackingOff() {
        Button trackButton = view.findViewById(R.id.trackButton);
        trackButton.setBackgroundColor(getResources().getColor(R.color.green));
        trackButton.setText("Track");
        if(tracking) {
            tracking = false;
        }
        RemoveAllNotifications();
        Log.d("TRACKING TURNED OFF", String.valueOf(tracking));
    }

    //tracking on support method
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void turnTrackingOn() {
        Button trackButton = view.findViewById(R.id.trackButton);
        trackButton.setBackgroundColor(getResources().getColor(R.color.red));
        trackButton.setText("Stop");

        getTracking();

        if (!tracking) {
            tracking = true;
        }

    }

    //Tracking button - handles the click and calls appropriate support method
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void trackClicked(View v) {

        //if tracking is off, turn it on
        if (!tracking) {
            turnTrackingOn();
        }
        //otherwise, turn it off
        else {
            turnTrackingOff();
        }
        Log.d("IS TRACKING ON", String.valueOf(tracking));

    }

    //tracking server request - called when tracking turned on or location changed
    public void getTracking() {

        req = buildPetHTML();

        new Thread(new Runnable() {

            @Override
            public void run() {
                String url = "http://cs65.cs.dartmouth.edu/track.pl?" + req;

                Log.d("URL", url);
                StringRequest req = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        postTrackResults(res);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("THE ERROR", error.toString());
                        postTrackResults("ERROR" + error.toString());
                    }
                });


                // Add the request to the RequestQueue.
                queue.add(req);
            }
        }).start();

    }

    //post track results method - calls notification
    public void postTrackResults(final String res) {
        dl.post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                if(res.contains("ERROR")) {
                    if (res.contains("NO_ID") || res.contains("MISSING_ID")) {
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
                    else if (res.contains("MISSING_LAT") || res.contains("MISSING_LNG")) {
                        Log.d("RESPONSE", res);
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ctx);
                        dlgAlert.setTitle("Uh oh...");
                        dlgAlert.setPositiveButton("OK", null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dlgAlert.setMessage("Something went wrong with finding your location, " +
                                "please check your wifi connection and try again");
                        dlgAlert.create().show();
                    }
                    trackClicked(view);
                }
                else {
                    Log.d("Tracking on", res);
                    try {
                        JSONObject tracker = new JSONObject(res);
                        trackingDistance = tracker.getDouble("distance");
                    }
                    catch(JSONException je) {
                        Log.d("Error", "MARKER CLICK JSON ERROR SITUATION");
                    }
                    RemoveAllNotifications();

                    //if the distance is close, use a close notification
                    if (trackingDistance < 100) {
                        String dist = String.valueOf(trackingDistance);
                        closeNotification(dist.substring(0,2));
                    }
                    //otherwise, use a silent notification
                    else {
                        String dist = String.valueOf(trackingDistance);
                        createAndGenerateNotification(dist.substring(0,3));
                    }
                }

            }
        });
    }




    //draws the cat markers
    private void drawCats(String jsonString) {

        try {
            JSONArray catlist = new JSONArray(jsonString);
            pubcat = catlist;

            Log.d("CATLISTARRAY", catlist.toString());

            for (int i = 0; i < catlist.length(); i++) {

                JSONObject cat = catlist.getJSONObject(i);

                String name = cat.get("name").toString();

                Double catLat = Double.parseDouble(cat.get("lat").toString());
                Double catLng = Double.parseDouble(cat.get("lng").toString());
                LatLng pos = new LatLng(catLat, catLng);

                Marker newmarker = mMap.addMarker(new MarkerOptions().position(pos).title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                          @Override
                          public boolean onMarkerClick(Marker marker) {
                              String nameclick = marker.getTitle();
                              Log.d("MARKER WAS CLICKED", nameclick);

                              //set name in panel
                              TextView name = view.findViewById(R.id.catname);
                              name.setText(nameclick);

                              //to calculate dist
                              LatLng catloc = marker.getPosition();
                              LatLng curr = loc;
                              String distance = CalculationByDistance(catloc, curr);
                              TextView dist = view.findViewById(R.id.catdist);
                              dist.setText(distance);

                              //iterate through json array to find correct json object
                              for (int i = 0; i < pubcat.length(); i++) {
                                  try {
                                      JSONObject cat = pubcat.getJSONObject(i);

                                      if (cat.get("name").toString().equals(marker.getTitle())) {

                                          Log.d("CAT NAME", nameclick);
                                          Log.d("CAT URL", cat.get("picUrl").toString());

                                          if (currid ==null ) {
                                              currid = cat.get("catId").toString();
                                          }
                                          else if (!currid.equals(cat.get("catId").toString())) {
                                              //set current id
                                              currid = cat.get("catId").toString();
                                              turnTrackingOff();
                                          }

                                          //get catpic url
                                          caturl = cat.get("picUrl").toString();

                                          image = changeBitMap(caturl);

                                          //delay before changing cat image for async task to go through
                                          final Handler delay = new Handler();
                                          delay.postDelayed(new Runnable() {
                                              @Override
                                              public void run() {
                                                  ImageView icon = view.findViewById(R.id.catIcon);
                                                  icon.setImageBitmap(image);
                                              }
                                          }, 300);



                                      }
                                  }
                                  catch(JSONException je) {
                                      Log.d("Error", "MARKER CLICK JSON ERROR SITUATION");
                                  }
                              }
                              return false;
                          }
                      });
            }

        } catch(JSONException je) {
            Log.d("Error", "JSON CREATION ERROR");
        }

    }

    //support method to get cat image from url
    protected Bitmap changeBitMap(String urlstring) {
        //Bitmap Icon = null;

        caturl = urlstring;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(caturl);
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch(MalformedURLException e) {
                    Log.d("MALFORMED URL", "DAMNIT");

                } catch(IOException e) {
                    Log.d("URL IO Exception", "YEP");
                }
            }
        }).start();
        return image;
    }

    //distance support method
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

    //double check permissions even though checked in main activity (just for troubleshoot purposes)
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestPermissions(){
        // Here, thisActivity is the current activity
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.INTERNET)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET},
                    MY_PERMISSIONS_REQUEST);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
                    // permissions not obtained
                    Toast.makeText(this,"failed request permission!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    //NEW NOTIFICATION METHODS
    //Creates a silent notification if the cat is far away
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void createAndGenerateNotification(String trackingdist) {

        Intent intent = new Intent();
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx);

        b.setAutoCancel(true)
                //.setDefaults(Notification.DEFAULT_ALL)
                //.setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.red_marker)
                .setContentTitle("Tracking")
                .setContentText("Cat is " + trackingdist + " meters away")
                .setContentIntent(contentIntent);


        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, b.build());

    }

    //Creates a sound notification if the cat is nearby
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void closeNotification(String trackingdist) {

        Intent intent = new Intent();
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx);

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.red_marker)
                .setContentTitle("Tracking")
                .setContentText("Cat is close! Only " + trackingdist + " meters away!")
                .setContentIntent(contentIntent);


        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, b.build());

    }

    //Removes all notifications
    public void RemoveAllNotifications() {

        NotificationManager mNotificationManager
                = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        try {
            mNotificationManager.cancelAll();
        }
        catch (Exception e) {
            Log.d("POINTER EXCEPTION", e.toString());
        }
    }


/*
    public void inGameZone(LatLng currentlocation) {

        Double lat = currentlocation.latitude;
        Double lng = currentlocation.longitude;

        //LNGS ARE NEGATIVE!!!

        //RIVER GAME
        if ((bottom < lat && lat < top) && (left < lng && lng < right)) {

        }

        //FORREST GAME
        else if () {

        }


        //URBAN GAME
        else if () {

        }

        else {

        }


    }
    */



}
