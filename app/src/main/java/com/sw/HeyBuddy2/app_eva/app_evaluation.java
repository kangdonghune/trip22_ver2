package com.sw.HeyBuddy2.app_eva;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.google.firebase.firestore.FirebaseFirestore;
import com.sw.HeyBuddy2.R;

import java.util.HashMap;

public class app_evaluation extends AppCompatActivity {

    private EditText ev1, ev2, ev3, ev4, ev5;
    private RatingBar rating;
    private Button btn_submit;
    private FirebaseFirestore db;
    HashMap app_evaInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_evaluation);

        ev1 = findViewById(R.id.ev1);
        ev2 = findViewById(R.id.ev2);
        ev3 = findViewById(R.id.ev3);
        ev4 = findViewById(R.id.ev4);
        ev5 = findViewById(R.id.ev5);
        rating = findViewById(R.id.rating5);
        btn_submit = findViewById(R.id.btn_submit);
        app_evaInfo = new HashMap();
        db = FirebaseFirestore.getInstance();


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                app_evaInfo.put("helpful", ev1.getText().toString());
                app_evaInfo.put("pay", ev2.getText().toString());
                app_evaInfo.put("funtional", ev3.getText().toString());
                app_evaInfo.put("operater", ev4.getText().toString());
                app_evaInfo.put("etc", ev5.getText().toString());
                app_evaInfo.put("rating", rating.getRating());
                db.collection("app_Evaluation").document().set(app_evaInfo);
                finish();
            }
        });
    }
}