package com.sw.HeyBuddy2.app_eva;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sw.HeyBuddy2.R;

import java.util.HashMap;

public class app_eva001 extends AppCompatActivity {

    public static HashMap evainfo;
    private EditText ev;
    private Button btn_next, btn_skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_eva001);

        evainfo = new HashMap();
        ev = (EditText) findViewById(R.id.ev1);
        btn_next = (Button) findViewById(R.id.next1);
        btn_skip = (Button) findViewById(R.id.skip1);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evainfo.put("helpful", ev.getText().toString());
                Intent intent = new Intent(app_eva001.this, app_eva002.class);
                startActivity(intent);
                finish();
            }
        });
        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evainfo.clear();
                finish();
            }
        });

    }
}