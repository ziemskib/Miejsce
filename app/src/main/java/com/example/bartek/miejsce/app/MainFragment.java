package com.example.bartek.miejsce.app;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.bartek.miejsce.R;
import com.example.bartek.miejsce.app.MainActivity;
import com.example.bartek.miejsce.model.Place;
import com.squareup.picasso.Picasso;

/**
 * Main Screen Fragment
*/
public class MainFragment extends Fragment {

    private static final int DELAY_MSG = 42;
    private static final int DELAY = 7000;  //Period of sliding pictures in viewFlipper
    public MainActivity mainActivity;

    public ViewFlipper viewFlipper;
    Button next; //Buttons to slide pictures in viewFlipper
    Button previous;
    Context context;
    TextView city;  //String: city
    //Animations to slide pictures in viewFlipper
    private Animation slideLeftInAuto;
    private Animation slideLeftOutAuto;
    private Animation slideLeftIn;
    private Animation slideLeftOut;
    private Animation slideRightIn;
    private Animation slideRightOut;

    private boolean isStarted = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        mainActivity = (MainActivity) getActivity();

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.main_fragment_layout, container, false);
        context = container.getContext();

        viewFlipper = (ViewFlipper) rootView.findViewById(R.id.viewFlipper);
        next = (Button) rootView.findViewById(R.id.next);
        previous = (Button) rootView.findViewById(R.id.previous);
        slideLeftInAuto = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_from_right);
        slideLeftOutAuto = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_left);
        slideLeftIn = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_from_right);
        slideLeftOut = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_left);
        slideRightIn = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_from_left);
        slideRightOut = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_right);
        city = (TextView) rootView.findViewById(R.id.city);
        city.setText(mainActivity.city);
        setImages();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopFlipper();
                viewFlipper.setInAnimation(slideLeftIn);
                viewFlipper.setOutAnimation(slideLeftOut);
                viewFlipper.showNext();
                next.setEnabled(false);
                previous.setEnabled(false);
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopFlipper();
                viewFlipper.setInAnimation(slideRightIn);
                viewFlipper.setOutAnimation(slideRightOut);
                viewFlipper.showPrevious();
                previous.setEnabled(false);
                next.setEnabled(false);
            }
        });

        //To disable buttons during sliding pictures
        slideLeftInAuto.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                previous.setEnabled(false);
                next.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                previous.setEnabled(true);
                next.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        slideLeftOut.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation animation) {
                previous.setEnabled(false);
                next.setEnabled(false);
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                next.setEnabled(true);
                previous.setEnabled(true);
                runFlipper();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        slideRightOut.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation animation) {
                previous.setEnabled(false);
                next.setEnabled(false);


            }
            @Override
            public void onAnimationEnd(Animation animation) {
                next.setEnabled(true);
                previous.setEnabled(true);
                runFlipper();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        return rootView;
    }
    //Setting images to viewFlipper
    public void setImages(){
        for(int i=0; i<mainActivity.main_places.size(); i++) {
            final Place tempPlace = mainActivity.main_places.get(i);
            ImageView imageView1 = new ImageView(context);
            Picasso.with(context).load(tempPlace.getBackgroundImage()).into(imageView1);
            imageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PlaceActivity.class);
                    intent.putExtra("place", tempPlace);
                    startActivity(intent);
                }
            });
            viewFlipper.addView(imageView1);
        }
        runFlipper();
        viewFlipper.setInAnimation(slideLeftInAuto);
        viewFlipper.setOutAnimation(slideLeftOutAuto);
        return;
    }

    private void runFlipper() {
        if (isStarted == false) {
            viewFlipper.setInAnimation(slideLeftInAuto);
            viewFlipper.setOutAnimation(slideLeftOutAuto);
            Message msg = mHandler.obtainMessage(DELAY_MSG);
            mHandler.sendMessageDelayed(msg, DELAY);
            isStarted = true;
        }
    }

    private void stopFlipper() {
        mHandler.removeMessages(DELAY_MSG);
        isStarted = false;
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == DELAY_MSG) {
                viewFlipper.showNext();
                msg = obtainMessage(DELAY_MSG);
                sendMessageDelayed(msg, DELAY);
            }
        }
    };
}