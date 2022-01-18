package com.example.protimer.ui.ui

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.protimer.R
import com.example.protimer.ui.ui.SignIn.Login
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottomsheet.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val REQUEST_CODE = 200
class MainActivity : AppCompatActivity() , Timer.OnTimerListener{


    private var permissions = arrayOf(Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var permissionGranted  = false

    private lateinit var recorder: MediaRecorder
    private var dirPath = ""
    private var filename = ""
    private var isRecording = false
    private var isPaused = false
    private lateinit var timer: Timer
    private var path = ""
    private var recordFile = ""
    private var clapDetectedNumber:Int = 0
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logOut.setOnClickListener {
            val preferences = this.getSharedPreferences("checkbox", AppCompatActivity.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = preferences!!.edit()
            editor.putString("remember","false")
            editor.apply()

            FirebaseAuth.getInstance().signOut()
            Toast.makeText(
                this,
                "Logged Out",
                Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, Login::class.java))
            this.finish()
        }


        result.isClickable = false

        permissionGranted = ActivityCompat.checkSelfPermission(this,permissions[0]) == PackageManager.PERMISSION_GRANTED
        if(!permissionGranted)
            ActivityCompat.requestPermissions(this,permissions, REQUEST_CODE)

        bottomSheetBehavior = BottomSheetBehavior.from(Bottom_sheet)
        bottomSheetBehavior.peekHeight = 0
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        playbutton.setOnClickListener {
            when {
                isPaused -> resumeRecording()
                isRecording -> pauseRecorder()
                else -> startRecording()
            }
        }
        result.setOnClickListener {
            var mp = MediaPlayer()
            mp.setDataSource(path+"/"+recordFile)
            mp.prepare()
            mp.start()
        }


        resetButton.setOnClickListener {
            stopRecorder()
            playbutton.setImageResource(R.drawable.button)
            Toast.makeText(this,"Record Saved",Toast.LENGTH_SHORT).show()
        }

        profileButton.setOnClickListener {
            val intent = Intent(this,Profile::class.java)
            startActivity(intent)
        }

        bottomNav.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottom_sheetBG.visibility = View.VISIBLE
        }

        cancel_btn.setOnClickListener{
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            bottom_sheetBG.visibility = View.GONE
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE)
            permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
    }

    private fun pauseRecorder(){
        recorder.pause()
        isPaused = true
        playbutton.background = ContextCompat.getDrawable(this, R.drawable.button)
        timer.pause()
    }

    private fun resumeRecording() {
        recorder.resume()
        isPaused = false
        playbutton.background = ContextCompat.getDrawable(this, R.drawable.ic_baseline_pause_circle_filled_24)
        timer.start()
    }

    private fun startRecording() {
        if(!permissionGranted) {
            ActivityCompat.requestPermissions(this,permissions, REQUEST_CODE)
            return
        }

        timer = Timer(this)

        recorder = MediaRecorder()
        dirPath  = "${externalCacheDir?.absolutePath}/"

        var simpleDateFormat = SimpleDateFormat("yyyy.MM.DD_hh.mm.ss")
        var date = simpleDateFormat.format(Date())
        filename = "audio_record_$date"
        path = this.getExternalFilesDir("/")!!.absolutePath
        recordFile = "filename.3gp"

        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(path+"/"+recordFile)
            var startAmplitude = 0
            var amplitudeThreshold = 18000
            var counter = 0

            try {
                prepare()

            }catch (e:IOException){
                e.printStackTrace()
            }
            start()
            var finishAmplitude = maxAmplitude
            if (finishAmplitude >= amplitudeThreshold){
                clapDetectedNumber++
            }
        }

        playbutton.background = ContextCompat.getDrawable(this, R.drawable.ic_baseline_pause_circle_filled_24)
        isRecording = true
        isPaused = false

        timer.start()

        result.isClickable = false
        resetButton.isClickable = true
    }

    private fun stopRecorder() {
        timer.stop()

        recorder.apply {
            stop()
            release()

        }
        Toast.makeText(this,"You Clapped $clapDetectedNumber",Toast.LENGTH_SHORT).show()
        isPaused = false
        isRecording = false
        result.isClickable = true
        resetButton.isClickable = false
        textView.text = "00:00"
    }

    override fun onTimerTick(duration: String) {
        textView.text = duration
    }
}