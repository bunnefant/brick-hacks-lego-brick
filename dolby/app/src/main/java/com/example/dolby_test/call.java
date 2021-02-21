package com.example.dolby_test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.voxeet.VoxeetSDK;
import com.voxeet.promise.solve.ErrorPromise;
import com.voxeet.sdk.services.conference.information.ConferenceInformation;

import butterknife.OnClick;

public class call extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
    }

    public void onLogout() {
        VoxeetSDK.session().close()
                .then((result, solver) -> {
                    Toast.makeText(call.this, "logout done", Toast.LENGTH_SHORT).show();
                }).error(error());
    }



    @OnClick(R.id.leave)
    public void onLeave() {
        VoxeetSDK.conference().leave()
                .then((result, solver) -> {
                    Toast.makeText(call.this, "left...", Toast.LENGTH_SHORT).show();
                }).error(error());
        onLogout();
    }

    private ErrorPromise error() {
        return error -> {
            Toast.makeText(call.this, "ERROR...", Toast.LENGTH_SHORT).show();
            error.printStackTrace();
        };
    }

}