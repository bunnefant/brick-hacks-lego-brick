package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 4000;

    // Variables
    Animation logoAnim;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // Animation
        logoAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        // Hooks
        logo = findViewById(R.id.logo);

        // Set Animation to Image
        logo.setAnimation(logoAnim);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, OnboardingActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_SCREEN);

    }
}