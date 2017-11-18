package com.example.gabechrisjack.finalproject;

/**
 * Created by ucla201 on 10/9/17.
 */

import android.app.Activity;
import android.content.DialogInterface;
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
    String catnum;
    ArrayList<Bitmap> imagelist = new ArrayList<Bitmap>();
    private String req;
    RequestQueue queue;
    private Handler dl;
    private Activity ctx;
    public String caturl;
    public String currid;
    public Bitmap image;
    protected View v;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.history_frag, container, false);

        user = ((MainActivity)getActivity()).user1;
        pass = ((MainActivity)getActivity()).pass1;

        dl = new Handler();
        ctx = getActivity();
        queue = Volley.newRequestQueue(getActivity());

        getCats(v);

        return v;
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


                Log.d("HERES DA QUEUE", queue.toString());
                // Add the request to the RequestQueue.
                queue.add(req);
            }
        }).start();
    }

    // builds string for retrieving cat list
    private String buildHTML(){
        String s = "name=" + user + "&password=" + pass + "&mode=easy";
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
                    drawcatHist(res);
                }

            }
        });
    }

    //method to draw cats in history fragment
    public void drawcatHist(String catlist) {

        try {

            //get json array from catlist string
            JSONArray pubcat = new JSONArray(catlist);

            //iterate through json array to find correct json object
            for (int i = 1; i < pubcat.length()+1; i++) {
                try {

                    JSONObject cat = pubcat.getJSONObject(i-1);

                    catnum = Integer.toString(i);
                    String namenum = "cat" + catnum + "name";
                    int resID = getResources().getIdentifier(namenum, "id", getActivity().getPackageName());

                    //Set name
                    String catname = cat.get("name").toString();
                    Log.d("THECATIS", catname);
                    TextView name = v.findViewById(resID);
                    name.setText(catname);

                    //Set lng
                    String catlng = "Lng: " + cat.get("lng").toString();
                    String lngnum = "cat" + catnum + "lng";
                    int resIDlng = getResources().getIdentifier(lngnum, "id", getActivity().getPackageName());
                    TextView lng = v.findViewById(resIDlng);
                    lng.setText(catlng);

                    //Set lat
                    String catlat = "Lat: " + cat.get("lat").toString();
                    String latnum = "cat" + catnum + "lat";
                    int resIDlat = getResources().getIdentifier(latnum, "id", getActivity().getPackageName());
                    TextView lat = v.findViewById(resIDlat);
                    lat.setText(catlat);


                    //set petted icon
                    Boolean isPet;
                    String petted = cat.get("petted").toString();
                    isPet = Boolean.parseBoolean(petted);
                    String petcat = "cat" + catnum + "pet";
                    int resIDpet = getResources().getIdentifier(petcat, "id", getActivity().getPackageName());
                    ImageView icon = v.findViewById(resIDpet);
                    if (isPet) {
                        icon.setImageResource(R.drawable.redheart);
                    }
                    else {
                        icon.setImageResource(R.drawable.greyheart);
                    }

                    //set current id
                    currid = cat.get("catId").toString();

                    //get catpic url
                    caturl = cat.get("picUrl").toString();

                    //Bitmap newimage = changeBitMap(caturl);
                    image = changeBitMap(caturl);

                    //add delay for async task
                    Thread.sleep(300);

                    //add cat pics to list
                    imagelist.add(image);
                    Log.d("IMAGELIST", imagelist.toString());

                    //draw cats from list
                    String picnum = "cat" + catnum;
                    int resIDpic = getResources().getIdentifier(picnum, "id", getActivity().getPackageName());
                    ImageView catpic = v.findViewById(resIDpic);
                    catpic.setImageBitmap(imagelist.get(i-1));

                } catch (JSONException je) {
                    Log.d("Error", "JSON object creation error");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        catch(JSONException je) {
            Log.d("Error", "JSON ARRAY CREATION ERROR");
        }
    }

    //support method to get cat image from url
    protected Bitmap changeBitMap(String urlstring) {

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

}