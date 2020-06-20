package com.sw.HeyBuddy2.LoginRegiser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.sw.HeyBuddy2.Main.MainActivity;
import com.sw.HeyBuddy2.Main.QMainActivity;
import com.sw.HeyBuddy2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.sw.HeyBuddy2.Tutorial;

import java.util.HashMap;

public class SelectActivity extends AppCompatActivity {
    private static final String TAG = "SelectActivity";
    Button questioner, respondent;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        questioner= (Button) findViewById(R.id.selection_questioner);
        respondent=(Button)findViewById(R.id.selection_respondent);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();





        questioner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserStatus();
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        HashMap<String, Object> updateUserMap = new HashMap<>();
                        updateUserMap.put("question",true);

                        db.collection("Users").document(currentUserId).set(updateUserMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful()){
                                    Log.d(TAG,"fail"+currentUserId);
                                }
                            }
                        });
                    }
                });
                Intent questioner_home= new Intent(SelectActivity.this, QMainActivity.class);
                startActivity(questioner_home);

            }
        });
        respondent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserStatus();
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        HashMap<String, Object> updateUserMap = new HashMap<>();
                        updateUserMap.put("question",false);

                        db.collection("Users").document(currentUserId).set(updateUserMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful()){
                                    Log.d(TAG,"fail"+currentUserId);
                                }
                            }
                        });
                    }
                });
                Intent respondent_home = new Intent(SelectActivity.this, MainActivity.class);
                startActivity(respondent_home);
            }
        });
    }
    private void updateUserStatus() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                String token = task.getResult().getToken();
                HashMap<String, Object> updateUserMap = new HashMap<>();
                updateUserMap.put("FCMToken", token);
                updateUserMap.put("date", FieldValue.serverTimestamp());
                updateUserMap.put("state", true);

                db.collection("Users").document(currentUserId).set(updateUserMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful()){
                            Log.d(TAG,"fail"+currentUserId);
                        }
                    }
                });
            }
        });
    }
}