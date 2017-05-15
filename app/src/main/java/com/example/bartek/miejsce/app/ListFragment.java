package com.example.bartek.miejsce.app;

/**
 * Created by Bartosz Ziemski on 19/04/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bartek.miejsce.OnLoadMoreListener;
import com.example.bartek.miejsce.R;
import com.example.bartek.miejsce.model.ListItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {
    public MainActivity mainActivity;
    private RecyclerView mRecyclerView;
    private List<ListItem> listItems = new ArrayList<> ();
    private UserAdapter mUserAdapter;
    private ImageButton mapButton;
    private ImageButton filtrButton;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_card, container, false);

        mapButton = (ImageButton) view.findViewById(R.id.map_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.mPager.setCurrentItem(mainActivity.mPager.getCurrentItem()+1);
            }
        });

        filtrButton = (ImageButton) view.findViewById(R.id.filtr_button);
        filtrButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(getContext(),Pop.class));
            }
        });
        mRecyclerView = (RecyclerView) view.findViewById(R.id.cardView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUserAdapter = new UserAdapter();
        mRecyclerView.setAdapter(mUserAdapter);
        mUserAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override public void onLoadMore() {
                //listItems.add(null);
               // mUserAdapter.notifyItemInserted(listItems.size() - 1);
                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        if(mainActivity.firstLoaded){
                            //Remove loading item
                            //listItems.remove(listItems.size() - 1);
                            //mUserAdapter.notifyItemRemoved(listItems.size());
                            //Load more data
                            loadMoreData();
                        }
                    }
                }, 0);
            }
        });
        return view;
    }

    class UserViewHolder extends RecyclerView.ViewHolder{
        public int id;
        public double distance;
        public TextView placeName;
        public TextView distanceTextView;
        public ImageView coverImageView;
        public ImageView likeImageView;
        public ImageView shareImageView;
        public UserViewHolder(View itemView) {
            super(itemView);
            placeName = (TextView) itemView.findViewById(R.id.titleTextView);
            distanceTextView = (TextView) itemView.findViewById(R.id.distanceTextView);
            coverImageView = (ImageView) itemView.findViewById(R.id.coverImageView);
            likeImageView = (ImageView) itemView.findViewById(R.id.likeImageView);
            shareImageView = (ImageView) itemView.findViewById(R.id.shareImageView);
            likeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = (int)likeImageView.getTag();
                    if( id == R.drawable.ic_like){
                        likeImageView.setTag(R.drawable.ic_liked);
                        likeImageView.setImageResource(R.drawable.ic_liked);
                        Toast.makeText(getActivity(),placeName.getText()+" added to favourites",Toast.LENGTH_SHORT).show();
                    }else{
                        likeImageView.setTag(R.drawable.ic_like);
                        likeImageView.setImageResource(R.drawable.ic_like);
                        Toast.makeText(getActivity(),placeName.getText()+" removed from favourites",Toast.LENGTH_SHORT).show();
                    }

                }
            });
            shareImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                            "://" + getResources().getResourcePackageName(coverImageView.getId())
                            + '/' + "drawable" + '/' + getResources().getResourceEntryName((int)coverImageView.getTag()));
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM,imageUri);
                    shareIntent.setType("image/jpeg");
                    startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));*/
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Przyciemnić klikniętą kartę
                    Toast.makeText(getActivity(), "Clicked id =" + Integer.toString(id), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), PlaceActivity.class);
                    intent.putExtra("placeId", id);
                    intent.putExtra("cityId", mainActivity.city_id);
                    intent.putExtra("distance", distance);
                    intent.putExtra("placeName", placeName.getText().toString());
                    startActivity(intent);
                }
            });

        }

    }
    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }

    class UserAdapter extends RecyclerView.Adapter < RecyclerView.ViewHolder > {
        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;
        private OnLoadMoreListener mOnLoadMoreListener;
        private boolean isLoading;
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;

        public UserAdapter() {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }
                }
            });
        }
        public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.mOnLoadMoreListener = mOnLoadMoreListener;
        }
        @Override public int getItemViewType(int position) {
            return listItems.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }
        @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.recycle_items, parent, false);
                return new ListFragment.UserViewHolder(view);
            } else if (viewType == VIEW_TYPE_LOADING) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_loading_item, parent, false);
                return new ListFragment.LoadingViewHolder(view);
            }
            return null;
        }
        @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ListFragment.UserViewHolder) {
                ListItem tempPlace = listItems.get(position);
                ListFragment.UserViewHolder userViewHolder = (ListFragment.UserViewHolder) holder;
                userViewHolder.id = tempPlace.id;
                userViewHolder.distance=tempPlace.double_distance;
                userViewHolder.placeName.setText(tempPlace.title);
                userViewHolder.distanceTextView.setText(tempPlace.distance);
                Picasso.with(getContext()).load(tempPlace.backgroungImageURL).into(userViewHolder.coverImageView);
                // userViewHolder.coverImageView.setTag(list.get(position).getImageResourceId());
                if(tempPlace.isfav==1){
                    userViewHolder.likeImageView.setImageResource(R.drawable.ic_liked);
                    userViewHolder.likeImageView.setTag(R.drawable.ic_liked);
                }
                else{
                    userViewHolder.likeImageView.setImageResource(R.drawable.ic_like);
                    userViewHolder.likeImageView.setTag(R.drawable.ic_like);
                }
            } else if (holder instanceof ListFragment.LoadingViewHolder) {
                ListFragment.LoadingViewHolder loadingViewHolder = (ListFragment.LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }
        }
        @Override public int getItemCount() {
            return listItems == null ? 0 : listItems.size();
        }
        public void setLoaded() {
            isLoading = false;
        }
    }

    public void createList(List<ListItem> newList){
        listItems = newList;
        mUserAdapter.notifyDataSetChanged();
    }

    public void loadMoreData(){
        DatabaseReference refDetails = database.getReference("city_details/"+Integer.toString(mainActivity.city_id)+"/place_details");
        refDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Download rest data for next x places
                int max = listItems.size()+mainActivity.loadingStep;
                if (max > mainActivity.places.size())
                    max = mainActivity.places.size();
                for (int i = listItems.size(); i < max; i++) {
                    String newName = dataSnapshot.child(Integer.toString(mainActivity.places.get(i).getId())).child("name").getValue(String.class);
                    Log.d("Pobieranie", "Jestem w petli, pobralem: " + newName);
                    mainActivity.places.get(i).setName(newName);
                    mainActivity.places.get(i).setBackgroundImage(dataSnapshot.child(Integer.toString(mainActivity.places.get(i).getId())).child("backgroundImage").getValue(String.class));
                    String distString;
                    distString = "";
                    Double dist = mainActivity.places.get(i).getDistance();
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
                    listItems.add(new ListItem(mainActivity.places.get(i).getId(), mainActivity.places.get(i).getDistance() ,R.drawable.kat1_button_normal, mainActivity.places.get(i).getName(), distString, mainActivity.places.get(i).getBackgroundImage(), 0, 0));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mUserAdapter.notifyDataSetChanged();
        mUserAdapter.setLoaded();
    }

    public void resume(){
        mUserAdapter.notifyDataSetChanged();
        mUserAdapter.setLoaded();
        //listItems.add(null);
    }
}