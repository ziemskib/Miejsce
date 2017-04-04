package com.example.bartek.miejsce;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bartosz Ziemski on 03/04/2017.
 */

public class ImageItemAdapter extends ArrayAdapter<PlaceDetail> {
    Context context;
    int layoutResourceId;
    private List<PlaceDetail> data;
    public ImageItemAdapter(Context context, int layoutResourceId, List<PlaceDetail> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    public void addListItemToAdapter(List<PlaceDetail> list){
        data.addAll(list);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View row = convertView;
        ListItemHolder holder = null;

        if(row==null){
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ListItemHolder();
            holder.image = (ImageView) row.findViewById(R.id.imageViewAVF);
            holder.linearLayout = (LinearLayout) row.findViewById(R.id.list_image_item);
            row.setTag(holder);
        }
        else{
            holder = (ListItemHolder) row.getTag();
        }
        PlaceDetail object = data.get(position);
        //Picasso.with(context).load(object.getImageUrl()).into(holder.image);
        holder.image.setImageResource(R.drawable.restauracja);
        return row;
    }
    static class ListItemHolder
    {
        ImageView image;
        LinearLayout linearLayout;
    }
}
