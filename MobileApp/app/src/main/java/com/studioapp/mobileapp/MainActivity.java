package com.studioapp.mobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        progressBar=findViewById(R.id.id_progressBar);

        progressBar.setMax(100);
        progressBar.setScaleY(3f);
        progressAnimation();

    }
    public void progressAnimation() {
        ProgressBarAnimation amin = new ProgressBarAnimation(this, progressBar, 0f, 100f,this);
        amin.setDuration(3000);
        progressBar.setAnimation(amin);
    }
}




