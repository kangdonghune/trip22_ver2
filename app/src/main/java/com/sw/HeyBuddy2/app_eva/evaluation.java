package com.sw.HeyBuddy2.app_eva;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sw.HeyBuddy2.R;

import java.util.HashMap;

public class evaluation extends AppCompatActivity {

    private Button ev1, ev2, ev3, ev4, ev5;
    private RatingBar rating;
    HashMap evaInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        ev1 = (Button) findViewById(R.id.ev1);

    }
}