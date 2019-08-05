package com.scvr_tech.travelmantics;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FirebaseUtil {

    public static FirebaseDatabase sFirebaseDatabase;
    public static DatabaseReference sDatabaseReference;
    private static FirebaseUtil sFirebaseUtil;
    public static ArrayList<TravelDeal> sDeals;

    private FirebaseUtil() {
        // Required empty constructor
    }

    public static void openFirebaseRef(String ref) {
        if (sFirebaseUtil == null) {
            sFirebaseUtil = new FirebaseUtil();
            sFirebaseDatabase = FirebaseDatabase.getInstance();
            sDeals = new ArrayList<>();
        }

        sDatabaseReference = sFirebaseDatabase.getReference().child(ref);
    }
}
