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

public class app__evaluation extends AppCompatActivity {

    private EditText ev1, ev2, ev3, ev4, ev5;
    private RatingBar rating;
    private HashMap evaInfo;
    private Button btn_submit;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app__evaluation);

        ev1 = (EditText) findViewById(R.id.ev1);
        ev2 = (EditText) findViewById(R.id.ev2);
        ev3 = (EditText) findViewById(R.id.ev3);
        ev4 = (EditText) findViewById(R.id.ev4);
        ev5 = (EditText) findViewById(R.id.ev5);

        evaInfo = new HashMap();

        db = FirebaseFirestore.getInstance();

        rating = (RatingBar) findViewById(R.id.rating5);

        btn_submit = (Button) findViewById(R.id.btn_submit);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evaInfo.put("helpful", ev1.getText().toString());
                evaInfo.put("pay", ev2.getText().toString());
                evaInfo.put("funtional", ev3.getText().toString());
                evaInfo.put("operater", ev4.getText().toString());
                evaInfo.put("etc", ev5.getText().toString());
                evaInfo.put("rating", rating.getRating());
                db.collection("app_Evaluation").document().set(evaInfo);

                finish();
            }
        });
    }
}