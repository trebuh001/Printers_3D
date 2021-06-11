package com.example.printers_3d

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.bumptech.glide.request.target.Target


import kotlinx.android.synthetic.main.activity_big_picture.*


class BigPictureActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_big_picture)
        Progress_bar_big_picture.visibility= View.VISIBLE
        val intent = intent

// Get the extras (if there are any)

// Get the extras (if there are any)
        val extras = intent.extras
        val imageView = findViewById<ImageView>(R.id.IV_big_picture)
        // Reference to an image file in Cloud Storage
        if (extras != null) {
            if(extras.containsKey("BigAvatarFromUserSettings"))
            {
                val storageRef =
                    Firebase.storage.reference.child("UserPhotoPictures").child("BigPicture")
                        .child(FirebaseAuth.getInstance().uid.toString())
                storageRef.downloadUrl.addOnSuccessListener { Uri ->
                    val imageURL = Uri.toString()
                    Glide.with(this /* context */)
                        .load(imageURL)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(p0: GlideException?, p1: Any?, p2: Target<Drawable>?, p3: Boolean): Boolean {
                                finish()
                                return true
                            }
                            override fun onResourceReady(p0: Drawable?, p1: Any?, p2: Target<Drawable>?, p3: DataSource?, p4: Boolean): Boolean {
                                Progress_bar_big_picture.visibility = View.INVISIBLE
                                return false
                            }
                        })
                        .into(imageView)
                    Progress_bar_big_picture.visibility = View.INVISIBLE
                }.addOnFailureListener {
                    this.finish();
                    Progress_bar_big_picture.visibility = View.INVISIBLE
                }
            }
            else if(extras.containsKey("BigAvatarFromUserShowSettings"))
            {
                val storageRef =
                    Firebase.storage.reference.child("UserPhotoPictures").child("BigPicture")
                        .child(extras.getString("BigAvatarFromUserShowSettings").toString())
                storageRef.downloadUrl.addOnSuccessListener { Uri ->
                   val imageURL = Uri.toString()
                    Glide.with(this /* context */)
                        .load(imageURL)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(p0: GlideException?, p1: Any?, p2: Target<Drawable>?, p3: Boolean): Boolean {
                                finish()
                                return true
                            }
                            override fun onResourceReady(p0: Drawable?, p1: Any?, p2: Target<Drawable>?, p3: DataSource?, p4: Boolean): Boolean {
                                Progress_bar_big_picture.visibility = View.INVISIBLE
                                return false
                            }
                        })
                        .into(imageView)
                    Progress_bar_big_picture.visibility = View.INVISIBLE
                }.addOnFailureListener {
                    this.finish();
                    Progress_bar_big_picture.visibility = View.INVISIBLE
                }
            }
            else if(extras.containsKey("BigPictureFromThread"))
            {
                Glide.with(this /* context */)
                    .load(extras.getString("BigPictureFromThread"))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(p0: GlideException?, p1: Any?, p2: Target<Drawable>?, p3: Boolean): Boolean {
                            finish()
                            return true
                        }
                        override fun onResourceReady(p0: Drawable?, p1: Any?, p2: Target<Drawable>?, p3: DataSource?, p4: Boolean): Boolean {
                            Progress_bar_big_picture.visibility = View.INVISIBLE
                            return false
                        }
                    })
                    .into(imageView)
            }
            else
            {
                this.finish()
            }
        }
    }
}