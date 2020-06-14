package com.example.lastplease.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.lastplease.LoginRegiser.LoginActivity;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);


    }
}
