package com.example.bartek.miejsce.model;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Bartek on 16.03.2017.
 */

public class Place implements Comparable<Place>, Serializable{
    private String name;
    private double latitude;
    private double longitude;
    private double distance;
    private int id;
    private String backgroundImage;
    private boolean main;
    private String description;
    private HashMap<String, Boolean> filters;

    public Place(){
        this.filters = new HashMap<>();
    }

    public Place(String name, double latitude, double longitude, int id, String backgroundImage, boolean main, String description){
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
        this.backgroundImage = backgroundImage;
        this.main = main;
        this.description = description;
        this.filters = new HashMap<>();
    }
    public void addFilter(String filterName, Boolean value){
        Log.d("Add filter", filterName+ " " + Boolean.toString(value));
        filters.put(filterName, value);
    }
    public Place(String name, double latitude, double longitude, int id, String backgroundImage, boolean main){
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
        this.backgroundImage = backgroundImage;
        this.main = main;
        this.description = " ";
    }
    public void setName(String newName){
        if(newName.equals(""))
            name="";
        else
            name = newName;
    }
    public String getName(){
        if(name==null){
            return "";
        }
        else
            return name;
    }
    //public void setLatitude(double newLatitude){latitude = newLatitude;}
    public double getLatitude(){return latitude;}
   // public void setLongitude(double newLongitude){longitude = newLongitude;}
    public double getLongitude(){return longitude;}
    public void setId(int newId){id = newId;}
    public int getId(){return id;}
    public void setDistance(double newDistance){distance = newDistance;}
    public double getDistance(){return distance;}
    public void setBackgroundImage(String newBackgroundImage){backgroundImage = newBackgroundImage;}
    public String getBackgroundImage(){return backgroundImage;}
    public String getDescription(){return description;}
    public void setDescription(String newDescription){description = newDescription;}

    public boolean getMain(){return main;}
    /*public void countDistance(double user_latitude, double user_longitude) {
        double a = Math.abs(user_latitude-latitude);
        double b = Math.abs(user_longitude-longitude);
        this.distance = Math.sqrt(a*a+b*b)*111.32;
    }*/

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     * I assume hight equals 0
     */
    public void countDistance(double user_latitude, double user_longitude) {
        if(user_latitude==-1.0 || user_longitude==-1.0){
            distance=-1.0;
            return;
        }
        final int R = 6371; // Radius of the earth
        Double latDistance = Math.toRadians(latitude - user_latitude);
        Double lonDistance = Math.toRadians(longitude - user_longitude);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(user_latitude)) * Math.cos(Math.toRadians(latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // convert to meters
        distance = Math.pow(distance, 2);
        this.distance= Math.sqrt(distance);
    }


    @Override
    public int compareTo(Place another) {
        if(this.distance<another.distance)
            return -1;
        else if(this.distance==another.distance)
            return 0;
        else
            return 1;
    }
}
