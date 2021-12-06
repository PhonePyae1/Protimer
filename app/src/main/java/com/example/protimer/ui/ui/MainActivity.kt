package com.example.protimer.ui.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.protimer.R
import kotlinx.android.synthetic.main.activity_main.*
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        result.isClickable = false

        permissionGranted = ActivityCompat.checkSelfPermission(this,permissions[0]) == PackageManager.PERMISSION_GRANTED
        if(!permissionGranted)
            ActivityCompat.requestPermissions(this,permissions, REQUEST_CODE)


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

            try {
                prepare()
            }catch (e:IOException){}

            start()
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