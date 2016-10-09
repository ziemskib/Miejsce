package com.example.bartek.miejsce;

/**
 * Created by Bartek on 29.07.2016.
 */
public class ListItem {

    public int icon;
    public String title;
    public String distance;
    public int background_image;
    public ListItem(){

    }

    public ListItem(int icon, int background_image, String title, String distance) {

        this.icon = icon;
        this.title = title;
        this.distance = distance;
        this.background_image = background_image;

    }
}