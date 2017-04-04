package com.example.bartek.miejsce;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterViewFlipper;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bartek on 26.07.2016.
 */

/**
 * obsluguje glowny ekran
*/
public class MainFragmentAVF extends Fragment {

    private LinearLayout ll;
    private FragmentActivity fa;

    //przyciski do przesuwania slajdow/zdjec
    Button next;
    Button previous;
    ImageButton kat1Button;
    Context context;
    TextView city;  //napis: miasto
    public ArrayList<PlaceDetail> ListImageItem_data;        //add get set
    public ImageItemAdapter adapter;

    private AdapterViewFlipper aVF;
    //animacje do przesuwania slajdow/zdjec
    //private ObjectAnimator slideLeftIn;
    //private Animation slideLeftOut;
    //private Animation slideRightIn;
    //private Animation slideRightOut;
    public MainActivity mainActivity;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        fa = super.getActivity();
        //  ll = (LinearLayout) inflater.inflate(R.layout.menu, container, false);
        mainActivity = (MainActivity) getActivity();

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.main_fragment_avf, container, false);
        context = container.getContext();

        aVF = (AdapterViewFlipper) rootView.findViewById(R.id.adapterViewFlipper);

        PlaceDetail first = new PlaceDetail("http://i.imgur.com/HN7HaEr.jpg?1");
        ListImageItem_data = new ArrayList<PlaceDetail>();  //lista z danymi do listy
        ListImageItem_data.add(first);
        ListImageItem_data.add(first);
        ListImageItem_data.add(first);
        adapter = new ImageItemAdapter(context, R.layout.avf_screen, ListImageItem_data);

        aVF.setAdapter(adapter);
        aVF.startFlipping();
        adapter.addListItemToAdapter((ArrayList<PlaceDetail>) ListImageItem_data);
        next = (Button) rootView.findViewById(R.id.next);
        previous = (Button) rootView.findViewById(R.id.previous);
//        slideLeftIn = AnimationUtils.loadAnimation(getActivity(), R.anim.left_in);
  //      slideLeftOut = AnimationUtils.loadAnimation(getActivity(), R.anim.right_out);
    //    slideRightIn = AnimationUtils.loadAnimation(getActivity(), R.anim.right_in);
      //  slideRightOut = AnimationUtils.loadAnimation(getActivity(), R.anim.left_out);
        city = (TextView) rootView.findViewById(R.id.city);
        city.setText(mainActivity.city);
        //Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MyriadProRegular");
        //city.setTypeface(font);

        //po kliknieciu next pokaz kolejne zdjecie
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aVF.stopFlipping();
                aVF.setInAnimation(context, R.animator.left_in);
                aVF.setOutAnimation(context, R.animator.right_out);
                aVF.showPrevious();
                //viewFlipper.setInAnimation(slideLeftIn);
                //viewFlipper.setOutAnimation(slideLeftOut);
                //viewFlipper.showNext();
        //        next.setEnabled(false);
         //       previous.setEnabled(false);
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aVF.stopFlipping();
                aVF.setInAnimation(context, R.animator.right_in);
                aVF.setOutAnimation(context, R.animator.left_out);
                aVF.showPrevious();
                /*viewFlipper.setInAnimation(slideRightIn);
                viewFlipper.setOutAnimation(slideRightOut);
                viewFlipper.showPrevious();
                previous.setEnabled(false);
                next.setEnabled(false);*/
            }
        });

        //Just to disable buttons during sliding pictures
        /*slideLeftOut.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                next.setEnabled(true);
                previous.setEnabled(true);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        slideRightOut.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                next.setEnabled(true);
                previous.setEnabled(true);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
*/
        return rootView;
    }
}
