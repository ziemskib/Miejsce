package com.example.bartek.miejsce;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.squareup.picasso.Picasso;

/**
 * Created by Bartek on 26.07.2016.
 */

/**
 * obsluguje glowny ekran
*/
public class MainFragment extends Fragment {

    private LinearLayout ll;
    private FragmentActivity fa;

    public ViewFlipper viewFlipper;
    //przyciski do przesuwania slajdow/zdjec
    Button next;
    Button previous;
    ImageButton kat1Button;
    Context context;
    TextView city;  //napis: miasto
    //animacje do przesuwania slajdow/zdjec
    private Animation slideLeftInAuto;
    private Animation slideLeftOutAuto;
    private Animation slideLeftIn;
    private Animation slideLeftOut;
    private Animation slideRightIn;
    private Animation slideRightOut;
    public MainActivity mainActivity;


    private boolean isStarted = false;
    private static final int DELAY_MSG = 42;
    private static final int DELAY = 3000;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        fa = super.getActivity();
        //  ll = (LinearLayout) inflater.inflate(R.layout.menu, container, false);
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
        //Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MyriadProRegular");
        //city.setTypeface(font);

        //po kliknieciu next pokaz kolejne zdjecie
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

        //Just to disable buttons during sliding pictures
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



    public void setImages(){
        for(int i=0; i<mainActivity.main_places.size(); i++) {
            ImageView imageView1 = new ImageView(context);
            Picasso.with(context).load(mainActivity.main_places.get(i).getBackgroundImage()).into(imageView1);
            // imageView1.setAdjustViewBounds(true);
            imageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Clicked id =" + view.getTag(), Toast.LENGTH_SHORT).show();
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

    //@SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == DELAY_MSG) {
                Log.d("ImageFlipper", "Next picture...");
                viewFlipper.showNext();
                msg = obtainMessage(DELAY_MSG);
                sendMessageDelayed(msg, DELAY);
            }
        }
    };

}
