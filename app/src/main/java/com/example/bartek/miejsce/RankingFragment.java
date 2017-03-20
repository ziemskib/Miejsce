package com.example.bartek.miejsce;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bartek on 29.07.2016.
 */

//obsluguje ekran z rankingiem

public class RankingFragment extends Fragment {

    private FragmentActivity fa;
    private ListView listView;
    Context context;
    private List<ListItem> ListItem_data;
    public Handler mHandler;
    public View ftView;
    public boolean isLoading = false;
    private ListItemAdapter adapter;

    private AlertDialog mDialog; //do wyswietlania kategorii/filtrow

    // public int currentId = 10;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fa = super.getActivity();
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.list, container, false);
        context = container.getContext();

        LayoutInflater li = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftView =li.inflate(R.layout.list_footer_view,null);
        mHandler = new MyHandler();
        ListItem_data = new ArrayList<>();  //lista z danymi do listy

        ListItem_data.add(new ListItem(R.drawable.kat1_button_normal,"dfsa", "1km", "http://i.imgur.com/J34v6G3.jpg"));
        ListItem_data.add(new ListItem(R.drawable.kat1_button_normal, "fasdfsa", "0.5km", "http://i.imgur.com/J34v6G3.jpg"));
        ListItem_data.add(new ListItem(R.drawable.kat1_button_normal, "fadsfas", "1km", "http://i.imgur.com/J34v6G3.jpg"));
        ListItem_data.add(new ListItem(R.drawable.kat1_button_normal, "adsfsdfas", "1km", "http://i.imgur.com/J34v6G3.jpg"));
        ListItem_data.add(new ListItem(R.drawable.kat1_button_normal, "afdf", "1km", "http://i.imgur.com/J34v6G3.jpg"));
        ListItem_data.add(new ListItem(R.drawable.kat1_button_normal, "dafsdfas", "1km", "http://i.imgur.com/J34v6G3.jpg"));
        ListItem_data.add(new ListItem(R.drawable.kat1_button_normal,  "fsdaas", "1km","http://i.imgur.com/J34v6G3.jpg"));
        ListItem_data.add(new ListItem(R.drawable.kat1_button_normal,  "asdf", "1km", "http://i.imgur.com/J34v6G3.jpg"));
        ListItem_data.add(new ListItem(R.drawable.kat1_button_normal,  "fsdaas", "1km", "http://i.imgur.com/J34v6G3.jpg"));
        ListItem_data.add(new ListItem(R.drawable.kat1_button_normal,  "afsad", "4km", "http://i.imgur.com/J34v6G3.jpg"));



        /* ListItem ListItem_data[] = new ListItem[] {

                new ListItem(R.drawable.kat1_button_normal, R.drawable.forum, "fsdaas", "1km"),
                new ListItem(R.drawable.kat1_button_normal, R.drawable.restauracja,"fasdfsa", "0.5km"),
                new ListItem(R.drawable.kat1_button_normal, R.drawable.forum, "fadsfas", "1km"),
                new ListItem(R.drawable.kat1_button_normal, R.drawable.restauracja_midova, "adsfsdfas", "1km"),
                new ListItem(R.drawable.kat1_button_normal, R.drawable.forum, "afdf", "1km"),
                new ListItem(R.drawable.kat1_button_normal, R.drawable.restauracja_midova, "asdfs", "1km"),
                new ListItem(R.drawable.kat1_button_normal, R.drawable.forum, "asdf", "1km"),
                new ListItem(R.drawable.kat1_button_normal, R.drawable.restauracja_midova, "dafsdfas", "1km"),
                new ListItem(R.drawable.kat1_button_normal, R.drawable.forum, "asdfdfasd", "1km"),
                new ListItem(R.drawable.kat1_button_normal, R.drawable.restauracja_midova, "asdf", "1km"),
                new ListItem(R.drawable.kat1_button_normal, R.drawable.forum, "dsaffasd", "1km"),
                new ListItem(R.drawable.kat1_button_normal, R.drawable.restauracja_midova, "adsfasdf", "1km"),
                new ListItem(R.drawable.kat1_button_normal, R.drawable.forum, "dsafads", "1km"),
        };



        ListItemAdapter adapter = new ListItemAdapter(context,
                R.layout.list_element, ListItem_data);
*/
        adapter = new ListItemAdapter(context,R.layout.list_element,ListItem_data);


        listView = (ListView) rootView.findViewById(R.id.Lista);
        listView.setAdapter(adapter);

        //po kliknieciu w element listy
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                //do sth
                Toast.makeText(context, "Clicked id =" + view.getTag(), Toast.LENGTH_SHORT).show();
            }
        });


        //przy scrollowaniu
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //check when scroll to last item in listview
                if(view.getLastVisiblePosition() == totalItemCount-1 && !isLoading){
                    isLoading = true;
                    Thread thread = new ThreadGetMoreData();
                    //start thread
                    thread.start();
                }

            }
        });

        //Obsluga przycisku filtrow
        final ImageButton btn=(ImageButton) rootView.findViewById(R.id.filtr_button);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

            }
        });


        return rootView;
    }

    public class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    //Add loading view during search processing
                    listView.addFooterView(ftView);
                    break;
                case 1:
                    //Update data adapter and UI
                    adapter.addListItemToAdapter((ArrayList<ListItem>)msg.obj);
                    //Remove loading view after update listview
                    listView.removeFooterView(ftView);
                    isLoading=false;
                    break;
                default:
                    break;
            }
        }
    }

    private ArrayList<ListItem> getMoreData(){
        ArrayList<ListItem> lst = new ArrayList<>();
        //get new data
        lst.add(new ListItem(R.drawable.kat1_button_normal,"nowy1", "1km","http://i.imgur.com/J34v6G3.jpg"));
        lst.add(new ListItem(R.drawable.kat1_button_normal, "nowy2", "0.5km", "http://i.imgur.com/J34v6G3.jpg"));
        lst.add(new ListItem(R.drawable.kat1_button_normal, "nowy3", "1km","http://i.imgur.com/J34v6G3.jpg"));
        lst.add(new ListItem(R.drawable.kat1_button_normal, "nowy4", "1km","http://i.imgur.com/J34v6G3.jpg"));
        lst.add(new ListItem(R.drawable.kat1_button_normal, "nowy5", "1km","http://i.imgur.com/J34v6G3.jpg"));
        return lst;
    }

    public class ThreadGetMoreData extends Thread{
        @Override
        public void run(){
            //Add footer view after get data
            mHandler.sendEmptyMessage(0);
            //Search more data
            ArrayList<ListItem> lstResult = getMoreData();
            //Delay time to show loading footer when debug
            //Do usuniecia po polaczeniu z serwerem
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //Send the result to Handle
            Message msg = mHandler.obtainMessage(1, lstResult);
            mHandler.sendMessage(msg);


        }
    }
}
