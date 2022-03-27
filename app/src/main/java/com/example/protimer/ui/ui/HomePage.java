package com.example.protimer.ui.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.protimer.R;
import com.example.protimer.ui.ui.SignIn.Login;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

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
    String[] splitStr;
    TextView timerTime;
    TextView select;
    ImageButton resetBtn;
    Boolean check1;
    Button playBtn;
    TextView setTime;
    private Handler mHandler = new Handler();
    Button stopBtn;
    Boolean checkForStop;
    LinearLayout logOut;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    LinearLayout tips;
    LinearLayout setting;
    RelativeLayout history11;
    ImageButton save;
    CollectionReference db;
    FirebaseUser fu;
    String userid;
    String timehistory;
    HashMap<String, String> inputs;
    String result11;
    ImageButton btnmenu;
    int amplitudeThreshold;
    TextView txtAmplitude;
    String[] str1;
    long startTime = 0L, timeinmilli =0L,timeswapBuff =0L, updateTime =0L;
    Handler customHandler = new Handler();

    Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            timeinmilli = SystemClock.uptimeMillis()-startTime;
            updateTime = timeswapBuff+timeinmilli;
            int secs = (int) (updateTime/1000);
            int mins =secs/60;
            secs%=60;
            int milliseconds = (int)(updateTime%1000);
            txtTimer.setText(""+mins+":"+String.format("%02d",secs)+":"+String.format("%03d",milliseconds));
            customHandler.postDelayed(this,0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initializeHandles();
        //ArrayAdapter<String> spinDurationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,durationItems);
        //spinDurationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spinDuration.setAdapter(spinDurationAdapter);
        timerTime = findViewById(R.id.textView2);
        select = findViewById(R.id.textView);
        checkForStop = true;
        amplitudeThreshold = 18000;
        stopBtn.setEnabled(false);

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
                    playBtn.setEnabled(false);
                    result11 ="";
                    setTime.setText("");
                    stopBtn.setVisibility(View.VISIBLE);
                    //mHandler.postDelayed((Runnable) HomePage.this,3000);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {


                            //final MediaPlayer mediaPlayer = MediaPlayer.create(HomePage.this, R.raw.sound);
                            stopBtn.setBackground(ContextCompat.getDrawable(HomePage.this, R.drawable.button1));
                            stopBtn.setEnabled(true);
                            recordAudioSync = new LongOperation();
                            //mediaPlayer.start();
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
                Bundle b = ActivityOptions.makeSceneTransitionAnimation(HomePage.this).toBundle();
                startActivity(switchActivityIntent,b);
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent switchActivityIntent = new Intent(HomePage.this,Setting.class);
                startActivity(switchActivityIntent);
            }
        });

        /*resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check1 == false) {
                    txtTimer.setText("00:00:0");
                    setTime.setText("");
                }
                result11 ="";
                check1 = true;
                playBtn.setEnabled(true);
            }
        });*/

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForStop = false;
                stopBtn.setEnabled(false);
                playBtn.setEnabled(true);
            }
        });

        fu = FirebaseAuth.getInstance().getCurrentUser();
        assert fu != null;
        userid = fu.getUid();

        save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (!setTime.getText().equals("")) {

                    LocalDateTime myDateObj = LocalDateTime.now();
                    System.out.println("Before formatting: " + myDateObj);
                    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                    String formattedDate = myDateObj.format(myFormatObj);
                    db = FirebaseFirestore.getInstance().collection("history").document(userid).collection("history");
                    timehistory = setTime.getText().toString().trim();
                    inputs = new HashMap<String, String>();
                    inputs.put("date", formattedDate.toString());
                    inputs.put("history", timehistory);
                    db.add(inputs);
                    Toast.makeText(HomePage.this, "Saved", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(HomePage.this, "Empty", Toast.LENGTH_SHORT).show();
                }
            }
        });


        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.homePage);
        final View bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet11);
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
        btnmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(HomePage.this);
                View mView = getLayoutInflater().inflate(R.layout.custom_dialog,null);

                final EditText txt_inputText = (EditText)mView.findViewById(R.id.txt_input);
                Button btn_cancel = (Button)mView.findViewById(R.id.cancelBtn);
                Button btn_ok = (Button)mView.findViewById(R.id.okbtn);
                alert.setView(mView);

                final AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!txt_inputText.getText().toString().equals("")) {
                            float decibels = Integer.parseInt(String.valueOf(txt_inputText.getText()));
                            amplitudeThreshold = (int) Math.pow(10,(decibels/20));
                            //amplitudeThreshold = Integer.parseInt(String.valueOf(txt_inputText.getText()));
                            txtAmplitude.setText(txt_inputText.getText());
                            alertDialog.dismiss();
                        }
                    }
                });
                //showMenu();
                alertDialog.show();
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

   /* private void showMenu() {
        PopupMenu popupMenu = new PopupMenu(this,btnmenu);
        popupMenu.getMenuInflater().inflate(R.menu.contents, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item0:
                        amplitudeThreshold = 18000;
                        txtAmplitude.setText(amplitudeThreshold.toString());
                        break;

                    case R.id.item1:
                        amplitudeThreshold = 20000;
                        txtAmplitude.setText(amplitudeThreshold.toString());
                        break;

                    case R.id.item2:
                        amplitudeThreshold = 25000;
                        txtAmplitude.setText(amplitudeThreshold.toString());
                        break;
                    case R.id.item3:
                        amplitudeThreshold = 30000;
                        txtAmplitude.setText(amplitudeThreshold.toString());
                        break;
                    case R.id.item4:
                        amplitudeThreshold = 35000;
                        txtAmplitude.setText(amplitudeThreshold.toString());

                        break;
                    case R.id.item5:
                        amplitudeThreshold = 40000;
                        txtAmplitude.setText(amplitudeThreshold.toString());

                        break;
                    case R.id.item6:
                        amplitudeThreshold = 45000;
                        txtAmplitude.setText(amplitudeThreshold.toString());

                        break;
                    case R.id.item7:
                        amplitudeThreshold = 50000;
                        txtAmplitude.setText(amplitudeThreshold.toString());
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }*/

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
        //resetBtn = findViewById(R.id.resetBtn);
        playBtn = findViewById(R.id.playbutton);
        setTime = findViewById(R.id.setTime);
        check1 = false;
        str = new String[10];
        str1 = new String[40];
        result11 = "";
        stopBtn = findViewById(R.id.stopBtn);
        logOut = findViewById(R.id.logOut);
        tips = findViewById(R.id.tips);
        save = findViewById(R.id.saveButton);
        history11 = findViewById(R.id.layout5);
        btnmenu = findViewById(R.id.amplitudeEdit);
        txtAmplitude = findViewById(R.id.txt_amplitude);
        splitStr = new String[10];
        setting = findViewById(R.id.setting);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(recordAudioSync != null && recordAudioSync.getStatus() != AsyncTask.Status.FINISHED){
            recordAudioSync.done();
            recordAudioSync.cancel(true);
            playBtn.setEnabled(true);
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
        //setTime.setText("");
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
            int counter = 0;
            timer = new Timer(HomePage.this);
            try{
                recorder.prepare();
                final MediaPlayer mediaPlayer = MediaPlayer.create(HomePage.this, R.raw.sound);
                mediaPlayer.start();
                recorder.start();
                Arrays.fill(str, null);
                //timer.start();
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread,0);

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
                    str[0] = timerTime.getText().toString();
                    str1[clapDetectedNumber] = timerTime.getText().toString();
                    clapDetectedNumber++;
                    count++;
                    try {
                        // code runs in a thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (clapDetectedNumber == 1) {
                                    result11 = (clapDetectedNumber) + "             " + str[0] + "                 " + "0:00:000" + "\n";
                                }else{
                                    Boolean check = false;
                                    if (str1[clapDetectedNumber-1] != null) {
                                        String test = str1[clapDetectedNumber-1];
                                        String test1 = str1[clapDetectedNumber - 2];
                                        StringBuilder string = new StringBuilder();
                                        StringBuilder string1 = new StringBuilder();

                                        for (int j = 0; j < str1[clapDetectedNumber-1].length(); j++) {
                                            if (!check) {
                                                if (test.charAt(j) != '0' && test.charAt(j) != ':') {
                                                    check = true;
                                                    string.append(test.charAt(j));
                                                }
                                                if (test1.charAt(j) != '0' && test1.charAt(j) != ':') {
                                                    check = true;
                                                    string1.append(test1.charAt(j));
                                                }
                                            } else {
                                                if (test.charAt(j) != ':') {
                                                    string.append(test.charAt(j));
                                                }
                                                if (test1.charAt(j) != ':') {
                                                    string1.append(test1.charAt(j));
                                                }
                                            }
                                        }
                                        Integer n = Integer.parseInt(String.valueOf(string));
                                        Integer m = Integer.parseInt(String.valueOf(string1));
                                        Integer splitSecond = n - m;
                                        String diff = "";
                                        if (splitSecond.toString().length() == 1) {
                                            diff = "0:00:" + splitSecond;
                                        }
                                        if (splitSecond.toString().length() == 3) {
                                            diff = "0:00:" + splitSecond.toString().charAt(0) + splitSecond.toString().charAt(1)+ splitSecond.toString().charAt(2);;
                                        }
                                        if (splitSecond.toString().length() == 4) {
                                            diff = "0:0" + splitSecond.toString().charAt(0) + ":" + splitSecond.toString().charAt(1) + splitSecond.toString().charAt(2)+ splitSecond.toString().charAt(3);
                                        }
                                        if (splitSecond.toString().length() == 5) {
                                            diff = "0:" + splitSecond.toString().charAt(0) +splitSecond.toString().charAt(1) + ":"+ splitSecond.toString().charAt(2)  + splitSecond.toString().charAt(3)+ splitSecond.toString().charAt(4);;
                                        }
                                        result11 += (clapDetectedNumber) + "             " + str1[clapDetectedNumber-1] + "                 " + (diff) + "\n";
                                }
                                }
                                setTime.setText(result11);

                            }
                        });
                    } catch (final Exception ex) {
                        Log.i("---","Exception in thread");
                    }
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

            final Boolean[] check = {false};
            if(recorder != null) {
                recorder.stop();
                recorder.release();
                //timer.stop();
                timeswapBuff+=timeinmilli;
                customHandler.removeCallbacks(updateTimerThread);
                startTime = 0L;
                timeinmilli =0L;
                timeswapBuff =0L;
                updateTime =0L;
                txtTimer.setText("0:00:000");
                /*setTime.post(new Runnable() {
                    @Override
                    public void run() {
                        Integer count = 0;
                        int count1 = 0;
                        for (int i=0; i<str.length; i++) {
                            if (str[i] != null) {
                                count1++;
                                if (i == 0) {
                                    result11 += (count + 1) + "             " + str[i] + "                 " + "00:00:0" + "\n";
                                } else if (str[i] != null) {
                                    String test = str[i];
                                    String test1 = str[i - 1];
                                    StringBuilder string = new StringBuilder();
                                    StringBuilder string1 = new StringBuilder();

                                    for (int j = 0; j < str[i].length(); j++) {
                                        if (!check[0]) {
                                            if (test.charAt(j) != '0' && test.charAt(j) != ':') {
                                                check[0] = true;
                                                string.append(test.charAt(j));
                                            }
                                            if (test1.charAt(j) != '0' && test1.charAt(j) != ':') {
                                                check[0] = true;
                                                string1.append(test1.charAt(j));
                                            }
                                        } else {
                                            if (test.charAt(j) != ':') {
                                                string.append(test.charAt(j));
                                            }
                                            if (test1.charAt(j) != ':') {
                                                string1.append(test1.charAt(j));
                                            }
                                        }
                                    }
                                    Integer n = Integer.parseInt(String.valueOf(string));
                                    Integer m = Integer.parseInt(String.valueOf(string1));
                                    Integer splitSecond = n - m;
                                    String diff = "";
                                    if (splitSecond.toString().length() == 1) {
                                        diff = "00:00:" + splitSecond;
                                    }
                                    if (splitSecond.toString().length() == 2) {
                                        diff = "00:0" + splitSecond.toString().charAt(0) + ":" + splitSecond.toString().charAt(1);
                                    }
                                    if (splitSecond.toString().length() == 3) {
                                        diff = "00:" + splitSecond.toString().charAt(0) + splitSecond.toString().charAt(1) + ":" + splitSecond.toString().charAt(2);
                                    }
                                    if (splitSecond.toString().length() == 4) {
                                        diff = "0" + splitSecond.toString().charAt(0) + ":" + splitSecond.toString().charAt(1) + splitSecond.toString().charAt(2) + ":" + splitSecond.toString().charAt(3);
                                    }
                                    result11 += (count + 1) + "             " + str[i] + "                 " + (diff) + "\n";
                                } else {
                                    result11 += (count + 1) + "             " + str[i] + "\n";
                                }
                            }
                            count++;
                        }
                        setTime.setText(result11);*/
                result11 = "";
                check1 = false;
                checkForStop = true;
                stopBtn.setBackground(ContextCompat.getDrawable(HomePage.this, R.drawable.button));
                btnStart.setBackground(ContextCompat.getDrawable(HomePage.this, R.drawable.ic_baseline_mic_off_24));
            }
        }

        private void doneB() {
            if(recorder != null) {
                recorder.stop();
                //recorder.release();
                timer.stop();
                checkForStop = true;
                txtTimer.setText("00:00:0");
                //Arrays.fill(str, null);
                check1 = false;
                stopBtn.setBackground(ContextCompat.getDrawable(HomePage.this, R.drawable.button));
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