package com.example.bartek.miejsce;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bartek on 29.07.2016.
 */
public class RankingFragment extends Fragment {

    private FragmentActivity fa;
    private ListView listView;
    Context context;
    private List<ListItem> ListItem_data;
    public Handler mHandler;
    public View ftView;
    public boolean isLoading = false;
    private ListItemAdapter adapter;
   // public int currentId = 10;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fa = super.getActivity();
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.list, container, false);
        context = container.getContext();

        LayoutInflater li = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftView =li.inflate(R.layout.list_footer_view,null);
        mHandler = new MyHandler();
        ListItem_data = new ArrayList<>();

        ListItem_data.add(new ListItem(R.drawable.kat1_button_normal, R.drawable.forum, "fsdaas", "1km"));
        ListItem_data.add(new ListItem(R.drawable.kat1_button_normal, R.drawable.restauracja,"fasdfsa", "0.5km"));
        ListItem_data.add(new ListItem(R.drawable.kat1_button_normal, R.drawable.forum, "fadsfas", "1km"));
        ListItem_data.add(new ListItem(R.drawable.kat1_button_normal, R.drawable.restauracja_midova, "adsfsdfas", "1km"));
        ListItem_data.add(new ListItem(R.drawable.kat1_button_normal, R.drawable.forum, "afdf", "1km"));
        ListItem_data.add(new ListItem(R.drawable.kat1_button_normal, R.drawable.restauracja_midova, "dafsdfas", "1km"));
        ListItem_data.add(new ListItem(R.drawable.kat1_button_normal, R.drawable.forum, "fsdaas", "1km"));
        ListItem_data.add(new ListItem(R.drawable.kat1_button_normal, R.drawable.restauracja_midova, "asdf", "1km"));
        ListItem_data.add(new ListItem(R.drawable.kat1_button_normal, R.drawable.forum, "fsdaas", "1km"));
        ListItem_data.add(new ListItem(R.drawable.kat1_button_normal, R.drawable.restauracja_midova, "afsad", "4km"));



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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                //do sth
                Toast.makeText(context, "Clicked id =" + view.getTag(), Toast.LENGTH_SHORT).show();
            }
        });

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
        lst.add(new ListItem(R.drawable.kat1_button_normal, R.drawable.forum, "nowy1", "1km"));
        lst.add(new ListItem(R.drawable.kat1_button_normal, R.drawable.restauracja,"nowy2", "0.5km"));
        lst.add(new ListItem(R.drawable.kat1_button_normal, R.drawable.forum, "nowy3", "1km"));
        lst.add(new ListItem(R.drawable.kat1_button_normal, R.drawable.restauracja_midova, "nowy4", "1km"));
        lst.add(new ListItem(R.drawable.kat1_button_normal, R.drawable.forum, "nowy5", "1km"));
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
