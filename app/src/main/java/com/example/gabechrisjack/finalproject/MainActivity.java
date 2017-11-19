package com.example.gabechrisjack.finalproject;

/*
Created by Chris Moorad
 */

/**
 *
 *See Game class for lab summary
 *
 */


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    public String user1;
    public String pass1;
    public boolean rivergame = false;
    public boolean urbangame = false;
    public boolean forestgame = false;

    private static final int MY_PERMISSIONS_REQUEST = 301;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions();

        Intent i = getIntent();
        user1 =  i.getStringExtra("user");
        pass1 = i.getStringExtra("pass");

        //pulls success extras
        Bundle extras = i.getExtras();
        if (extras != null) {
            if (extras.containsKey("rivergame")) rivergame = i.getExtras().getBoolean("rivergame");
            if (extras.containsKey("urbangame")) urbangame = i.getExtras().getBoolean("urbangame");
            if (extras.containsKey("forestgame")) forestgame = i.getExtras().getBoolean("forestgame");
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //add all the tabs to the tablayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Tab 1"));
        tabLayout.addTab(tabLayout.newTab().setText("Tab 2"));
        tabLayout.addTab(tabLayout.newTab().setText("Tab 3"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //add tabs to adapter to concatenate view
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        PlayFrag tab1 = new PlayFrag();
                        return tab1;
                    case 1:
                        history_Frag tab2 = new history_Frag();
                        return tab2;
                    case 2:
                        preferences_Frag tab3 = new preferences_Frag();
                        return tab3;

                    default:
                        return null;
                }
            }


            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "Play";
                    case 1:
                        return "History";
                    case 2:
                        return "Settings";
                    default:
                        return "Settings";
                }
            }
        };


        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    //PERMISSIONS REQUEST
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


}