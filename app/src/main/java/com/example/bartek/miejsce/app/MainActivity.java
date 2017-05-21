package com.example.bartek.miejsce.app;

//main activity -> obsluguje przesuwanie glownych ekranow

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bartek.miejsce.*;
import com.example.bartek.miejsce.model.City;
import com.example.bartek.miejsce.model.CustomViewPager;
import com.example.bartek.miejsce.model.ListItem;
import com.example.bartek.miejsce.model.MyLocation;
import com.example.bartek.miejsce.model.Place;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by Bartek on 18.07.2016.
 */
public class MainActivity extends FragmentActivity{

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1000;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1001;
    private static final int NUM_PAGES = 4; //Number of sliding screens
    public String city=" "; //Name of current city
    int city_id = 0;
    public CustomViewPager mPager;  //Viewpager
    private PagerAdapter mPagerAdapter;//The pager adapter, which provides the pages to the view pager widget.

    double userLatitude =-1.0;  //User location
    double userLongitude = -1.0;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final ArrayList<String> filters = new ArrayList<>(); //List of all filters
    final List<City> cities = new ArrayList<>();    //List of all available cities
    final List<Place> places = new ArrayList<>(); //List of all places in current city
    List<Place> main_places = new ArrayList<>();    //Contains places to display on main_page
    boolean firstLoaded = false;    //Defines if first data has been loaded
    int loadingStep = 4;    //Determines how many list items should be loaded each time during scrolling

    private MainFragment m1stFragment; //First Fragment  - Menu (later)
    private MainFragment mainFragment;  //Second Fragment  - Main Screen
    private ListFragment listFragment;  //Third Fragment - Ranking List
    private MapViewFragment mapFragment;   //Fourth Fragment - Map Fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPager = (CustomViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        //Set start screen on aplication start
        mPager.setCurrentItem(1);
        //Switch off paging for loading data
        mPager.setPagingEnabled(false);
        mPager.setOffscreenPageLimit(3);

        setLoadingScreen();
        //Instantiate a ViewPager and a PagerAdapter
        //Getting user's location
        if(isNetworkAvailable()){
            MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
                @Override
                public void gotLocation(Location location){
                    //Got the location
                    userLatitude = location.getLatitude();
                    userLongitude = location.getLongitude();
                }
            };
            MyLocation myLocation = new MyLocation(this, this);
            myLocation.getLocation(this, locationResult);


            //Mozliwe ze trzeba przeniesc wewnatrz pobierania cities
            //Getting list of filters
            DatabaseReference refFilters = database.getReference("filters");
            refFilters.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int count = (int) dataSnapshot.getChildrenCount();
                    Log.d("Filtry", Integer.toString(count));
                    String temp;
                    for (int i = 1; i <= count; i++) {
                        temp = dataSnapshot.child(Integer.toString(i)).getValue(String.class);
                        filters.add(temp);
                        Log.d("Filtry", filters.get(i-1));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            //Finding nearest city
            DatabaseReference refCities = database.getReference("cities");
            refCities.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int count = (int) dataSnapshot.getChildrenCount();
                    City temp;
                    for (int i = 1; i <= count; i++) {
                        temp = dataSnapshot.child(Integer.toString(i)).getValue(City.class);
                        temp.countDistance(userLatitude, userLongitude);
                        cities.add(temp);
                    }
                    //Sort cities by distance
                    Collections.sort(cities);
                    city = cities.get(1).getName();
                    city_id = cities.get(1).getId();
                    TextView city_textView = (TextView) findViewById(R.id.city);
                    city_textView.setText(city);
                    DatabaseReference refPlaces = database.getReference("city_details/"+Integer.toString(city_id)+"/places");
                    refPlaces.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Search more data
                            int count = (int) dataSnapshot.getChildrenCount();
                            Place temp;
                            for (int i = 1; i <= count; i++) {
                                temp = dataSnapshot.child(Integer.toString(i)).getValue(Place.class);
                                temp.countDistance(userLatitude, userLongitude);
                                Boolean filterTemp;
                                //Pobrac filtry do mapy w miejscu
                                for(int j = 0; j < filters.size(); j++){
                                    filterTemp=dataSnapshot.child(Integer.toString(i)).child(filters.get(j)).getValue(Boolean.class);
                                    Log.d("Filter", Boolean.toString(filterTemp));
                                    Log.d("Filter", filters.get(j));
                                    temp.addFilter(filters.get(j), filterTemp);
                                }
                                places.add(temp);
                                if(temp.getMain()){
                                    main_places.add(temp);
                                }
                            }
                            //Sort places by distance
                            Collections.sort(places);
                            final ArrayList<ListItem> lstResult = new ArrayList<ListItem>();
                            DatabaseReference refDetails = database.getReference("city_details/"+Integer.toString(city_id)+"/place_details");
                            refDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //Download rest data for first x places for list of places
                                    if (loadingStep > places.size())
                                        loadingStep = places.size();
                                    for (int i = 0; i < loadingStep; i++) {
                                        String newName = dataSnapshot.child(Integer.toString(places.get(i).getId())).child("name").getValue(String.class);
                                        places.get(i).setName(newName);
                                        places.get(i).setBackgroundImage(dataSnapshot.child(Integer.toString(places.get(i).getId())).child("backgroundImage").getValue(String.class));
                                        String distString;
                                        distString = "";
                                        Double dist = places.get(i).getDistance();
                                        if(dist!=-1){
                                            if(dist >= 1)
                                            {
                                                distString = Integer.toString(dist.intValue()) + " km";
                                            }
                                            else if(dist<1){
                                                dist = dist*1000;
                                                distString = Integer.toString(dist.intValue()) + " m";
                                            }
                                        }
                                        lstResult.add(new ListItem(places.get(i).getId(), places.get(i).getDistance(),R.drawable.kat1_button_normal, places.get(i).getName(), distString, places.get(i).getBackgroundImage(), 1, 0));
                                    }

                                    //Download images to display in main screen
                                    for(int i=0; i<main_places.size(); i++) {
                                        main_places.get(i).setBackgroundImage(dataSnapshot.child(Integer.toString(main_places.get(i).getId())).child("backgroundImage").getValue(String.class));
                                    }
                                    //Setting images in ViewFlipper on Main Page
                                    if (mainFragment != null) {
                                        mainFragment.setImages();
                                    }
                                    //Setting list in listFragment
                                    if (listFragment != null){
                                        listFragment.createList(lstResult);
                                    }
                                    if (mapFragment!=null){
                                        mapFragment.setMarkers();
                                    }
                                    removeLoadingScreen();
                                    firstLoaded = true;
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        }
        else{
            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_LONG).show();
            startActivity(new Intent(this,InternetSettings.class));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(listFragment!=null)
            listFragment.resume();
    }
    //Back button action
    @Override
    public void onBackPressed(){
        if(mPager.getCurrentItem()==1){
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        }
        else if (mPager.getCurrentItem()<1){
            mPager.setCurrentItem(mPager.getCurrentItem()+1);
        }
        else{
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem()-1);
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter{
        public ScreenSlidePagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position){
            switch(position){
                case 0:
                    return new MainFragment(); //menu in the future
                case 1:
                    return new MainFragment(); //main screen
                case 2:
                    return new ListFragment(); //ranking/location list
                case 3:
                    return new MapViewFragment(); //map
                default:
                    return null;
            }
        }
        @Override
        public int getCount(){
            return NUM_PAGES;
        }   //returns number of pages

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            // save the appropriate reference depending on position
            switch (position) {
                case 0:
                    m1stFragment = (MainFragment) createdFragment;
                    break;
                case 1:
                    mainFragment = (MainFragment) createdFragment;
                    break;
                case 2:
                    listFragment = (ListFragment) createdFragment;
                    break;
                case 3:
                    mapFragment = (MapViewFragment) createdFragment;
                    break;
            }
            return createdFragment;
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    //Enables sliding pages and changes foreground to transparent
    public void removeLoadingScreen(){
        ImageView please_wait = (ImageView) findViewById(R.id.please_wait);
        please_wait.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
        //FrameLayout layout= (FrameLayout) findViewById(R.id.pager_layout);
       // ((FrameLayout) layout).setForeground(new ColorDrawable(Color.TRANSPARENT));
        mPager.setPagingEnabled(true);
        return;
    }
    public void setLoadingScreen(){
        ImageView please_wait = (ImageView) findViewById(R.id.please_wait);
        please_wait.bringToFront();
        //FrameLayout layout= (FrameLayout) findViewById(R.id.pager_layout);
        //((FrameLayout) layout).setForeground(new ColorDrawable(Color.YELLOW));
    }
    private boolean isNetworkAvailable() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;
        return connected;
    }
}
