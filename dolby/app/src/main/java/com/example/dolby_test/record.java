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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.OnClick;

public class record extends AppCompatActivity {
    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    private EditText editText;
    private ImageView micButton;
    public String finalPhrase;
    public Integer AudioRecordStatus = 0;
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }


    @OnClick(R.id.leave)
    public void onLeave()   {

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("safeWord", finalPhrase);
        editor.apply();

        Intent intent = new Intent(this, monitor.class);
        Bundle extras = new Bundle();
        String messageBundle = finalPhrase;
        extras.putString("safeWord", messageBundle);
        intent.putExtras(extras);
        startActivity(intent);
    }
    public void startNewActivity() {
        Intent intent = new Intent(this, monitor.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }
        editText = findViewById(R.id.text);
        micButton = findViewById(R.id.button);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        Button button = (Button) findViewById(R.id.leave);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewActivity();
                android.util.Log.e("myApp", "on click ran");
            }
        });

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
                        //editText.setText("");
                        //editText.setHint("Speech to Text Started Up");
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
                        micButton.setImageResource(R.drawable.safeword_mircophone_record);
                        ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                        editText.setText(data.get(0));
                        finalPhrase=data.get(0);
                        android.util.Log.e("Send Safe Word Phrase:", finalPhrase);

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
                if (AudioRecordStatus==1){
                    finalPhrase=editText.getText().toString();
                    speechRecognizer.stopListening();
                    AudioRecordStatus=0;
                }
                else if (AudioRecordStatus==0){
                    micButton.setImageResource(R.drawable.safeword_mircophone_record);

                    speechRecognizer.startListening(speechRecognizerIntent);
                    AudioRecordStatus=1;
                }
                return false;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }
}