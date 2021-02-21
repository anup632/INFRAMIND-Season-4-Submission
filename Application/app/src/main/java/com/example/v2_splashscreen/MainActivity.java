package com.example.v2_splashscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    //V15 Inframind app
    private static int SPLASH_TIME_OUT = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();//Remove action bar
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent LoginPage = new Intent(MainActivity.this, LoginPage.class);
                startActivity(LoginPage);
                finish();

            }
        },SPLASH_TIME_OUT);
    }
}