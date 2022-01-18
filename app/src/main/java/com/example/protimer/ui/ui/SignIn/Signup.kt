package com.example.protimer.ui.ui.SignIn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.protimer.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_signup.*

class Signup : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        goToLogIn.setOnClickListener {
            val intent = Intent(this,Login::class.java)
            startActivity(intent)
            finish()
        }

        registerButton1.setOnClickListener {
            when {
                TextUtils.isEmpty(gmailInput.text.toString().trim{it<= ' '}) -> {
                    Toast.makeText(
                        this,
                        "Please Enter Email",
                        Toast.LENGTH_SHORT).show()
                }

                TextUtils.isEmpty(passwordInput.text.toString().trim{it<= ' '}) -> {
                    Toast.makeText(
                        this,
                        "Please Enter Password",
                        Toast.LENGTH_SHORT).show()
                }

                else -> {
                    val email: String = gmailInput.text.toString().trim { it <= ' '}
                    val password: String = passwordInput.text.toString().trim { it <= ' '}

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                        OnCompleteListener<AuthResult> {
                                task ->
                            if (task.isSuccessful) {
                                FirebaseAuth.getInstance().currentUser?.sendEmailVerification()?.addOnCompleteListener { task->
                                    if (task.isSuccessful){
                                        val intent = Intent(this, Login::class.java)
                                        Toast.makeText(this,"You are registered successful and emailed",
                                            Toast.LENGTH_SHORT).show()
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            }else {
                                Toast.makeText(this,task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }

    }

    fun AlreadyHaveAccount(view: View) {
        onBackPressed()
    }
}