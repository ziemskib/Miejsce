package com.example.bartek.miejsce.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.ListView;

import com.example.bartek.miejsce.R;
import com.example.bartek.miejsce.adapter.FiltrListAdapter;
import com.example.bartek.miejsce.model.FiltrItem;

import java.util.ArrayList;

/**
 * Created by Bartosz Ziemski on 28/03/2017.
 */

public class Pop extends Activity {
    private ArrayList<FiltrItem> filterList;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.filtr_window);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*.8), (int) (height*.6));
        filterList = new ArrayList<>();

        Intent intent = getIntent();

        ArrayList<String> filtersString = intent.getStringArrayListExtra("filters");

        FiltrItem temp;
        for(int i=0; i<filtersString.size(); i++) {
            temp = new FiltrItem(filtersString.get(i), "subtitle", R.drawable.common_google_signin_btn_icon_dark);
            filterList.add(temp);
        }
        FiltrListAdapter adapter = new FiltrListAdapter(this, filterList);
        ListView listView = (ListView) findViewById(R.id.filtr_list);
        listView.setAdapter(adapter);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If we've received a touch notification that the user has touched
        // outside the app, finish the activity.
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            finish();
            return true;
        }
        // Delegate everything else to Activity.
        return super.onTouchEvent(event);
    }
}
