package com.example.bartek.miejsce;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Bartosz Ziemski on 03/04/2017.
 */

public class PlaceDetail {
    private String imageUrl;

    public PlaceDetail(String url){
        this.imageUrl = url;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public void setImageUrl(String url){
        this.imageUrl = url;
    }
}
