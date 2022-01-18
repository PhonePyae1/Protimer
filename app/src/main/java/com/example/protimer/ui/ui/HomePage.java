package com.example.protimer.ui.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.protimer.R;
import com.example.protimer.ui.ui.SignIn.Login;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Arrays;

public class HomePage extends AppCompatActivity implements Timer.OnTimerListener {
    int count = 0;
    final int RECORD_AUDIO = 0;
    ImageButton btnStart;
    ImageButton btnProfile;
    TextView txtTimer;
    Spinner spinDuration;
    int clipDuration = 0;
    LongOperation recordAudioSync = null;
    Timer timer;
    String[] durationItems = {"5","10", "15", "20"};
    TextView countGunShots;
    View bottomSheetBehavior;
    String[] str;
    TextView timerTime;
    TextView select;
    ImageButton resetBtn;
    Boolean check1;
    Button playBtn;
    TextView setTime;
    private Handler mHandler = new Handler();
    ImageButton stopBtn;
    Boolean checkForStop;
    LinearLayout logOut;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    LinearLayout tips;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        initializeHandles();
        //ArrayAdapter<String> spinDurationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,durationItems);
        //spinDurationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spinDuration.setAdapter(spinDurationAdapter);
        timerTime = findViewById(R.id.textView2);
        select = findViewById(R.id.textView);
        checkForStop = true;

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences = HomePage.this.getSharedPreferences("checkbox", AppCompatActivity.MODE_PRIVATE);
                editor = (SharedPreferences.Editor) preferences.edit();
                editor.putString("remember","false");
                editor.apply();

                FirebaseAuth.getInstance().signOut();
                Toast.makeText(HomePage.this, "Logged Out", Toast.LENGTH_SHORT).show();

                Intent switchActivityIntent = new Intent(HomePage.this, Login.class);
                startActivity(switchActivityIntent);
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clipDuration = Integer.parseInt(spinDuration.getSelectedItem().toString());
                if (ActivityCompat.checkSelfPermission(HomePage.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(HomePage.this,new String[]{Manifest.permission.RECORD_AUDIO},RECORD_AUDIO);
                }
                else{
                    btnStart.setBackground(ContextCompat.getDrawable(HomePage.this, R.drawable.ic_baseline_mic_24));
                    playBtn.setBackground(ContextCompat.getDrawable(HomePage.this, R.drawable.button1));
                    //mHandler.postDelayed((Runnable) HomePage.this,3000);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //final MediaPlayer mediaPlayer = MediaPlayer.create(HomePage.this, R.raw.sound);
                            //mediaPlayer.start();
                            recordAudioSync = new LongOperation();
                            recordAudioSync.execute("");
                        }
                    }, 3000);

                }
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent switchActivityIntent = new Intent(HomePage.this, Profile.class);
                startActivity(switchActivityIntent);
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check1 == false) {
                    txtTimer.setText("00:00:00");
                }
                check1 = true;
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForStop = false;

            }
        });

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.homePage);
        final View bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet111);
        final BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setPeekHeight(0);
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        ImageButton bottomNav = findViewById(R.id.bottomNav);
        View bottom_sheetBG  = findViewById(R.id.bottom_sheetBG);
        ImageView cancel_btn = findViewById(R.id.cancel_btn);

        bottomNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottom_sheetBG.setVisibility(View.VISIBLE);
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                bottom_sheetBG.setVisibility(View.GONE);
            }
        });

        tips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                bottom_sheetBG.setVisibility(View.GONE);
            }
        });

    }

    public void openDialog() {
        DialogTips dialogTips = new DialogTips();
        dialogTips.show(getSupportFragmentManager(),"Tips");
    }

    @Override
    public void onBackPressed() {
        //Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT).show();
    }


    private void initializeHandles() {
        btnStart = findViewById(R.id.start);
        //spinDuration = findViewById(R.id.duration);
        btnProfile = findViewById(R.id.profileButton);
        txtTimer = findViewById(R.id.textView2);
        resetBtn = findViewById(R.id.resetBtn);
        playBtn = findViewById(R.id.playbutton);
        setTime = findViewById(R.id.setTime);
        check1 = false;
        str = new String[10];
        stopBtn = findViewById(R.id.stopBtn);
        logOut = findViewById(R.id.logOut);
        tips = findViewById(R.id.tips);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(recordAudioSync != null && recordAudioSync.getStatus() != AsyncTask.Status.FINISHED){
            recordAudioSync.done();
            recordAudioSync.cancel(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(recordAudioSync != null && recordAudioSync.getStatus() != AsyncTask.Status.FINISHED){
            recordAudioSync.done();
            recordAudioSync.cancel(true);
        }
    }

    @Override
    public void onTimerTick(@NonNull String duration) {
        txtTimer.setText(duration);
        setTime.setText("");
    }

    private class LongOperation extends AsyncTask<String, Void,String> {


        MediaRecorder recorder;
        int clapDetectedNumber;


        @Override
        protected String doInBackground(String... strings) {
            recordAudio();
            return ""+ clapDetectedNumber;
        }

        @Override
        protected void onPreExecute() {
            //btnStart.setImageResource(R.drawable.ic_baseline_mic_24);
            btnStart.setEnabled(false);

        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(HomePage.this,s+ " Shots",Toast.LENGTH_SHORT).show();
            btnStart.setImageResource(R.drawable.ic_baseline_mic_off_24);
            btnStart.setEnabled(true);
        }

        private void recordAudio() {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile("/data/data/" + getPackageName()+"/recording.3pg");
            int startAmplitude = 0;
            int finishAmplitude;
            int amplitudeThreshold = 18000;
            int counter = 0;
            timer = new Timer(HomePage.this);
            try{
                recorder.prepare();
                recorder.start();
                Arrays.fill(str, null);
                timer.start();
                check1 = false;
                startAmplitude = recorder.getMaxAmplitude();

            }catch (IOException e) {
                e.printStackTrace();
            }do{
                if(isCancelled()) {
                    break;
                }
                if (check1) {
                    break;
                }

                counter++;
                waitSome();
                finishAmplitude = recorder.getMaxAmplitude();
                if(finishAmplitude >= amplitudeThreshold) {
                    str[clapDetectedNumber] = timerTime.getText().toString();
                    clapDetectedNumber++;
                    count++;
                }

            }
            //while(counter < (clipDuration *3.5));
            while(checkForStop);

            if (!check1) {
                done();
            }
            else{
                doneB();
            }
        }

        private void done() {
            if(recorder != null) {
                recorder.stop();
                recorder.release();
                timer.stop();
                txtTimer.setText("00:00:00");
                setTime.post(new Runnable() {
                    @Override
                    public void run() {
                        setTime.setText("1           "+str[0] + "\n" +"2           "+str[1] +"\n" +"3           "+ str[2]);
                    }
                });
                //Arrays.fill(str, null);
                check1 = false;
                checkForStop = true;
                playBtn.setBackground(ContextCompat.getDrawable(HomePage.this, R.drawable.button));
                btnStart.setBackground(ContextCompat.getDrawable(HomePage.this, R.drawable.ic_baseline_mic_off_24));
            }
        }

        private void doneB() {
            if(recorder != null) {
                recorder.stop();
                //recorder.release();
                timer.stop();
                checkForStop = true;
                txtTimer.setText("00:00:00");
                //Arrays.fill(str, null);
                check1 = false;
                playBtn.setBackground(ContextCompat.getDrawable(HomePage.this, R.drawable.button));
                btnStart.setBackground(ContextCompat.getDrawable(HomePage.this, R.drawable.ic_baseline_mic_off_24));
            }
        }

        private void waitSome() {
            try{
                Thread.sleep(250);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}