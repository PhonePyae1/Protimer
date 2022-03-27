package com.example.protimer.ui.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.protimer.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_edit_profile_page.*
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.File
import kotlin.properties.Delegates

class EditProfilePage : AppCompatActivity() {
    lateinit var fStore: FirebaseFirestore
    lateinit var databaseReference: DatabaseReference
    lateinit var user: FirebaseUser
    lateinit var userid:String
    lateinit var imageUri: Uri
    lateinit var uploadTask: UploadTask
    lateinit var storageProfilePicsRef:StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile_page)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        user = FirebaseAuth.getInstance().currentUser!!
        // userid = FirebaseDatabase.getInstance().getReference("Users").toString()
        userid = user.uid

        val fAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val fStore:FirebaseFirestore = FirebaseFirestore.getInstance()
        var check:Boolean = true


        databaseReference = FirebaseDatabase.getInstance().getReference().child("User")
        val storageRef = FirebaseStorage.getInstance().reference.child("userProfiles/$userid")
        val localfile = File.createTempFile("tempImage","jpg")
        storageRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            editprofilePic.setImageBitmap(bitmap)
        }

        btneditprofileCancel.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        btneditprofilesave.setOnClickListener {
            var name: String = editprofileName.text.toString().trim()
            var bio: String = editprofileBio.text.toString().trim()
            var exp: String = editprofileExp.text.toString().trim()
            var gun: String = editprofilegun.text.toString().trim()
            if (name.isEmpty() || bio.isEmpty() || exp.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please fill in the fields !",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else{
                val documentReference: DocumentReference =
                    fStore.collection("users").document(userid)
                val storageReference =
                    FirebaseStorage.getInstance().getReference("userProfiles/$userid")
                var user = HashMap<String, Any>()
                user.put("name", name)
                user.put("bio", bio)
                user.put("exp", exp)
                user.put("gun",gun)
                if (check == false) {
                    storageReference.putFile(imageUri)
                }
                documentReference.set(user)
                Toast.makeText(
                    this,
                    "Saved",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this, Profile::class.java)
                startActivity(intent)
            }


        }
        editprofilePic.setOnClickListener {
            check = false
            selectImage()
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==100 && resultCode== RESULT_OK) {
            imageUri = data?.data!!
            editprofilePic.setImageURI(imageUri)
        }
    }
}