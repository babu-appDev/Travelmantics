package com.scvr_tech.travelmantics;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

    private ArrayList<TravelDeal> mDeals;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    public DealAdapter() {
        mFirebaseDatabase = FirebaseUtil.sFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.sDatabaseReference;
        mDeals = FirebaseUtil.sDeals;
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
    }

    @NonNull
    @Override
    public DealHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
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

    class DealHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView dImage;
        private TextView dPrice, dDescription, dTitle;

        DealHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_deals, parent, false));

            dImage = itemView.findViewById(R.id.deal_image);
            dPrice = itemView.findViewById(R.id.deal_price);
            dDescription = itemView.findViewById(R.id.deal_description);
            dTitle = itemView.findViewById(R.id.deal_title);
        }

        private void bindDeal(TravelDeal deal) {

            dPrice.setText(deal.getPrice());
            dDescription.setText(deal.getDescription());
            dTitle.setText(deal.getTitle());
            showImage(deal.getImgUrl());

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Log.d("Clicked position" , String.valueOf(position));
            TravelDeal selected = mDeals.get(position);
            Intent intent = new Intent(view.getContext(), DealActivity.class);
            intent.putExtra("Deal", selected);
            view.getContext().startActivity(intent);
        }

        private void showImage(String url) {
            if (url != null && !url.isEmpty()) {
                Picasso.get()
                        .load(url)
                        .into(dImage);
            }
        }
    }
}
