package com.sw.HeyBuddy2.Feed;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.sw.HeyBuddy2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class DeleteFeedActivity extends Activity {

    FirebaseFirestore db;
    private static final String TAG = "DeleteFeedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_delete_feed);

        db = FirebaseFirestore.getInstance();
    }
    public void mDelete(View v){
        final Intent intent=getIntent();
        db.collection("Feeds").document(intent.getExtras().get("id").toString()).delete();
        db.collection("Feeds").document(intent.getExtras().get("id").toString())
                .collection("LikeMember").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().size()>0){
                        for(int i=0; i<task.getResult().size(); i++){
                            task.getResult().getDocuments().get(i).getReference().delete();
                        }
                    }
                }
            }
        });
        db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if (task.getResult().size()>0){
                        for (int i=0; i<task.getResult().getDocuments().size(); i++){
                            task.getResult().getDocuments().get(i).getReference().collection("LikeFeed")
                                    .whereEqualTo("doc_id",intent.getExtras().get("id").toString()).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                if(task.getResult().size()>0){
                                                    task.getResult().getDocuments().get(0).getReference().delete();
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                }
            }
        });
        finish();
    }

    public void mCancel(View v){
        finish();
    }
}