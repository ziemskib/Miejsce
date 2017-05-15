package com.example.bartek.miejsce.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.bartek.miejsce.model.ListItem;
import com.example.bartek.miejsce.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Bartek on 29.07.2016.
 */

//List of places screen Fragment
public class RankingFragment_with_filters extends Fragment {

    public MainActivity mainActivity;

    Context context;

    ImageButton map_button;
    public Handler mHandler;
    public View ftView;
    public boolean isLoading = false;

    private ListView listView;

    //final List<Place> places = new ArrayList<Place>();
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    //Boolean firstLoaded = false;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.list, container, false);
        context = container.getContext();
        mainActivity = (MainActivity) getActivity();

        map_button = (ImageButton) rootView.findViewById(R.id.map_button);
        map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapActivity.class);
                startActivity(intent);
                //mainActivity.mPager.setCurrentItem(mainActivity.mPager.getCurrentItem()+1);
            }
        });

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftView = li.inflate(R.layout.list_footer_view, null);
        mHandler = new MyHandler();

        listView = (ListView) rootView.findViewById(R.id.Lista);
        //listView.setAdapter(adapter);
        listView.setAdapter(mainActivity.adapter);
        //During scrolling
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //check when scroll to last item in listview
                if (view.getLastVisiblePosition() == totalItemCount - 1 && !isLoading && mainActivity.firstLoaded) {
                    isLoading = true;
                    Thread thread = new ThreadGetMoreData();
                    //start thread
                    thread.start();
                }
            }
        });

        final ImageButton btn = (ImageButton) rootView.findViewById(R.id.filtr_button);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //On filtr button click
                startActivity(new Intent(context,Pop.class));
            }
        });
        return rootView;
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //Add loading view during search processing
                    listView.addFooterView(ftView);
                    break;
                case 1:
                    //Update data adapter and UI
                    mainActivity.adapter.addListItemToAdapter((ArrayList<ListItem>) msg.obj);
                    //Remove loading view after update listview
                    listView.removeFooterView(ftView);
                   // isLoading = false;
                    break;
                default:
                    break;
            }
        }
    }
    private ArrayList<ListItem> getMoreData() {
        final ArrayList<ListItem> lst = new ArrayList<>();
        //get new data
        DatabaseReference refDetails = database.getReference("city_details/"+Integer.toString(mainActivity.city_id)+"/place_details");
        refDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Download rest data for next x places
                Log.d("Powtarzanie", Integer.toString(mainActivity.ListItem_data.size()));
                int max = mainActivity.ListItem_data.size()+mainActivity.loadingStep;
                if (max > mainActivity.places.size())
                    max = mainActivity.places.size();
                for (int i = mainActivity.ListItem_data.size(); i < max; i++) {
                    String newName = dataSnapshot.child(Integer.toString(mainActivity.places.get(i).getId())).child("name").getValue(String.class);
                    mainActivity.places.get(i).setName(newName);
                    mainActivity.places.get(i).setBackgroundImage(dataSnapshot.child(Integer.toString(mainActivity.places.get(i).getId())).child("backgroundImage").getValue(String.class));
                    String distString;
                    distString = "";
                    Double dist = mainActivity.places.get(i).getDistance();
                    if(dist!=-1){
                        if(dist >= 1)
                        {
                            distString = Integer.toString(dist.intValue()) + " km";
                        }
                        else if(dist<1){
                            dist = dist*1000;
                            distString = Integer.toString(dist.intValue()) + " m";
                        }
                    }
                    lst.add(new ListItem(mainActivity.places.get(i).getId(), mainActivity.places.get(i).getDistance(), R.drawable.kat1_button_normal, mainActivity.places.get(i).getName(), distString, mainActivity.places.get(i).getBackgroundImage(), 0, 0));
                }
                Message msg = mHandler.obtainMessage(1, lst);
                mHandler.sendMessage(msg);
                isLoading = false;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return lst;
    }

    public class ThreadGetMoreData extends Thread {
        @Override
        public void run() {
            //Add footer view after get data
            mHandler.sendEmptyMessage(0);
            //Search more data
            ArrayList<ListItem> lstResult = getMoreData();
            //Send the result to Handle
            Message msg = mHandler.obtainMessage(1, lstResult);
            mHandler.sendMessage(msg);
        }
    }
}