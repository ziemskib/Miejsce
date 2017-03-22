package com.example.bartek.miejsce;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Bartosz Ziemski on 25/03/2017.
 */

public class FiltrListAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<FiltrItem> mNavItems;

    public FiltrListAdapter(Context context, ArrayList<FiltrItem> navItems) {
        mContext = context;
        mNavItems = navItems;
    }

    @Override
    public int getCount() {
        return mNavItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mNavItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View view;

        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.filtr_item, null);
        }
        else{
            view = convertView;
        }

        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);
        ImageView iconView = (ImageView) view.findViewById(R.id.imgIcon);

        titleView.setText(mNavItems.get(i).mTitle);
        //iconView.setImageResource(mNavItems.get(i).mIcon);
        subtitleView.setText(mNavItems.get(i).mSubtitle);

        return view;
    }
}
