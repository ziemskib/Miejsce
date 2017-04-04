package com.example.bartek.miejsce;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by Bartosz Ziemski on 01/04/2017.
 */

public class InternetSettings extends Activity {
    boolean settings_opened = false;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.internet_settings);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*.8), (int) (height*.4));
        ImageButton internetSettingsButton = (ImageButton) findViewById(R.id.internetSettingsButton);
        internetSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //On filtr button click
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If we've received a touch notification that the user has touched
        // outside the app, finish the activity.
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            finish();
            return true;
        }
        // Delegate everything else to Activity.
        return super.onTouchEvent(event);
    }
    //Close app on back button pressed
    @Override
    public void onBackPressed()
    {
        ActivityCompat.finishAffinity(this);
        settings_opened = true;
    }
    @Override
    public void onResume(){
        super.onResume();
        if(settings_opened)
            finish();
        //System.exit(0);
    }
}
