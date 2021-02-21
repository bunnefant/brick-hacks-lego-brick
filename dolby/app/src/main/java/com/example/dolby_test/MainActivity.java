package com.example.dolby_test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.voxeet.VoxeetSDK;
import com.voxeet.promise.solve.ErrorPromise;
import com.voxeet.promise.solve.ThenPromise;
import com.voxeet.sdk.json.ParticipantInfo;
import com.voxeet.sdk.json.internal.ParamsHolder;
import com.voxeet.sdk.models.Conference;
import com.voxeet.sdk.models.v1.CreateConferenceResult;
import com.voxeet.sdk.services.builders.ConferenceCreateOptions;
import com.voxeet.sdk.services.conference.information.ConferenceInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;


import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    private EditText editText;
    private ImageView micButton;
    protected List<View> views = new ArrayList<>();
    protected List<View> buttonsNotLoggedIn = new ArrayList<>();
    protected List<View> buttonsInConference = new ArrayList<>();
    protected List<View> buttonsNotInConference = new ArrayList<>();
    protected List<View> buttonsInOwnVideo = new ArrayList<>();
    protected List<View> buttonsNotInOwnVideo = new ArrayList<>();
    protected List<View> buttonsInOwnScreenShare = new ArrayList<>();
    protected List<View> buttonsNotInOwnScreenShare = new ArrayList<>();




    @Bind(R.id.user_name)
    EditText user_name;



    @OnClick(R.id.startButton)
    public void startAppTest()  {
        Intent intent = new Intent(this, monitor.class);
        intent.putExtra("Name", user_name.getText().toString());
        startActivity(intent);
    }
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        user_name.setHint("Name");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //all the logic of the onCreate will be put after this comment
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }
        editText = findViewById(R.id.text);
        micButton = findViewById(R.id.button);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);


        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizer.setRecognitionListener(
                    new RecognitionListener() {
                    @Override
                    public void onReadyForSpeech(Bundle bundle) {

                    }

                    @Override
                    public void onBeginningOfSpeech() {
                        editText.setText("");
                        editText.setHint("Speech to Text Started Up");
                    }

                    @Override
                    public void onRmsChanged(float v) {

                    }

                    @Override
                    public void onBufferReceived(byte[] bytes) {

                    }

                    @Override
                    public void onEndOfSpeech() {
                        editText.setHint("Speech ended");
                    }

                    @Override
                    public void onError(int i) {
                        editText.setHint("Error Code: " + i);
                    }

                    @Override
                    public void onResults (Bundle bundle){
                        micButton.setImageResource(R.drawable.biolabselfie);
                        ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                        editText.setText(data.get(0));
                    }

                    @Override
                    public void onPartialResults(Bundle bundle) {
                    }

                    @Override
                    public void onEvent(int i, Bundle bundle) {

                    }
                });
        micButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    speechRecognizer.stopListening();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    micButton.setImageResource(R.drawable.biolabselfie);
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                return false;
            }
        });


        //we now initialize the sdk
        VoxeetSDK.initialize("grB4NiWlMEvzpaLbBKBmVw==", "ap6TnDQpnFUEPlIgrN3ir3hoL2NLrCLHLHd1s_YjYW0=");

        //adding the user_name, login and logout views related to the open/close and conference flow
        add(views, R.id.login);
        add(views, R.id.logout);

        add(buttonsNotLoggedIn, R.id.login);
        add(buttonsNotLoggedIn, R.id.user_name);

        add(buttonsInConference, R.id.logout);

        add(buttonsNotInConference, R.id.logout);

        // Set a random user name
        String[] avengersNames = {
                "Thor",
                "Cap",
                "Tony Stark",
                "Black Panther",
                "Black Widow",
                "Hulk",
                "Spider-Man",
        };
        Random r = new Random();
        user_name.setText(avengersNames[r.nextInt(avengersNames.length)]);

        // Add the join button and enable it only when not in a conference
        add(views, R.id.join);
        add(buttonsNotInConference, R.id.join);

        // Set a default conference name
        conference_name.setText("Avengers meeting");



    }


    private void updateViews() {
        //this method will be updated step by step

        //disable every view
        setEnabled(views, false);

        //if the user is not connected, we will only enabled the not logged in buttons
        if (!VoxeetSDK.session().isSocketOpen()) {
            setEnabled(buttonsNotLoggedIn, true);
            return;
        }

        ConferenceInformation current = VoxeetSDK.conference().getCurrentConference();
        //we can now add the logic to manage our basic state
        if (null != current && VoxeetSDK.conference().isLive()) {
            setEnabled(buttonsInConference, true);
        } else {
            setEnabled(buttonsNotInConference, true);
        }

    }

    private ErrorPromise error() {
        return error -> {
            Toast.makeText(MainActivity.this, "ERROR...", Toast.LENGTH_SHORT).show();
            error.printStackTrace();
            updateViews();
        };
    }

    private void setEnabled(List<View> views, boolean enabled) {
        for (View view : views) view.setEnabled(enabled);
    }

    private MainActivity add(List<View> list, int id) {
        list.add(findViewById(id));
        return this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }

}