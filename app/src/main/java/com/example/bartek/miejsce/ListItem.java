package com.example.bartek.miejsce;

/**
 * Created by Bartek on 29.07.2016.
 */

//pojedynczy element listy rankingowej
public class ListItem {

    public int icon;
    public String title;
    public String distance;
    public String backgroungImageURL;
    public ListItem(){

    }

    public ListItem(int icon, String title, String distance, String backgroungImageURL) {
        this.icon = icon;
        this.title = title;
        this.distance = distance;
        this.backgroungImageURL = backgroungImageURL;
    }


}