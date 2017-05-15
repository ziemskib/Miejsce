package com.example.bartek.miejsce.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.example.bartek.miejsce.R;
import com.example.bartek.miejsce.model.Place;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Bartosz Ziemski on 14/05/2017.
 */

public class PlaceActivity extends Activity {

    private int cityId = 1;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    private Place place;
    private String distString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        place = new Place();
        place.setId(1);


        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if(b.containsKey("placeId")){
            place.setId(b.getInt("placeId"));

        }
        if(b.containsKey("cityId")){
            cityId = b.getInt("cityId");
        }
        if (b.containsKey("distance")) {
            place.setDistance(b.getDouble("distance"));
        }
        if(b.containsKey("placeName")){
            place.setName(b.getString("placeName"));
            TextView textView = (TextView) findViewById(R.id.place_name);
            textView.setText(place.getName());
        }
        downloadData();
    }


    void downloadData(){
        DatabaseReference refDetails = database.getReference("city_details/"+Integer.toString(cityId)+"/place_details");
        refDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Download rest data for next x places
                place.setId(place.getId());
                //String newName = dataSnapshot.child(Integer.toString(place.getId())).child("name").getValue(String.class);
                //place.setName(newName);
                place.setBackgroundImage(dataSnapshot.child(Integer.toString(place.getId())).child("backgroundImage").getValue(String.class));
                place.setDescription(dataSnapshot.child(Integer.toString(place.getId())).child("desctiption").getValue(String.class));
                distString = "";
                Double dist = place.getDistance();
                  if(dist!=-1) {
                      if (dist >= 1) {
                          distString = Integer.toString(dist.intValue()) + " km";
                      } else if (dist < 1) {
                          dist = dist * 1000;
                          distString = Integer.toString(dist.intValue()) + " m";
                      }
                  }
                setUpScreen();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void setUpScreen(){
        TextView textView = (TextView) findViewById(R.id.place_name);
        textView.setText(place.getDescription());
    }
}
