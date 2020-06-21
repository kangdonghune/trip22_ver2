package com.sw.HeyBuddy2.FindBuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.sw.HeyBuddy2.R;

import xyz.hasnat.sweettoast.SweetToast;

public class Report extends AppCompatActivity {

    TextView report_you, report_buddy;
    EditText report_content;
    Button report_submit;
    String reportyou, reportbuddy;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        reportyou=getIntent().getExtras().get("you").toString();
        reportbuddy=getIntent().getExtras().get("another").toString();

        report_you.setText(reportyou);
        report_buddy.setText(reportbuddy);

        report_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetToast.info(getApplicationContext(),"submit your report?");
                finish();
            }
        });
    }
}