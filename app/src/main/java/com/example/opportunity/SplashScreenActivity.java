package com.example.opportunity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView opportunityImageView;
    TextView appNameTextView;
    TextView textualLogoTextView;
    Animation translateBottom, translateTop;
    long time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        opportunityImageView = findViewById(R.id.opportunity_image_view);
        appNameTextView = findViewById(R.id.app_name_text_view);
        textualLogoTextView = findViewById(R.id.textual_logo_text_view);


        translateBottom = AnimationUtils.loadAnimation(this, R.anim.translate_bottom);
        translateTop = AnimationUtils.loadAnimation(this, R.anim.translate_top);
        opportunityImageView.setAnimation(translateTop);
        appNameTextView.setAnimation(translateBottom);
        textualLogoTextView.setAnimation(translateBottom);

        int SPLASH_SCREEN = 3500;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intentToMainActivity = new Intent(SplashScreenActivity.this, IntroActivity.class);
                startActivity(intentToMainActivity);
                finish();
            }
        }, SPLASH_SCREEN);
    }

    @Override
    public void onBackPressed() {
        if ((time + 2000) > System.currentTimeMillis()) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Press again", Toast.LENGTH_LONG).show();
        }
        time = System.currentTimeMillis();
    }
}