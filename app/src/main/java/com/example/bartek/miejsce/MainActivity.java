package com.example.bartek.miejsce;

//main activity -> obsluguje przesuwanie glownych ekranow

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.widget.FrameLayout;


/**
 * Created by Bartek on 18.07.2016.
 */
public class MainActivity extends FragmentActivity{

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1000;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1001;
    //Number of sliding screens
    private static final int NUM_PAGES = 3;
    //name of current city
    public String city = "krakow";
    //pager adapter
    private CustomViewPager mPager;
    //User location
    double userLatitude =-1.0;
    double userLongitude = -1.0;
    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Instantiate a ViewPager and a PagerAdapter
        mPager = (CustomViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        //Set start screen on aplication start
        mPager.setCurrentItem(1);
        //Switch off paging for loading data
        mPager.setPagingEnabled(false);
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
            if(position==0) return new RankingFragment_with_filters_old();   //ranking po lewej
            else if (position==1)  return new MainFragment();   //MainFragment jako startowy
            else return new RankingFragment_with_filters();  //do ustawienia
        }
        @Override
        public int getCount(){
            return NUM_PAGES;
        }   //zwraca ilosc ekranow
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
        FrameLayout layout= (FrameLayout) findViewById(R.id.pager_layout);
        ((FrameLayout) layout).setForeground(new ColorDrawable(Color.TRANSPARENT));
        mPager.setPagingEnabled(true);
        return;
    }
}