package com.example.bartek.miejsce.model;

/**
 * Created by Bartek on 16.03.2017.
 */

public class City implements Comparable<City>{
    private String name;
    private double latitude;
    private double longitude;
    private double distance;
    private int id;

    public City(){}

    public City(String name, double latitude, double longitude, int id){
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
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
    public double getLatitude(){return latitude;}
    public double getLongitude(){return longitude;}
    public int getId(){return id;}
    public double getDistance(){return distance;}
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
    public int compareTo(City another) {
        if(this.distance<another.distance)
            return 1;
        else if(this.distance==another.distance)
            return 0;
        else
            return -1;
    }
}
