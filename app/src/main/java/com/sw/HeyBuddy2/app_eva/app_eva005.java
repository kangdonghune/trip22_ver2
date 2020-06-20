package com.sw.HeyBuddy2.app_eva;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.google.firebase.firestore.FirebaseFirestore;
import com.sw.HeyBuddy2.R;

import java.util.HashMap;

import static com.sw.HeyBuddy2.app_eva.app_eva001.evainfo;

public class app_eva005 extends AppCompatActivity {

    private EditText ev;
    private Button btn_next, btn_skip;
    private RatingBar rating;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_eva005);

        ev = (EditText) findViewById(R.id.ev5);
        btn_next = (Button) findViewById(R.id.next5);
        rating = (RatingBar) findViewById(R.id.rating5);
        db = FirebaseFirestore.getInstance();

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evainfo.put("etc", ev.getText().toString());
                evainfo.put("rating", rating.getRating());
                db.collection("app_Evaluation").document().set(evainfo);
                evainfo.clear();
                finish();
            }
        });

    }
    @Override
    protected void onStop(){
        super.onStop();
        evainfo.clear();
    }
}