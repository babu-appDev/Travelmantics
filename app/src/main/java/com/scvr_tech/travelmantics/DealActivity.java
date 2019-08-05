package com.scvr_tech.travelmantics;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class DealActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    private EditText mTitle, mDescription, mPrice;
    private ImageView mImageView;
    private Button btnInsert;
    private TravelDeal mDeal;

    private static final int PICTURE_RESULT = 42; // the answer to everything

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FirebaseDatabase firebaseDatabase = FirebaseUtil.sFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.sDatabaseReference;

        mTitle = findViewById(R.id.txtTitle);
        mDescription = findViewById(R.id.txtDescription);
        mPrice = findViewById(R.id.txtPrice);

        Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");
        if (deal==null) {
            deal= new TravelDeal();
        }
        this.mDeal = deal;

        mTitle.setText(mDeal.getTitle());
        mPrice.setText(mDeal.getPrice());
        mDescription.setText(mDeal.getDescription());

        btnInsert = findViewById(R.id.upload_image);
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickImage = new Intent();
                pickImage.setAction(Intent.ACTION_GET_CONTENT);
                pickImage.setType("image/*");
                pickImage.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(pickImage, "Insert Image"), PICTURE_RESULT);
            }
        });

        mImageView = findViewById(R.id.imageView);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showImage(mDeal.getImgUrl());
            }
        }, 500);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);

        if (FirebaseUtil.isAdmin) {
            menu.findItem(R.id.delete_menu).setVisible(true);
            menu.findItem(R.id.save_menu).setVisible(true);
            enableEditText(true);
            btnInsert.setEnabled(true);
        } else {
            menu.findItem(R.id.delete_menu).setVisible(false);
            menu.findItem(R.id.save_menu).setVisible(false);
            enableEditText(false);
            btnInsert.setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_menu:
                deleteDeal();
                Toast.makeText(this, "Deal Deleted.", Toast.LENGTH_SHORT).show();
                backToList();
                return true;
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(DealActivity.this, "Deal Saved!", Toast.LENGTH_SHORT).show();
                clean();
                backToList();
                return true;
            case android.R.id.home:
                backToList();
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
        mDeal.setTitle(mTitle.getText().toString());
        mDeal.setDescription(mDescription.getText().toString());
        mDeal.setPrice(mPrice.getText().toString());

        if (mDeal.getId()==null) {
            mDatabaseReference.push().setValue(mDeal);
        }else {
            mDatabaseReference.child(mDeal.getId()).setValue(mDeal);
        }

    }

    private void deleteDeal() {

        if (mDeal == null) {
            Toast.makeText(this, "Please save this deal before deleting", Toast.LENGTH_SHORT).show();
            return;
        }

        mDatabaseReference.child(mDeal.getId()).removeValue();
        if (mDeal.getImageName() != null && !mDeal.getImageName().isEmpty()) {
            StorageReference picRef = FirebaseUtil.sStorage.getReference().child(mDeal.getImageName());
            picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Delete Image", "Image Successfully Deleted");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Delete Image", e.getMessage());
                }
            });
        }
    }

    private void backToList(){
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
        finish();
    }

    private void enableEditText(boolean isEnabled) {
        mTitle.setEnabled(isEnabled);
        mDescription.setEnabled(isEnabled);
        mPrice.setEnabled(isEnabled);
    }

    private void showImage(String url) {
        if (url != null && !url.isEmpty()) {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;

            Picasso.get()
                    .load(url)
                    .resize(width, width*2/3)
                    .centerCrop()
                    .into(mImageView);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Log.d("DealActivity", uri.toString());
            StorageReference ref = FirebaseUtil.sReference.child(uri.getLastPathSegment());
            ref.putFile(uri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            String pictureName = taskSnapshot.getStorage().getPath();
                            mDeal.setImgUrl(url);
                            mDeal.setImageName(pictureName);
                            showImage(url);
                        }
                    });
                }
            });

        }
    }
}
