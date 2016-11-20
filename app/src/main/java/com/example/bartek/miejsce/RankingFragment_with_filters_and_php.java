package com.example.bartek.miejsce;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Bartek on 29.07.2016.
 */

//obsluguje ekran z rankingiem

public class RankingFragment_with_filters_and_php extends Fragment {

    private FragmentActivity fa;
    Context context;
    CheckBox cb1,cb2,cb3;
    int key=0;

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT=15000;

///////////////
    private List<ListItem> ListItem_data;
    public Handler mHandler;
    public View ftView;
    public boolean isLoading = false;
    private ListItemAdapter adapter;

    private ListView listView;
    ////////////////


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fa = super.getActivity();
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.list_with_filters, container, false);
  //      ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.list, container, false);

        context = container.getContext();

        new GetData().execute();


/***************************/
        LayoutInflater li= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftView =li.inflate(R.layout.list_footer_view,null);
        mHandler = new MyHandler();
        ListItem_data = new ArrayList<>();  //lista z danymi do listy



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




/**************************************/



        final Transparent popup = (Transparent) rootView.findViewById(R.id.popup_window);
        popup.setVisibility(View.GONE);

        final ImageButton btn=(ImageButton) rootView.findViewById(R.id.filtr_button);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(key==0){
                    key=1;
                    popup.setVisibility(View.VISIBLE);
                    //	btn.setBackgroundResource(R.drawable.ic_launcher);
                }
                else if(key==1){
                    key=0;
                    popup.setVisibility(View.GONE);
                    //	btn.setBackgroundResource(R.drawable.ic_action_search);
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

    private class GetData extends AsyncTask<String, Void, String> {
        //okienko "czekaj"
        private ProgressDialog dialog = new ProgressDialog(getActivity());

        // metoda wykonywana jest zaraz przed główną operacją (doInBackground())
        // mamy w niej dostęp do elementów UI
        @Override
        protected void onPreExecute(){
            //wyswietlamy progressdialog "czekaj"
            dialog.setMessage("Loading...");
            dialog.show();
        }

        // główna operacja, która wykona się w osobnym wątku
        // nie ma w niej dostępu do elementów UI
        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL("http://192.168.221.245/index.php");
                HttpURLConnection connection =(HttpURLConnection) url.openConnection();

                connection.setDoInput(true);
                InputStream is = new BufferedInputStream(connection.getInputStream());
               // String s = readStream(is);
                /*
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String wynik1 = bufferedReader.readLine();
                ListItem_data.add(new ListItem(R.drawable.kat1_button_normal, R.drawable.forum, "zrobione", "1km"));
*/
                //return  wynik1;

                //pobranie danych do InputStream
                /*InputStream in = new BufferedInputStream(
                        connection.getInputStream());


                //konwersja InputStream na String
                //wynik bedzie przekazany do metody onPostExecute()
               */
                return streamToString(is);

            } catch(Exception e) {
                //obsluz wyjatek

                return "Blad w doInBackgroun";
            }
        }

        // metoda wykonuje się po zakończeniu metody głównej,
        // której wynik będzie przekazany;
        // w tej metodzie mamy dostęp do UI
        @Override
        protected void onPostExecute(String result){
            //usuwamy okno dialogowe
            dialog.dismiss();

            String jsonStr = result;
            if(jsonStr!=null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);

                    ListItem_data.add(new ListItem(R.drawable.kat1_button_normal, R.drawable.forum, jsonObject.getString("imie"), "1km"));
                    ListItem_data.add(new ListItem(R.drawable.kat1_button_normal, R.drawable.restauracja, "fasdfsa", "0.5km"));
                    ListItem_data.add(new ListItem(R.drawable.kat1_button_normal, R.drawable.forum, "fadsfas", "1km"));
                    ListItem_data.add(new ListItem(R.drawable.kat1_button_normal, R.drawable.restauracja_midova, "adsfsdfas", "1km"));
                    ListItem_data.add(new ListItem(R.drawable.kat1_button_normal, R.drawable.forum, "afdf", "1km"));
                    ListItem_data.add(new ListItem(R.drawable.kat1_button_normal, R.drawable.restauracja_midova, "dafsdfas", "1km"));
                    ListItem_data.add(new ListItem(R.drawable.kat1_button_normal, R.drawable.forum, "fsdaas", "1km"));
                    ListItem_data.add(new ListItem(R.drawable.kat1_button_normal, R.drawable.restauracja_midova, "asdf", "1km"));
                    ListItem_data.add(new ListItem(R.drawable.kat1_button_normal, R.drawable.forum, "fsdaas", "1km"));
                    ListItem_data.add(new ListItem(R.drawable.kat1_button_normal, R.drawable.restauracja_midova, "afsad", "4km"));

                } catch (Exception e) {
                    //Obsluz wyjatek
                    e.printStackTrace();
                    //Toast.makeText(context, "Error parsing JSON data.", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(context, "Nie mozna zaladowac", Toast.LENGTH_SHORT).show();
            }


        }
    }

    //konwersja z InputStream do String
    public static String streamToString(InputStream is){
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        try{
            while ((line = reader.readLine())!= null) {
                stringBuilder.append(line);
            }
            reader.close();
        } catch(IOException e){
            //obsluz wyjatek
            return "Blad w streamToString";
        }
        return stringBuilder.toString();

    }

}
