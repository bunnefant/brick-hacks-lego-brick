package com.example.dolby_test;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
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

import butterknife.ButterKnife;

public class monitor extends AppCompatActivity {
    protected List<View> views = new ArrayList<>();
    protected List<View> buttonsNotLoggedIn = new ArrayList<>();
    protected List<View> buttonsInConference = new ArrayList<>();
    protected List<View> buttonsNotInConference = new ArrayList<>();
    protected List<View> buttonsInOwnVideo = new ArrayList<>();
    protected List<View> buttonsNotInOwnVideo = new ArrayList<>();
    protected List<View> buttonsInOwnScreenShare = new ArrayList<>();
    protected List<View> buttonsNotInOwnScreenShare = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;
    //Speech to text
    private SpeechRecognizer speechRecognizer;
    public String safePhrase = "I am not in harms way";
    public static final Integer RecordAudioRequestCode = 1;
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }


    public void onCall(String conference_name) {
        ParamsHolder paramsHolder = new ParamsHolder();
        paramsHolder.setDolbyVoice(true);

        ConferenceCreateOptions conferenceCreateOptions = new ConferenceCreateOptions.Builder()
                .setConferenceAlias(conference_name)
                .setParamsHolder(paramsHolder)
                .build();

        VoxeetSDK.conference().create(conferenceCreateOptions)
                .then((ThenPromise<CreateConferenceResult, Conference>) res -> {
                    Conference conference = VoxeetSDK.conference().getConference(res.conferenceId);
                    return VoxeetSDK.conference().join(conference);
                })
                .then(conference -> {
                    Toast.makeText(monitor.this, "started...", Toast.LENGTH_SHORT).show();
                    updateViews();
                })
                .error((error_in) -> {
                    Toast.makeText(monitor.this, "Could not create conference", Toast.LENGTH_SHORT).show();
                });
//        List<ParticipantInfo> person = new ArrayList<>();
//        person.add(new ParticipantInfo("Helper", "", ""));
//        VoxeetSDK.notification().invite(VoxeetSDK.conference().getConference(), person);


        Intent intent = new Intent(this, call.class);
        startActivity(intent);
    }



    public void onLogout() {
        VoxeetSDK.session().close()
                .then((result, solver) -> {
                    Toast.makeText(monitor.this, "logout done", Toast.LENGTH_SHORT).show();
                    updateViews();
                }).error(error());
    }

    public void startNewActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onLeave() {
        VoxeetSDK.conference().leave()
                .then((result, solver) -> {
                    updateViews();
                    Toast.makeText(monitor.this, "left...", Toast.LENGTH_SHORT).show();
                }).error(error());
        onLogout();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*Bundle extras = getIntent().getExtras();
        dangerPhrase = extras.getString("safeWord");
        android.util.Log.w("myApp", "result is"+dangerPhrase);*/
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        final String dangerPhrase = sharedPref.getString("safeWord", "");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        ButterKnife.bind(this);

        //login

        //join call

        //Speech to Text

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizer.startListening(speechRecognizerIntent);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizer.setRecognitionListener(
                new RecognitionListener() {
                    @Override
                    public void onReadyForSpeech(Bundle bundle) {

                    }

                    @Override
                    public void onBeginningOfSpeech() {
                        //editText.setText("");
                        //editText.setHint("Speech to Text Started Up");
                        android.util.Log.w("myApp", "speech recording booted up");

                    }

                    @Override
                    public void onRmsChanged(float v) {

                    }

                    @Override
                    public void onBufferReceived(byte[] bytes) {

                    }

                    @Override
                    public void onEndOfSpeech() {
                        //editText.setHint("Speech ended");
                    }

                    @Override
                    public void onError(int i) {
                        //editText.setHint("Error Code: " + i);
                    }

                    @Override
                    public void onResults (Bundle bundle){
                        ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                        android.util.Log.w("myApp", "result is"+data.get(0));

                        if(data.get(0).toLowerCase().contains(dangerPhrase.toLowerCase())) {
                            //DANGER RUN
                            speechRecognizer.stopListening();
                            String name = "Vennela";
                            VoxeetSDK.initialize("grB4NiWlMEvzpaLbBKBmVw==", "ap6TnDQpnFUEPlIgrN3ir3hoL2NLrCLHLHd1s_YjYW0=");
                            String conference_name = "Safety-Hotline";
                            VoxeetSDK.session().open(new ParticipantInfo(name, "", ""))
                                    .then((result, solver) -> {
                                        Toast.makeText(monitor.this, "log in successful", Toast.LENGTH_SHORT).show();
                                        updateViews();
                                    })
                                    .error(error());
                            onCall(conference_name);


                        }
                        else if (data.get(0).contains(safePhrase)) {
                            //SAFE RUN
                            speechRecognizer.stopListening();
                            startNewActivity();
                        }
                    }

                    @Override
                    public void onPartialResults(Bundle bundle) {
                        ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                        android.util.Log.w("myapp", dangerPhrase);
                        if(data.get(0).contains(dangerPhrase)) {
                            //DANGER RUN
                            speechRecognizer.stopListening();
                            String name = "Sohil";
                            VoxeetSDK.initialize("grB4NiWlMEvzpaLbBKBmVw==", "ap6TnDQpnFUEPlIgrN3ir3hoL2NLrCLHLHd1s_YjYW0=");
                            String conference_name = "Safety-Hotline";
                            VoxeetSDK.session().open(new ParticipantInfo(name, "", ""))
                                    .then((result, solver) -> {
                                        Toast.makeText(monitor.this, "log in successful", Toast.LENGTH_SHORT).show();
                                        updateViews();
                                    })
                                    .error(error());
                            onCall(conference_name);


                        }
                        else if (data.get(0).toLowerCase().contains(safePhrase.toLowerCase())) {
                            //SAFE RUN
                            speechRecognizer.stopListening();
                            startNewActivity();
                        }
                    }

                    @Override
                    public void onEvent(int i, Bundle bundle) {

                    }
                });
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        Context context = getApplicationContext();
//        CharSequence text = "Hello toast!";
//        int duration = Toast.LENGTH_SHORT;
//        Toast toast = Toast.makeText(context, text, duration);
//        toast.show();



        //all the logic of the onCreate will be put after this comment

        //we now initialize the sdk


        //adding the user_name, login and logout views related to the open/close and conference flow
//        add(views, R.id.login);
//        add(views, R.id.logout);
//
//        add(buttonsNotLoggedIn, R.id.login);
//        add(buttonsNotLoggedIn, R.id.user_name);
//
//        add(buttonsInConference, R.id.logout);
//
//        add(buttonsNotInConference, R.id.logout);

        // Set a random user name
//        String[] avengersNames = {
//                "Thor",
//                "Cap",
//                "Tony Stark",
//                "Black Panther",
//                "Black Widow",
//                "Hulk",
//                "Spider-Man",
//        };
//        Random r = new Random();
//        user_name.setText(avengersNames[r.nextInt(avengersNames.length)]);
//
//        // Add the join button and enable it only when not in a conference
//        add(views, R.id.join);
//        add(buttonsNotInConference, R.id.join);
//
//        // Set a default conference name
//        conference_name.setText("Avengers meeting");
//
//        // Add the leave button and enable it only while in a conference
//        add(views, R.id.leave);
//        add(buttonsInConference, R.id.leave);


    }

    @Override
    protected void onResume() {
        super.onResume();

        //here will be put the permission check

        //we update the various views to enable or disable the ones we want to
        updateViews();

        //register the current activity in the SDK
        VoxeetSDK.instance().register(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA}, 0x20);
        }

    }

    @Override
    protected void onPause() {
        //register the current activity in the SDK
        VoxeetSDK.instance().unregister(this);

        super.onPause();
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
            Toast.makeText(monitor.this, "ERROR...", Toast.LENGTH_SHORT).show();
            error.printStackTrace();
            updateViews();
        };
    }

    private void setEnabled(List<View> views, boolean enabled) {
        for (View view : views) view.setEnabled(enabled);
    }

    private monitor add(List<View> list, int id) {
        list.add(findViewById(id));
        return this;
    }


}