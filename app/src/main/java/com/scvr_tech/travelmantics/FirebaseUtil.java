package com.scvr_tech.travelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {

    public static FirebaseDatabase sFirebaseDatabase;
    public static DatabaseReference sDatabaseReference;
    private static FirebaseUtil sFirebaseUtil;
    public static FirebaseAuth sFirebaseAuth;
    public static FirebaseStorage sStorage;
    public static StorageReference sReference;
    public static FirebaseAuth.AuthStateListener sAuthStateListener;
    public static ArrayList<TravelDeal> sDeals;
    private static ListActivity sCaller;
    public static boolean isAdmin;

    private static final int RC_SIGN_IN = 123;
    private FirebaseUtil() {
        // Required empty constructor
    }

    public static void openFirebaseRef(String ref, final ListActivity callerActivity) {
        if (sFirebaseUtil == null) {
            sFirebaseUtil = new FirebaseUtil();
            // Create and launch sign-in intent
            sCaller = callerActivity;
            sFirebaseDatabase = FirebaseDatabase.getInstance();
            sFirebaseAuth = FirebaseAuth.getInstance();
            sAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null) {
                        FirebaseUtil.signIn();
                    }
                    else {
                        String userId = sFirebaseAuth.getUid();
                        checkAdmin(userId);
                    }
                }
            };
            connectStorage();
        }

        sDeals = new ArrayList<>();
        sDatabaseReference = sFirebaseDatabase.getReference().child(ref);
    }

    private static void checkAdmin(String userId) {
        FirebaseUtil.isAdmin = false;
        DatabaseReference ref = sFirebaseDatabase.getReference().child("administrators")
                .child(userId);

        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUtil.isAdmin = true;
                sCaller.showMenu();
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

        ref.addChildEventListener(listener);
    }

    private static void signIn() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        sCaller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public static void attachListener(){
        sFirebaseAuth.addAuthStateListener(sAuthStateListener);
    }

    public static void detachListener() {
        sFirebaseAuth.removeAuthStateListener(sAuthStateListener);
    }

    public static void connectStorage() {
        sStorage = FirebaseStorage.getInstance();
        sReference = sStorage.getReference().child("deals_pictures");
    }
}
