package com.example.bartek.miejsce;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

/**
 * Created by Bartek on 26.07.2016.
 */
public class MainFragment extends Fragment {

    private LinearLayout ll;
    private FragmentActivity fa;

    ViewFlipper viewFlipper;
    Button next;
    Button previous;
    Context context;
    TextView city;
    private Animation slideLeftIn;
    private Animation slideLeftOut;
    private Animation slideRightIn;
    private Animation slideRightOut;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        fa = super.getActivity();
        //  ll = (LinearLayout) inflater.inflate(R.layout.menu, container, false);

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.main_fragment_layout, container, false);
        context = container.getContext();

        viewFlipper = (ViewFlipper) rootView.findViewById(R.id.viewFlipper);
        next = (Button) rootView.findViewById(R.id.next);
        previous = (Button) rootView.findViewById(R.id.previous);
        slideLeftIn = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_from_right);
        slideLeftOut = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_left);
        slideRightIn = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_from_left);
        slideRightOut = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_right);
        city = (TextView) rootView.findViewById(R.id.city);
        //Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MyriadProRegular");
        //city.setTypeface(font);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.setInAnimation(slideLeftIn);
                viewFlipper.setOutAnimation(slideLeftOut);
                viewFlipper.showNext();
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.setInAnimation(slideRightIn);
                viewFlipper.setOutAnimation(slideRightOut);
                viewFlipper.showPrevious();
            }
        });
        return rootView;


        // return ll;
    }
}
