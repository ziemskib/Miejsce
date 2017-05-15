package com.example.bartek.miejsce.model;

/**
 * Created by Bartek on 29.07.2016.
 */

//pojedynczy element listy rankingowej
public class ListItem {

    public int id;
    public double double_distance;
    public int icon;
    public String title;
    public String distance;
    public String backgroungImageURL;

    //int imageResourceId;
    public int isfav;
    public int isturned;

    public ListItem(){

    }

    public ListItem(int id, double double_distance, int icon, String title, String distance, String backgroungImageURL, int isfav, int isturned) {
        this.id = id;
        this.double_distance = double_distance;
        this.icon = icon;
        this.title = title;
        this.distance = distance;
        this.backgroungImageURL = backgroungImageURL;
        this.isfav = isfav;
        this.isturned = isturned;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setIsturned(int isturned) {
        this.isturned = isturned;
    }

    public void setIsfav(int isfav) {
        this.isfav = isfav;
    }

    //public int getImageResourceId() {return imageResourceId;}

    //public void setImageResourceId(int imageResourceId) {this.imageResourceId = imageResourceId;}

}