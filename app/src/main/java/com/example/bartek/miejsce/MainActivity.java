package com.example.bartek.miejsce;

//main activity -> obsluguje przesuwanie glownych ekranow

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

/**
 * Created by Bartek on 18.07.2016.
 */
public class MainActivity extends FragmentActivity {
    /**
     * Ilosc ekranow do przesuwania
     */
    private static final int NUM_PAGES = 3;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */

    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */

    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Instantiate a ViewPager and a PagerAdapter
        mPager=(ViewPager) findViewById(R.id.pager);
        mPagerAdapter=new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        //startujemy od ekranu 1
        mPager.setCurrentItem(1);
    }

    //po przycisnieciu przycisku "powrot"
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

            if(position==0) return new RankingFragment_with_filters();   //ranking po lewej
            else if (position==1)  return new MainFragment();   //MainFragment jako startowy
            else return new RankingFragment();  //do ustawienia
        }

        @Override
        public int getCount(){
            return NUM_PAGES;
        }   //zwraca ilosc ekranow
    }
}
