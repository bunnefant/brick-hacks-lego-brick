package com.example.dolby_test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.dolby_test.Adapter.Adapter_walkthrough;

import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;

public class OnboardingActivity extends AppCompatActivity {

    public ViewPager viewpager;
    Adapter_walkthrough adapter_walkthrough;

   @OnClick(R.id.next)
    public void onLeave()   {
        Intent intent = new Intent(this, record.class);
        startActivity(intent);
       android.util.Log.e("myApp", "on leave ran");

   }
    public void startNewActivity() {
        Intent intent = new Intent(this, record.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        Button button = (Button) findViewById(R.id.next);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity();
                android.util.Log.e("myApp", "on click ran");
            }
        });

        viewpager = findViewById(R.id.viewpager);

        CircleIndicator indicator = findViewById(R.id.indicator);

        adapter_walkthrough = new Adapter_walkthrough(getSupportFragmentManager());

        viewpager.setAdapter(adapter_walkthrough);

        indicator.setViewPager(viewpager);

        adapter_walkthrough.registerDataSetObserver(indicator.getDataSetObserver());
    }
}