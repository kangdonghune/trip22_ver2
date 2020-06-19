package com.sw.HeyBuddy2.Feed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.sw.HeyBuddy2.Main.MainActivity;
import com.sw.HeyBuddy2.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import xyz.hasnat.sweettoast.SweetToast;

public class FeedWriteActivity extends AppCompatActivity {
    private static final String TAG = "FeedWriteActivity";
    private ImageView imageview;
    Button btn_ok;
    EditText text;
    private CheckBox restaurant, culture, show, art, sights, shopping, walk;
    private Spinner sp1;


    FirebaseFirestore db;
    private String currentUserID;
    private String documentId;
    private FirebaseAuth mAuth;


    StorageReference storageRef;
    int REQUEST_EXTERNAL_STORAGE_PERMISSION=1002;
    int REQUEST_IMAGE_CODE=1001;

    String feed_uri;
    String feed_desc;
    String NLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_write);

        sp1=(Spinner)findViewById(R.id.spinner_city);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        storageRef = FirebaseStorage.getInstance().getReference();
        documentId=db.collection("Feeds").document().getId();

        restaurant=(CheckBox)findViewById(R.id.restaurant);
        culture =(CheckBox)findViewById(R.id.culture);
        show=(CheckBox)findViewById(R.id.show);
        art=(CheckBox)findViewById(R.id.art);
        sights=(CheckBox)findViewById(R.id.sights);
        shopping=(CheckBox)findViewById(R.id.shopping);
        walk=(CheckBox)findViewById(R.id.walk);

        imageview = (ImageView) findViewById(R.id.image);
        btn_ok= findViewById(R.id.btn_ok);

        text = (EditText) findViewById(R.id.feed_text);
        if(ContextCompat.checkSelfPermission(FeedWriteActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(FeedWriteActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)){

            }else{
                ActivityCompat.requestPermissions(FeedWriteActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_EXTERNAL_STORAGE_PERMISSION);
            }
        }else{

        }

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(in, REQUEST_IMAGE_CODE);
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writefeed();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_IMAGE_CODE && resultCode==RESULT_OK){
            final Uri image=data.getData();
            Picasso.get().load(image)
                    .placeholder(R.drawable.default_profile_image)
                    .error(R.drawable.default_profile_image)
                    .resize(0,400)
                    .into(imageview);

            final StorageReference riversRef = storageRef.child("Feeds").child(currentUserID).child(documentId).child("feed.jpg");
            UploadTask uploadTask=riversRef.putFile(image);
            Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        SweetToast.error(FeedWriteActivity.this, "Feed Photo Error: " + task.getException().getMessage());
                    }
                    feed_uri=riversRef.getDownloadUrl().toString();
                    return riversRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        feed_uri=task.getResult().toString();

                        HashMap<String, Object> update_feed_data=new HashMap<>();
                        update_feed_data.put("feed_uri",feed_uri);

                        db.collection("Feeds").document(documentId).set(update_feed_data, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                });
                    }
                }
            });

        }
    }
    private void writefeed() {
        final HashMap<String,Boolean> feed_keyword= new HashMap<>();

        if(restaurant.isChecked())
            feed_keyword.put(restaurant.getText().toString(),true);
        if(culture.isChecked())
            feed_keyword.put(culture.getText().toString(),true);
        if(show.isChecked())
            feed_keyword.put(show.getText().toString(),true);
        if(art.isChecked())
            feed_keyword.put(art.getText().toString(),true);
        if(sights.isChecked())
            feed_keyword.put(sights.getText().toString(),true);
        if(shopping.isChecked())
            feed_keyword.put(shopping.getText().toString(),true);
        if(walk.isChecked())
            feed_keyword.put(walk.getText().toString(),true);

        feed_desc=text.getText().toString();
        NLocation=sp1.getSelectedItem().toString();
        final Map<String, Object> feed = new HashMap<>();

        feed.put("feed_desc",feed_desc);
        feed.put("location",NLocation);
        feed.put("feed_time", FieldValue.serverTimestamp());
        feed.put("uid", currentUserID);
        feed.put("feed_area",feed_keyword);
        feed.put("like_number",0);
        Log.e("버그발생", feed.get("uid").toString());

        db.collection("Feeds").document(documentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().contains("feed_uri")){
                        task.getResult().getReference().set(feed,SetOptions.merge())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(FeedWriteActivity.this, "피드 등록이 완료되었습니다.",Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                });
                    }
                    else {
                        SweetToast.error(FeedWriteActivity.this, "Image is required");
                    }
                }
            }
        });
    }

}