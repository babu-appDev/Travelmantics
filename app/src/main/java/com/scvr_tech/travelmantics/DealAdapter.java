package com.scvr_tech.travelmantics;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealHolder> {

    private Context mContext;
    private ArrayList<TravelDeal> mDeals;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    public DealAdapter(Context context) {
        this.mDeals = FirebaseUtil.sDeals;
        FirebaseUtil.openFirebaseRef("traveldeals");
        mFirebaseDatabase = FirebaseUtil.sFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.sDatabaseReference;

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                TravelDeal td = dataSnapshot.getValue(TravelDeal.class);
                Log.d("Deal: ", td.getTitle());
                td.setId(dataSnapshot.getKey());
                mDeals.add(td);
                notifyItemInserted(mDeals.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addChildEventListener(mChildEventListener);
        this.mContext = context;
    }

    @NonNull
    @Override
    public DealHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new DealHolder(inflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull DealHolder holder, int position) {
        holder.bindDeal(mDeals.get(position));
    }

    @Override
    public int getItemCount() {
        return mDeals.size();
    }

    class DealHolder extends RecyclerView.ViewHolder {

        private ImageView dImage;
        private TextView dPrice, dDescription, dTitle;

        private TravelDeal mDeal;

        DealHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_deals, parent, false));

            dImage = itemView.findViewById(R.id.deal_image);
            dPrice = itemView.findViewById(R.id.deal_price);
            dDescription = itemView.findViewById(R.id.deal_description);
            dTitle = itemView.findViewById(R.id.deal_title);
        }

        private void bindDeal(final TravelDeal deal) {
            mDeal = deal;

            dPrice.setText(mDeal.getPrice());
            dDescription.setText(mDeal.getDescription());
            dTitle.setText(mDeal.getTitle());

            //Picasso.get()
            //        .load(deal.getImgUrl())
            //        .into(dImage);
        }
    }
}
