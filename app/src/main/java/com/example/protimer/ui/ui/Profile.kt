package com.example.protimer.ui.ui

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.example.protimer.R
import com.example.protimer.ui.ui.SignIn.Login
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.bottomsheet.*
import kotlinx.android.synthetic.main.item.*
import java.io.File

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        Backimage.setOnClickListener {
            startActivity(Intent(this, HomePage::class.java))
            this.finish()
        }
        editprofile11.setOnClickListener {
            val intent = Intent(this,EditProfilePage::class.java)
            startActivity(intent)
        }

        val user:FirebaseAuth = FirebaseAuth.getInstance()
        val userid = user.uid
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val storageRef = FirebaseStorage.getInstance().reference.child("userProfiles/$userid")
        val localfile = File.createTempFile("tempImage","jpg")
        storageRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            imageProfile.setImageBitmap(bitmap)
        }
        layout7.setOnClickListener {
            val preferences = this.getSharedPreferences("checkbox", AppCompatActivity.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = preferences!!.edit()
            editor.putString("remember","false")
            editor.apply()

            FirebaseAuth.getInstance().signOut()
            Toast.makeText(
                this,
                "Logged Out",
                Toast.LENGTH_SHORT).show()

            startActivity(Intent(this,Login::class.java))
            this.finish()
        }
        layout5.setOnClickListener{
            startActivity(Intent(this,History2::class.java))
            this.finish()
        }
        layout6.setOnClickListener {
            openDialog()
        }


        if (userid != null) {
            db.collection("users").document(userid).addSnapshotListener(object:
                EventListener<DocumentSnapshot> {
                override fun onEvent(value: DocumentSnapshot?, error: FirebaseFirestoreException?) {
                    var username: String? = value?.getString("name")
                    var bio:String? = value?.getString("bio")
                    var exp:String? = value?.get("exp") as String?
                    var gunn:String? = value?.get("gun")as String?

                    if (name != null) {
                        name.text = username
                    }
                    if (age != null) {
                        age.text = bio
                    }
                    if (profession != null) {
                        profession.text = exp
                    }
                    if (gun!=null){
                        gun.text = gunn
                    }
                }
            })
        }
    }
    fun openDialog() {
        val dialogTips = DialogTips()
        dialogTips.show(supportFragmentManager, "Tips")
    }
}