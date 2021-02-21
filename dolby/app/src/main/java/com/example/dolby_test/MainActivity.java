package com.example.dolby_test;


import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @Bind(R.id.user_name)
    EditText user_name;



    @OnClick(R.id.startButton)
    public void startAppTest()  {
        Intent intent = new Intent(this, monitor.class);
        intent.putExtra("Name", user_name.getText().toString());
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        user_name.setHint("Name");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }


}