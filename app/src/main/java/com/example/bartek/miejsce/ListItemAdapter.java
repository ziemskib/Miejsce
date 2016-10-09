package com.example.bartek.miejsce;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Bartek on 29.07.2016.
 */
public class ListItemAdapter extends ArrayAdapter<ListItem>{
    Context context;
    int layoutResourceId;
   // ListItem data[] = null;
    private List<ListItem> data;
/*
    public ListItemAdapter(Context context, int layoutResourceId, ListItem[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }
*/
    public ListItemAdapter(Context context, int layoutResourceId, List<ListItem> data) {
    super(context, layoutResourceId, data);
    this.layoutResourceId = layoutResourceId;
    this.context = context;
    this.data = data;
}

    public void addListItemToAdapter(List<ListItem> list)
    {
        //Add list to current array list of data
        data.addAll(list);
        //Notify UI
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ListItemHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ListItemHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.distance = (TextView)row.findViewById(R.id.distance);
            holder.relativeLayout = (RelativeLayout)row.findViewById(R.id.list_item);

            row.setTag(holder);
        }
        else
        {
            holder = (ListItemHolder)row.getTag();
        }

        //ListItem object = data[position];
        ListItem object = data.get(position);
        holder.txtTitle.setText(object.title);
        holder.imgIcon.setImageResource(object.icon);
        holder.distance.setText(object.distance);
        holder.relativeLayout.setBackgroundResource(object.background_image);
        return row;
    }




    static class ListItemHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
        TextView distance;
        RelativeLayout relativeLayout;

    }
}
