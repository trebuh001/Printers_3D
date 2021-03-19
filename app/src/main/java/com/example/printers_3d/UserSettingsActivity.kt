package com.example.printers_3d

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_user_settings.*
import java.io.ByteArrayOutputStream


@Suppress("DEPRECATION")
class UserSettingsActivity : AppCompatActivity() {
    val TAG="User Settings TAG"
    private lateinit var imageUri: Uri
    private  val REQUEST_IMAGE_CAPTURE=100
    private  val PICK_IMAGE_REQUEST=123
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

// ...
    companion object {
    //image pick code
    private val IMAGE_PICK_CODE = 1000;
    //Permission code
    private val PERMISSION_CODE = 1001;
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_settings)
        val database = Firebase.database.reference

        Progress_bar_user_settings.visibility= View.VISIBLE
        val ordersRef = database.child("Users").orderByKey().equalTo(FirebaseAuth.getInstance().uid.toString())
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    TV_user_name_database.setText(ds.child("name").getValue(String::class.java))
                    TV_user_email_database.text= ds.child("email").getValue(String::class.java)
                    Et_user_description_database.setText(
                        ds.child("description").getValue(String::class.java)

                    )
                    //Log.d(TAG, username)
                    Progress_bar_user_settings.visibility= View.GONE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
                Progress_bar_user_settings.visibility= View.GONE
            }
        }
        ordersRef.addListenerForSingleValueEvent(valueEventListener)




      Progress_bar_user_photo.visibility=View.VISIBLE
        val imageView = findViewById<ImageView>(R.id.IV_user_photo)
        // Reference to an image file in Cloud Storage
        val storageRef= Firebase.storage.reference.child("UserPhotoPictures").
        child(FirebaseAuth.getInstance().uid.toString())
        storageRef.downloadUrl.addOnSuccessListener { Uri ->
            val imageURL = Uri.toString()
            Glide.with(this /* context */)
                .load(imageURL)
                .into(imageView)
        }.addOnFailureListener {
            // Handle any errors
        }
        Progress_bar_user_photo.visibility=View.INVISIBLE


}



    fun GoForumButton(view: View)
    {
        val intent= Intent(this,StartForumActivity::class.java)
        startActivity(intent)
    }
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        if(mAuth.currentUser==null)
        {
            val intent=Intent(this, MainActivity::class.java)
            startActivity(intent)
            this.finish()
        }

    }

    fun ChangeAvatarButton(view: View) {


        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED){
                            //permission denied
                            val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            //show popup to request runtime permission
                            requestPermissions(permissions, PERMISSION_CODE);
                        }
                        else{
                            //permission already granted
                            val intent = Intent(Intent.ACTION_PICK)
                            intent.type = "image/*"
                            startActivityForResult(intent, IMAGE_PICK_CODE)
                        }
                    }
                    else{
                        //system OS is < Marshmallow
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        startActivityForResult(intent, IMAGE_PICK_CODE)
                    }

                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    val intent=Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
                           pictureIntent.resolveActivity(this.packageManager!!).also{
                              startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE)
                         }
                }
                }
                DialogInterface.BUTTON_NEUTRAL -> {
                    Progress_bar_user_photo.visibility=View.VISIBLE
                    val storageRef= FirebaseStorage.getInstance().reference.child("UserPhotoPictures").
                    child(FirebaseAuth.getInstance().uid.toString())
                        storageRef.delete().addOnSuccessListener {
                            Toast.makeText(this,getString(R.string.avatar_restoring_default_success),Toast.LENGTH_LONG).show()
                        }.addOnFailureListener {
                            Toast.makeText(this,getString(R.string.avatar_restoring_default_error),Toast.LENGTH_LONG).show()

                        }
                    Progress_bar_user_photo.visibility=View.GONE
                    this.finish()
                    startActivity(getIntent())

                }



            }
        }

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.avatar_dialog_interface_message)).
        setPositiveButton(getString(R.string.avatar_dialog_interface_button_set_from_device_storage), dialogClickListener)
            .setNegativeButton(getString(R.string.avatar_dialog_interface_button_set_from_camera), dialogClickListener)
            .setNeutralButton(getString(R.string.avatar_dialog_interface_button_set_default),dialogClickListener).show()

          /* val intent=Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
         pictureIntent.resolveActivity(this.packageManager!!).also{
             startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE)
         }

        }    Read from camera   */


    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent, IMAGE_PICK_CODE)

                } else {
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            val imageUrii =data?.data as Uri
            val imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUrii)
            val baos=ByteArrayOutputStream()
            val resizedBitmap:Bitmap?=getResizedBitmap(imageBitmap, 128, 128)
            resizedBitmap?.compress(Bitmap.CompressFormat.PNG, 100, baos)

            val storageRef= FirebaseStorage.getInstance().reference.child("UserPhotoPictures").
            child(FirebaseAuth.getInstance().uid.toString())
          //  imageBitmap.compress(Bitmap.CompressFormat.PNG,100,baos)

            val image= baos.toByteArray()
            val upload=storageRef.putBytes(image)
            Progress_bar_user_photo.visibility=View.VISIBLE
            upload.addOnCompleteListener{ task->
                Progress_bar_user_photo.visibility=View.INVISIBLE
                if(task.isSuccessful)
                {
                    storageRef.downloadUrl.addOnCompleteListener{ urlTask ->
                        urlTask.result?.let {
                            imageUri=it
                            // Toast.makeText(this,imageUri.toString(),Toast.LENGTH_LONG).show()
                            IV_user_photo.setImageBitmap(resizedBitmap)
                        }

                    }
                }
                else
                {
                    task.exception?.let{
                        Toast.makeText(this, it.message!!, Toast.LENGTH_LONG).show()
                    }
                }
            }

        }  // take from gallery


        if(requestCode== REQUEST_IMAGE_CAPTURE && resultCode== RESULT_OK)
        {
            val imageBitmap =data?.extras?.get("data") as Bitmap
            val baos=ByteArrayOutputStream()
            val storageRef= FirebaseStorage.getInstance().reference.child("UserPhotoPictures").
            child(FirebaseAuth.getInstance().uid.toString())
            val resizedBitmap:Bitmap?=getResizedBitmap(imageBitmap, 128, 128)
            resizedBitmap?.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val image= baos.toByteArray()
            val upload=storageRef.putBytes(image)
            Progress_bar_user_photo.visibility=View.VISIBLE
            upload.addOnCompleteListener{ task->
                Progress_bar_user_photo.visibility=View.INVISIBLE
                if(task.isSuccessful)
                {
                    storageRef.downloadUrl.addOnCompleteListener{ urlTask ->
                        urlTask.result?.let {
                            imageUri=it
                           // Toast.makeText(this,imageUri.toString(),Toast.LENGTH_LONG).show()
                            IV_user_photo.setImageBitmap(resizedBitmap)
                        }


                    }
                }
                else
                {
                    task.exception?.let{
                        Toast.makeText(this, it.message!!, Toast.LENGTH_LONG).show()
                    }
                }
            }

        }
    }
    fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(
            bm, 0, 0, width, height, matrix, false
        )
        bm.recycle()
        return resizedBitmap
    }
    fun EditNameButton(view: View)
    {

          FirebaseDatabase.getInstance().getReference("Users")
              .child(FirebaseAuth.getInstance().uid.toString()).child("name").
              setValue(TV_user_name_database.text.toString())
              .addOnCompleteListener { task ->
                  if (task.isSuccessful) {
                      Log.d(TAG, "Name in profile updated successfully.")
                      Toast.makeText(
                          this,
                          getString(R.string.name_profile_changed_success),
                          Toast.LENGTH_SHORT
                      ).show()
                      val intent= Intent(this, UserSettingsActivity::class.java)
                      this.finish()
                      startActivity(intent)
                  }
                  else
                  {
                      Log.d(TAG, "User re-authenticated.")
                      Toast.makeText(
                          this,
                          getString(R.string.name_profile_changed_error),
                          Toast.LENGTH_SHORT
                      ).show()

                  }
      }


    }
    fun UpdateDesciptionButton(view: View)
    {
        FirebaseDatabase.getInstance().getReference("Users")
            .child(FirebaseAuth.getInstance().uid.toString()).child("description").
            setValue(Et_user_description_database.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Description in profile updated successfully.")
                    Toast.makeText(
                        this,
                        getString(R.string.description_profile_changed_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent= Intent(this, UserSettingsActivity::class.java)
                    this.finish()
                    startActivity(intent)
                }
                else
                {
                    Log.d(TAG, "Description in profile updated error")
                    Toast.makeText(
                        this,
                        getString(R.string.description_profile_changed_error),
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }


    }
    fun ChangePasswordButton(view: View)
    {
        val intent= Intent(this, ChangePasswordActivity::class.java)
        startActivity(intent)
    }
    fun ChangeEmailButton(view: View)
    {
        val intent= Intent(this, ChangeEmailActivity::class.java)
        startActivity(intent)
    }
    fun LogoutButton(view: View)
    {
        Toast.makeText(this, getString(R.string.logout_successful), Toast.LENGTH_SHORT).show()
        mAuth.signOut()
        this.finish();
        startActivity(getIntent());
        //val intent=Intent(this,MainActivity::class.java)
        //startActivity(intent)
    }
    override fun onBackPressed() {
        // Do Here what ever you want do on back press;
    }
}