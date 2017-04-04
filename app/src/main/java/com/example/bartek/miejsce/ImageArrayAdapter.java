package com.example.bartek.miejsce;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Bartosz Ziemski on 03/04/2017.
 */

public class ImageArrayAdapter extends ArrayAdapter<PlaceDetail> {
    ArrayList<PlaceDetail> imageList;
    private Context context;
    public ImageArrayAdapter(@NonNull Context context, ArrayList<PlaceDetail> objects) {
        super(context, R.layout.avf_screen, objects);
        this.imageList = objects;
        this.context = context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        if(convertView == null){
            if(parent == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.avf_screen, null);
            }
            else
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.avf_screen, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageViewAVF);
        if(imageView!=null){
            Picasso.with(getContext()).load("http://i.imgur.com/HN7HaEr.jpg?1").into(imageView);

        }
        return convertView;
    }

}
