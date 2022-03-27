package com.example.protimer.ui.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.protimer.R
import com.example.protimer.ui.ui.SignIn.Login
import com.example.protimer.ui.ui.SignIn.Signup
import kotlinx.android.synthetic.main.activity_splash_screen.*
import kotlinx.android.synthetic.main.bottomsheet.view.*

class SplashScreen : AppCompatActivity() {

    lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()
        handler = Handler()
        handler.postDelayed({
            val intent = Intent(this,Login::class.java)
            startActivity(intent)
            finish()
        },3000)
    }
}