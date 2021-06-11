package com.example.printers_3d

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_user_settings.*
import kotlinx.android.synthetic.main.activity_user_settings_show.*

class UserSettingsShowActivity : AppCompatActivity() {

    val TAG = "UserSettingsShow TAG"
    private lateinit var imageUri: Uri
    private val REQUEST_IMAGE_CAPTURE = 100
    private val PICK_IMAGE_REQUEST = 123
    var user_id = ""
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var appSettingsPrefs: SharedPreferences

    // ...
    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;

        //Permission code
        private val PERMISSION_CODE = 1001;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_settings_show)
        appSettingsPrefs = getSharedPreferences(
            "AppSettingPrefs",
            Context.MODE_PRIVATE
        )
        val extras= intent.extras
        if(extras!=null) {

            if (extras.containsKey("USER_ID")) {
                this.user_id = extras.getString("USER_ID").toString()
            }
        }
        val database = Firebase.database.reference
        val isNightModeOn: Boolean = appSettingsPrefs.getBoolean("NightMode", true)

        Progress_bar_user_show_settings.visibility = View.VISIBLE

        val ordersRef =
            database.child("Users").orderByKey().equalTo(user_id)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    TV_show_user_name_database.setText(ds.child("name").getValue(String::class.java))
                    TV_show_user_description_database.setText(getString(R.string.description)+": "+
                            ds.child("description").getValue(String::class.java))
                    //Log.d(TAG, username)
                    Progress_bar_user_show_settings.visibility = View.GONE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
                Progress_bar_user_settings.visibility = View.GONE
            }
        }
        ordersRef.addListenerForSingleValueEvent(valueEventListener)

        Progress_bar_show_user_photo.visibility = View.VISIBLE
        val imageView = findViewById<ImageView>(R.id.IV_user_photo)
        // Reference to an image file in Cloud Storage
        val storageRef = Firebase.storage.reference.child("UserPhotoPictures").child("Thumbnails")
            .child(user_id)
        storageRef.downloadUrl.addOnSuccessListener { Uri ->
            val imageURL = Uri.toString()
            Glide.with(this /* context */)
                .load(imageURL)
                .into(imageView)
        }.addOnFailureListener {
            // Handle any errors
        }
        Progress_bar_show_user_photo.visibility = View.INVISIBLE

    }

    fun UserPhotoOpenBig(view: View) {

        val intent = Intent(this, BigPictureActivity::class.java)
        intent.putExtra("BigAvatarFromUserShowSettings", user_id)
        startActivity(intent)
    }
}