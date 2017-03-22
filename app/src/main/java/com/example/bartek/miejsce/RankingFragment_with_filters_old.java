package com.example.bartek.miejsce;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bartek.miejsce.MyLocation.LocationResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Bartek on 29.07.2016.
 */

//obsluguje ekran z rankingiem

public class RankingFragment_with_filters_old extends Fragment {

    private FragmentActivity fa;
    Context context;

    ImageButton map_button;
    CheckBox cb1, cb2, cb3;
    int key = 0;
    //describes how many elements app should load
    int loadingStep = 5;
    ///////////////
    private List<ListItem> ListItem_data;
    public Handler mHandler;
    public View ftView;
    public boolean isLoading = false;
    private ListItemAdapter adapter;

    public MainActivity mainActivity;
    private ListView listView;

    final List<Place> places = new ArrayList<Place>();
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    Boolean firstLoaded = false;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fa = super.getActivity();
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.list_with_filters_old, container, false);
        //      ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.list, container, false);

        context = container.getContext();
        mainActivity = (MainActivity) getActivity();
        //Filtr list
        final Transparent popup = (Transparent) rootView.findViewById(R.id.popup_window);
        popup.setVisibility(View.GONE);

        map_button = (ImageButton) rootView.findViewById(R.id.map_button);

        map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapActivity.class);
                startActivity(intent);
            }
        });

/***************************/
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftView = li.inflate(R.layout.list_footer_view, null);
        mHandler = new MyHandler();
        ListItem_data = new ArrayList<>();  //lista z danymi do listy


        LocationResult locationResult = new LocationResult(){
            @Override
            public void gotLocation(Location location){
                //Got the location!
                mainActivity.userLatitude = location.getLatitude();
                mainActivity.userLongitude = location.getLongitude();
                }
        };
        MyLocation myLocation = new MyLocation(context, mainActivity);
        myLocation.getLocation(context, locationResult);
        //final double userLatitude = 57.037638;
        //final double userLongitude = 9.9336;

        adapter = new ListItemAdapter(context, R.layout.list_element, ListItem_data);

        listView = (ListView) rootView.findViewById(R.id.Lista);
        listView.setAdapter(adapter);

        DatabaseReference refPlaces = database.getReference("krakow/places");

        refPlaces.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mHandler.sendEmptyMessage(0);
                //Search more data
                final ArrayList<ListItem> lstResult = new ArrayList<ListItem>();
                //Send the result to Handle
                int num = 1;
                int count = (int) dataSnapshot.getChildrenCount();
                Place temp;
                for (int i = 1; i <= count; i++) {
                    temp = dataSnapshot.child(Integer.toString(i)).getValue(Place.class);
                    temp.countDistance(mainActivity.userLatitude, mainActivity.userLongitude);
                    places.add(temp);
                }
                //Sort places by distance
                Collections.sort(places);
                DatabaseReference refDetails = database.getReference("krakow/details");
                refDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Download rest data for first x places
                        //isLoading = true;
                        if (loadingStep > places.size())
                            loadingStep = places.size();
                        for (int i = 0; i < loadingStep; i++) {
                            String newName = dataSnapshot.child(Integer.toString(places.get(i).getId())).child("name").getValue(String.class);
                            places.get(i).setName(newName);
                            places.get(i).setBackgroundImage(dataSnapshot.child(Integer.toString(places.get(i).getId())).child("backgroundImage").getValue(String.class));
                            String distString;
                            distString = "";
                            Double dist = places.get(i).getDistance();
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
                            lstResult.add(new ListItem(R.drawable.kat1_button_normal, places.get(i).getName(), distString, places.get(i).getBackgroundImage()));
                        }
                        Message msg = mHandler.obtainMessage(1, lstResult);
                        mHandler.sendMessage(msg);
                        firstLoaded = true;
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        //list element Click Action
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Hide filtr list if is visible
                if (key == 1) {
                    key = 0;
                    popup.setVisibility(View.GONE);
                } else {
                    //do sth
                    Toast.makeText(context, "Clicked id =" + view.getTag(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //during scrolling
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //check when scroll to last item in listview
                if (view.getLastVisiblePosition() == totalItemCount - 1 && !isLoading && firstLoaded) {
                    isLoading = true;
                    Thread thread = new ThreadGetMoreData();
                    //start thread
                    thread.start();
                }
            }
        });
/**************************************/
        final ImageButton btn = (ImageButton) rootView.findViewById(R.id.filtr_button);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (key == 0) {
                    key = 1;
                    popup.setVisibility(View.VISIBLE);
                    //	btn.setBackgroundResource(R.drawable.ic_launcher);
                } else if (key == 1) {
                    key = 0;
                    popup.setVisibility(View.GONE);
                    //	btn.setBackgroundResource(R.drawable.ic_action_search);
                }
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
                    adapter.addListItemToAdapter((ArrayList<ListItem>) msg.obj);
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
        DatabaseReference refDetails = database.getReference("krakow/details");
        refDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Download rest data for next x places
                Log.d("Powtarzanie", Integer.toString(ListItem_data.size()));
                int max = ListItem_data.size()+loadingStep;
                if (max > places.size())
                    max = places.size();
                for (int i = ListItem_data.size(); i < max; i++) {
                    String newName = dataSnapshot.child(Integer.toString(places.get(i).getId())).child("name").getValue(String.class);
                    places.get(i).setName(newName);
                    places.get(i).setBackgroundImage(dataSnapshot.child(Integer.toString(places.get(i).getId())).child("backgroundImage").getValue(String.class));
                    String distString;
                    distString = "";
                    Double dist = places.get(i).getDistance();
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
                    lst.add(new ListItem(R.drawable.kat1_button_normal, places.get(i).getName(), distString, places.get(i).getBackgroundImage()));
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
