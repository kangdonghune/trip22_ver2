package com.sw.HeyBuddy2.app_eva;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sw.HeyBuddy2.R;

import java.util.HashMap;

import static com.sw.HeyBuddy2.app_eva.app_eva001.evainfo;

public class app_eva002 extends AppCompatActivity {

    private EditText ev;
    private Button btn_next, btn_skip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_eva002);

        ev = (EditText) findViewById(R.id.ev2);
        btn_next = (Button) findViewById(R.id.next2);
        btn_skip = (Button) findViewById(R.id.skip2);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evainfo.put("pay", ev.getText().toString());
                Intent intent = new Intent(app_eva002.this, app_eva003.class);
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