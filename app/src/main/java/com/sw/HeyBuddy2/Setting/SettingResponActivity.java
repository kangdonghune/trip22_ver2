package com.sw.HeyBuddy2.Setting;

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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sw.HeyBuddy2.Main.MainActivity;
import com.sw.HeyBuddy2.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import xyz.hasnat.sweettoast.SweetToast;

public class SettingResponActivity extends AppCompatActivity {

    private TextView userName;
    private EditText userStatus;
    private CheckBox english, korean, chinese, restaurant, culture, show, art, sights, shopping, walk;
    private Button updateAccountSettings;
    private Spinner location;
    String profile_download_url;
    private RadioGroup rg;
    private RadioButton r1,r2,r3;
    private ImageView ivBack;
    private OkHttpClient client=new OkHttpClient();



    private String currentUserID, name, profileback_download_url;
    private FirebaseAuth mAuth;
    // cloudfirestore로 변환중
    private FirebaseFirestore db;

    //이미지 관련 부분
    private static final String TAG = "ProfileFragment";
    int REQUEST_IMAGE_CODE=1001;
    int GALLERY_IMAGE_CODE=1002;
    private CircleImageView ivUser;
    private ImageView editPhotoIcon;
    private StorageReference mStorageRef;
    int REQUEST_EXTERNAL_STORAGE_PERMISSION=1002;

    String NLocation = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_respon);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        ivBack=findViewById(R.id.profile_ivUserBackground);

        if(ContextCompat.checkSelfPermission(SettingResponActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(SettingResponActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)){

            }else{
                ActivityCompat.requestPermissions(SettingResponActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_EXTERNAL_STORAGE_PERMISSION);
            }
        }else{

        }
        mStorageRef = FirebaseStorage.getInstance().getReference();

        rg=(RadioGroup)findViewById(R.id.language_radio);
        r1=(RadioButton)findViewById(R.id.language_english);
        r2=(RadioButton)findViewById(R.id.language_korean);
        r3=(RadioButton)findViewById(R.id.language_Chinese);

        updateAccountSettings = (Button) findViewById(R.id.update_settings_button);
        userName = (TextView) findViewById(R.id.userName);
        userStatus = (EditText) findViewById(R.id.set_profile_status);
        ivUser = (CircleImageView) findViewById(R.id.ivUser);
        editPhotoIcon = findViewById(R.id.editPhotoIcon);
        location=(Spinner)findViewById(R.id.spinner_city);

        english=(CheckBox)findViewById(R.id.english);
        korean=(CheckBox)findViewById(R.id.korean);
        chinese=(CheckBox)findViewById(R.id.chinese);

        restaurant=(CheckBox)findViewById(R.id.restaurant);
        culture =(CheckBox)findViewById(R.id.culture);
        show=(CheckBox)findViewById(R.id.show);
        art=(CheckBox)findViewById(R.id.art);
        sights=(CheckBox)findViewById(R.id.sights);
        shopping=(CheckBox)findViewById(R.id.shopping);
        walk=(CheckBox)findViewById(R.id.walk);


        updateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
                finish();

            }
        });
        //사진 관련 코드
        editPhotoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(in, REQUEST_IMAGE_CODE);
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(in, GALLERY_IMAGE_CODE);
            }
        });
        db.collection("Users").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> map = document.getData();
                        if(map.containsKey("name")){
                            name=map.get("name").toString();
                            userName.setText(name);}
                        if (map.containsKey("user_image")) {
                            final String userUri = map.get("user_image").toString();
                            Picasso.get().load(userUri)
                                    .networkPolicy(NetworkPolicy.OFFLINE) // for Offline
                                    .placeholder(R.drawable.default_profile_image)
                                    .error(R.drawable.default_profile_image)
                                    .resize(0,170)
                                    .into(ivUser, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Picasso.get().load(userUri)
                                                    .placeholder(R.drawable.default_profile_image)
                                                    .error(R.drawable.default_profile_image)
                                                    .resize(0,170)
                                                    .into(ivUser);

                                        }
                                    });
                        }
                        if (map.containsKey("user_back_image")) {
                            final String userbackUri = map.get("user_back_image").toString();
                            Picasso.get().load(userbackUri)
                                    .networkPolicy(NetworkPolicy.OFFLINE) // for offline
                                    .placeholder(R.drawable.profile_ivuserbackgroundimage)
                                    .error(R.drawable.profile_ivuserbackgroundimage)
                                    .resize(0,400)
                                    .into(ivBack, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Picasso.get().load(userbackUri)
                                                    .placeholder(R.drawable.profile_ivuserbackgroundimage)
                                                    .error(R.drawable.profile_ivuserbackgroundimage)
                                                    .resize(0,400)
                                                    .into(ivBack);
                                        }
                                    });
                        }
                        if(map.containsKey("newL"))
                        {
                            if(map.get("newL").toString().equals("English"))
                                r1.setChecked(true);
                            if(map.get("newL").toString().equals("Korean"))
                                r2.setChecked(true);
                            if(map.get("newL").toString().equals("Chinese"))
                                r3.setChecked(true);
                        }
                        if(map.containsKey("status")){
                            String retrieveUserStatus = map.get("status").toString();
                            userStatus.setText(retrieveUserStatus);
                        }
                        if(map.containsKey("newSubL")){

                            List<String> Language=new ArrayList<>();
                            List<String> check_key=(ArrayList<String>)map.get("newSubL");

                            for(int i=0; i<check_key.size();i++)
                            {
                                if(check_key.get(i).toString().equals("Korean"))
                                    korean.setChecked(true);
                                if(check_key.get(i).toString().equals("English"))
                                    english.setChecked(true);
                                if(check_key.get(i).toString().equals("Chinese"))
                                    chinese.setChecked(true);
                            }

                        }

                        if(map.containsKey("NLocation")) {
                            String locations = (String) map.get("NLocation");
                            String[] cityarray = getResources().getStringArray(R.array.city);

                            for (int i = 0; i < cityarray.length; i++) {
                                if (locations.equals(cityarray[i]))
                                    location.setSelection(i);
                            }
                        }


                        if(map.containsKey("newI")){
                            List<String> check_key=(ArrayList<String>)map.get("newI");
                            for(int i=0;i<check_key.size();i++){
                                if(check_key.get(i).toString().equals("Restaurant"))
                                    restaurant.setChecked(true);
                                if(check_key.get(i).toString().equals("Culture"))
                                    culture.setChecked(true);
                                if(check_key.get(i).toString().equals("Show"))
                                    show.setChecked(true);
                                if(check_key.get(i).toString().equals("Art"))
                                    art.setChecked(true);
                                if(check_key.get(i).toString().equals("Sights"))
                                    sights.setChecked(true);
                                if(check_key.get(i).toString().equals("Shopping"))
                                    shopping.setChecked(true);
                                if(check_key.get(i).toString().equals("Walk"))
                                    walk.setChecked(true);
                            }
                        }
                    }
                }
                else {
                    Toast.makeText(SettingResponActivity.this, "Please set & update profile...", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if(requestCode==REQUEST_IMAGE_CODE){
                final Uri image=data.getData();
                Picasso.get().load(image)
                        .placeholder(R.drawable.default_profile_image)
                        .error(R.drawable.default_profile_image)
                        .resize(0,170)
                        .into(ivUser);

                final StorageReference riversRef = mStorageRef.child("Users").child(currentUserID).child("profile.jpg");
                UploadTask uploadTask=riversRef.putFile(image);
                Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            SweetToast.error(SettingResponActivity.this, "Profile Photo Error: " + task.getException().getMessage());
                        }
                        profile_download_url=riversRef.getDownloadUrl().toString();
                        return riversRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            profile_download_url=task.getResult().toString();

                            HashMap<String, Object> update_user_data=new HashMap<>();
                            update_user_data.put("user_image",profile_download_url);

                            db.collection("Users").document(currentUserID).set(update_user_data, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    });


                        }
                    }
                });

            }
            if(requestCode==GALLERY_IMAGE_CODE && resultCode == SettingResponActivity.this.RESULT_OK){
                final Uri image=data.getData();
                Picasso.get().load(image)
                        .placeholder(R.drawable.profile_ivuserbackgroundimage)
                        .error(R.drawable.profile_ivuserbackgroundimage)
                        .resize(0,200)
                        .into(ivBack);

                final StorageReference storeRef = mStorageRef.child("Users").child(currentUserID).child("profile_back.jpg");
                UploadTask uploadTask=storeRef.putFile(image);
                Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            SweetToast.error(SettingResponActivity.this, "Profile Photo Error: " + task.getException().getMessage());
                        }
                        profileback_download_url=storeRef.getDownloadUrl().toString();
                        return storeRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            profileback_download_url=task.getResult().toString();

                            HashMap<String, Object> update_user_data=new HashMap<>();
                            update_user_data.put("user_back_image",profileback_download_url);

                            db.collection("Users").document(currentUserID).set(update_user_data, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    });


                        }
                    }
                });
            }
        }catch (Exception e){

        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private void UpdateSettings() {
        String setUserName = userName.getText().toString();
        String setStatus = userStatus.getText().toString();

        List<String> Language=new ArrayList<>();
        String newL="";
        String newSubL="";


        NLocation=location.getSelectedItem().toString();


        if(r1.isChecked())
            newL="English";
        if(r2.isChecked())
            newL="Korean";
        if(r3.isChecked())
            newL="Chinese";

        if(english.isChecked())
           Language.add("English");

        if(korean.isChecked())
            Language.add("Korean");

        if(chinese.isChecked())
            Language.add("Chinese");

        List<String> new_interests=new ArrayList<>();

        if(restaurant.isChecked())
            new_interests.add("Restaurant");
        if(culture.isChecked())
            new_interests.add("Culture");
        if(show.isChecked())
            new_interests.add("Show");
        if(art.isChecked())
            new_interests.add("Art");
        if(sights.isChecked())
            new_interests.add("Sights");
        if(shopping.isChecked())
            new_interests.add("Shopping");
        if(walk.isChecked())
            new_interests.add("Walk");


        if (TextUtils.isEmpty(setUserName)) {
            Toast.makeText(this, "Please write your user name first...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(setStatus)) {
            Toast.makeText(this, "Please write your status...", Toast.LENGTH_SHORT).show();
        }
        else {

            HashMap<String, Object> profileMap = new HashMap<>();

            profileMap.put("name", setUserName);
            profileMap.put("uid", currentUserID);
            profileMap.put("status", setStatus);
            profileMap.put("newL",newL);
            profileMap.put("newSubL",Language);
            profileMap.put("NLocation",NLocation);
            profileMap.put("newI",new_interests);

            db.collection("Users").document(currentUserID).set(profileMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                       /*Intent selectIntent = new Intent(SettingResponActivity.this, MainActivity.class);
                        selectIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(selectIntent);*/
                        finish();
                        // Toast.makeText(SettingsActivity.this, "Profile Update Successfully...", Toast.LENGTH_SHORT).show();
                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(SettingResponActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
}