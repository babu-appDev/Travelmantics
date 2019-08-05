package com.scvr_tech.travelmantics;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DealActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    private EditText mTitle, mDescription, mPrice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUtil.openFirebaseRef("traveldeals");
        FirebaseDatabase firebaseDatabase = FirebaseUtil.sFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.sDatabaseReference;

        mTitle = findViewById(R.id.txtTitle);
        mDescription = findViewById(R.id.txtDescription);
        mPrice = findViewById(R.id.txtPrice);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(this, "Deal saved.", Toast.LENGTH_SHORT).show();
                clean();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clean() {
        mTitle.setText("");
        mPrice.setText("");
        mDescription.setText("");

        mTitle.requestFocus();
    }

    private void saveDeal() {
        String title = mTitle.getText().toString();
        String description = mDescription.getText().toString();
        String price = mPrice.getText().toString();

        TravelDeal travelDeal = new TravelDeal(title, description, price, "");
        mDatabaseReference.push().setValue(travelDeal);

    }
}
