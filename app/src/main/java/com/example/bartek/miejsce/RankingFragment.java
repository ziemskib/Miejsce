package com.example.bartek.miejsce;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

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

    //Filtr list
    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    ArrayList<FiltrItem> mNavItems = new ArrayList<FiltrItem>();


    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fa = super.getActivity();
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.list_with_drawer, container, false);
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

        //Filtr list
        // More info: http://codetheory.in/difference-between-setdisplayhomeasupenabled-sethomebuttonenabled-and-setdisplayshowhomeenabled/

        //ab.setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    /*    @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Pass the event to ActionBarDrawerToggle
            // If it returns true, then it has handled
            // the nav drawer indicator touch event
            if (mDrawerToggle.onOptionsItemSelected(item)) {
                return true;
            }

            // Handle your other action bar items...

            return super.onOptionsItemSelected(item);
        }*/



        ImageButton map_button = (ImageButton) rootView.findViewById(R.id.map_button);

        map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapActivity.class);
                startActivity(intent);
            }
        });

        mNavItems.add(new FiltrItem("Home", "Meetup destination", R.drawable.kat1_button));
        mNavItems.add(new FiltrItem("Preferences", "Change your preferences", R.drawable.kat1_button));
        mNavItems.add(new FiltrItem("About", "Get to know about us", R.drawable.kat1_button));

        DrawerLayout mDrawerLayout = (DrawerLayout) rootView.findViewById(R.id.drawerLayout);
        //Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) rootView.findViewById(R.id.drawerPane);
        mDrawerList = (ListView) rootView.findViewById(R.id.navList);

        FiltrListAdapter filtr_adapter = new FiltrListAdapter(this.context, mNavItems);
        mDrawerList.setAdapter(filtr_adapter);

        //Filtr Item click listeners
        //WRZUCIC TU



        mDrawerToggle = new ActionBarDrawerToggle(this.getActivity(), mDrawerLayout, R.string.app_name, R.string.next){
          @Override
            public void onDrawerOpened(View drawerView){
              super.onDrawerOpened(drawerView);
                //invalidateOptionsMenu();
          }

          @Override
            public void onDrawerClosed(View drawerView){
              super.onDrawerClosed(drawerView);
              Log.d(TAG,"onDrowerClosed: ");
              //invalidateOptionsMenu();
          }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);


        //mDrawerToggle.setToolbarNavigationClickListener();

        //Ranking list
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
                startActivity(new Intent(context,Pop.class));
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
    // Called when invalidateOptionsMenu() is invoked
    /*public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_search).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }*/
}
