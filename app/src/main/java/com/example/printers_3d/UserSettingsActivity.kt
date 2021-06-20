package com.example.printers_3d

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.activity_user_settings.*
import java.io.ByteArrayOutputStream
import java.util.*


//metoda zwraca wartość logiczną
fun methodBoolean(arg1: String, arg2:Int):Boolean
{
    //lista instrukcji
    return true
    //lista instrukcji
    return false
}
//metoda nie zwraca żadnej wartości
fun methodUnit():Unit
{
    //lista instrukcji
}


@Suppress("DEPRECATION")
class UserSettingsActivity : AppCompatActivity() {
    val TAG="UserSettings TAG"
    private lateinit var imageUri: Uri
    private  val REQUEST_IMAGE_CAPTURE=100
    private  val PICK_IMAGE_REQUEST=123
    var old_nickname=""
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
        setContentView(R.layout.activity_user_settings)
        appSettingsPrefs= getSharedPreferences("AppSettingPrefs",
            Context.MODE_PRIVATE)
        val database = Firebase.database.reference
        val isNightModeOn:Boolean= appSettingsPrefs.getBoolean("NightMode",true)
        if(isNightModeOn)
        {
            IV_edit_name_confirm.setImageResource(R.drawable.confirm_bright)
            IV_logout.setImageResource(R.drawable.logout_bright)
            IV_settings.setImageResource(R.drawable.settings_bright)
        }
        else
        {
            IV_edit_name_confirm.setImageResource(R.drawable.confirm)
            IV_logout.setImageResource(R.drawable.logout)
            IV_settings.setImageResource(R.drawable.settings)
        }
        Progress_bar_user_settings.visibility= View.VISIBLE

        val ordersRef = database.child("Users").orderByKey().equalTo(FirebaseAuth.getInstance().uid.toString())
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                for (ds in dataSnapshot.children) {
                    val sharedPrefsEdit: SharedPreferences.Editor= appSettingsPrefs.edit()
                    sharedPrefsEdit.putString("username",ds.child("name").getValue(String::class.java))
                    sharedPrefsEdit.apply()
                    TV_user_name_database.setText(ds.child("name").getValue(String::class.java))
                    TV_user_email_database.text= ds.child("email").getValue(String::class.java)
                    old_nickname=TV_user_name_database.text.toString()
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
        val storageRef= Firebase.storage.reference.child("UserPhotoPictures").child("Thumbnails").
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
    fun UserPhotoOpenBig(view: View)
    {

        val intent= Intent(this,BigPictureActivity::class.java)
        intent.putExtra("BigAvatarFromUserSettings",FirebaseAuth.getInstance().uid.toString())
        startActivity(intent)
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
                    DeleteDataWithAvatarLinkToThread()
                    val storageRef= FirebaseStorage.getInstance().reference.child("UserPhotoPictures").child("Thumbnails").
                    child(FirebaseAuth.getInstance().uid.toString())
                    val storageRefBig= FirebaseStorage.getInstance().reference.child("UserPhotoPictures").child("BigPicture").
                    child(FirebaseAuth.getInstance().uid.toString())
                        storageRef.delete().addOnSuccessListener {
                           // Toast.makeText(this,getString(R.string.avatar_restoring_default_success),Toast.LENGTH_LONG).show()
                            storageRefBig.delete().addOnSuccessListener {
                                Toast.makeText(this,getString(R.string.avatar_restoring_default_success),Toast.LENGTH_LONG).show()
                            }.addOnFailureListener {
                                Toast.makeText(this,getString(R.string.avatar_restoring_default_error),Toast.LENGTH_LONG).show()
                            }
                        }.addOnFailureListener {
                            Toast.makeText(this,getString(R.string.avatar_restoring_default_error),Toast.LENGTH_LONG).show()

                        }



                    Progress_bar_user_photo.visibility=View.GONE
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);

                }
            }
        }

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.avatar_dialog_interface_message)).
        setPositiveButton(getString(R.string.avatar_dialog_interface_button_set_from_device_storage), dialogClickListener)
            .setNegativeButton(getString(R.string.avatar_dialog_interface_button_set_from_camera), dialogClickListener)
            .setNeutralButton(getString(R.string.avatar_dialog_interface_button_set_default),dialogClickListener).show()
    }
    fun CheckCharacters(string: String): Boolean {
        for (check in string)
        {
            if (check !in 'A'..'Z' && check !in 'a'..'z' && check !in '0'..'9' && check !in '_'..'_') {
                return false
            }
        }
        return true
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
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
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
            val resizedBitmap:Bitmap?=getResizedBitmap(imageBitmap, 102, 136) //96 128
            resizedBitmap?.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val storageRef= FirebaseStorage.getInstance().reference.child("UserPhotoPictures").child("Thumbnails").
            child(FirebaseAuth.getInstance().uid.toString())
            var x=0
            var y=0
            x=imageBitmap.width
            y=imageBitmap.height
            if(x>y)
            {

                val imageBitmapBig = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUrii)
                val baosBig = ByteArrayOutputStream()
                val resizedBitmapBig: Bitmap? = getResizedBitmap(imageBitmapBig, 800, 600) //96 128
                resizedBitmapBig?.compress(Bitmap.CompressFormat.PNG, 100, baosBig)
                //resizedBitmapBig?.compress(Bitmap.CompressFormat.PNG, 100, baosBig)


                val storageRefBig= FirebaseStorage.getInstance().reference.child("UserPhotoPictures").child("BigPicture").
                child(FirebaseAuth.getInstance().uid.toString())
                val imageThumbnail= baos.toByteArray()
                val upload=storageRef.putBytes(imageThumbnail)
                val imageBigPicture = baosBig.toByteArray()
                val uploadBigPicture=storageRefBig.putBytes(imageBigPicture)
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
                        uploadBigPicture.addOnCompleteListener{ task->
                            Progress_bar_user_photo.visibility=View.INVISIBLE
                            if(task.isSuccessful)
                            {

                            }
                            else
                            {
                                task.exception?.let{
                                    Toast.makeText(this, it.message!!, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                        UpdateDataWithAvatarLinkToThread()
                    }
                    else
                    {
                        task.exception?.let{
                            Toast.makeText(this, it.message!!, Toast.LENGTH_LONG).show()
                        }
                    }
                }

            }
            else
            {
                val imageBitmapBig = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUrii)
                val baosBig = ByteArrayOutputStream()
                val resizedBitmapBig: Bitmap? = getResizedBitmap(imageBitmapBig, 780, 1040) //96 128
                resizedBitmapBig?.compress(Bitmap.CompressFormat.PNG, 100, baosBig)
                //resizedBitmapBig?.compress(Bitmap.CompressFormat.PNG, 100, baosBig)

                val storageRefBig= FirebaseStorage.getInstance().reference.child("UserPhotoPictures").child("BigPicture").
                child(FirebaseAuth.getInstance().uid.toString())
                val imageThumbnail= baos.toByteArray()
                val upload=storageRef.putBytes(imageThumbnail)
                val imageBigPicture = baosBig.toByteArray()
                val uploadBigPicture=storageRefBig.putBytes(imageBigPicture)
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
                        uploadBigPicture.addOnCompleteListener{ task->
                            Progress_bar_user_photo.visibility=View.INVISIBLE
                            if(task.isSuccessful)
                            {

                            }
                            else
                            {
                                task.exception?.let{
                                    Toast.makeText(this, it.message!!, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                        UpdateDataWithAvatarLinkToThread()
                    }
                    else
                    {
                        task.exception?.let{
                            Toast.makeText(this, it.message!!, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }



        }  // take from gallery


        if(requestCode== REQUEST_IMAGE_CAPTURE && resultCode== RESULT_OK)
        {
            val imageBitmap =data?.extras?.get("data") as Bitmap
            val baos=ByteArrayOutputStream()
            val storageRef= FirebaseStorage.getInstance().reference.child("UserPhotoPictures").child("Thumbnails").
            child(FirebaseAuth.getInstance().uid.toString())
            val resizedBitmap:Bitmap?=getResizedBitmap(imageBitmap, 102, 136)//96 128
            resizedBitmap?.compress(Bitmap.CompressFormat.PNG, 100, baos)

            var x=0
            var y=0
            x=imageBitmap.width
            y=imageBitmap.height
            if(x>y)
            {

                val imageBitmapBig = data?.extras?.get("data") as Bitmap
                val baosBig = ByteArrayOutputStream()
                val resizedBitmapBig: Bitmap? = getResizedBitmap(imageBitmapBig, 800, 600) //96 128
                resizedBitmapBig?.compress(Bitmap.CompressFormat.PNG, 100, baosBig)
                //resizedBitmapBig?.compress(Bitmap.CompressFormat.PNG, 100, baosBig)


                val storageRefBig= FirebaseStorage.getInstance().reference.child("UserPhotoPictures").child("BigPicture").
                child(FirebaseAuth.getInstance().uid.toString())
                val imageThumbnail= baos.toByteArray()
                val upload=storageRef.putBytes(imageThumbnail)
                val imageBigPicture = baosBig.toByteArray()
                val uploadBigPicture=storageRefBig.putBytes(imageBigPicture)
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
                        uploadBigPicture.addOnCompleteListener{ task->
                            Progress_bar_user_photo.visibility=View.INVISIBLE
                            if(task.isSuccessful)
                            {

                            }
                            else
                            {
                                task.exception?.let{
                                    Toast.makeText(this, it.message!!, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                        UpdateDataWithAvatarLinkToThread()
                    }
                    else
                    {
                        task.exception?.let{
                            Toast.makeText(this, it.message!!, Toast.LENGTH_LONG).show()
                        }
                    }
                }

            }
            else
            {
                val imageBitmapBig = data?.extras?.get("data") as Bitmap
                val baosBig = ByteArrayOutputStream()
                val resizedBitmapBig: Bitmap? = getResizedBitmap(imageBitmapBig, 780, 1040) //96 128
                resizedBitmapBig?.compress(Bitmap.CompressFormat.PNG, 100, baosBig)
                //resizedBitmapBig?.compress(Bitmap.CompressFormat.PNG, 100, baosBig)

                val storageRefBig= FirebaseStorage.getInstance().reference.child("UserPhotoPictures").child("BigPicture").
                child(FirebaseAuth.getInstance().uid.toString())
                val imageThumbnail= baos.toByteArray()
                val upload=storageRef.putBytes(imageThumbnail)
                val imageBigPicture = baosBig.toByteArray()
                val uploadBigPicture=storageRefBig.putBytes(imageBigPicture)
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
                        uploadBigPicture.addOnCompleteListener{ task->
                            Progress_bar_user_photo.visibility=View.INVISIBLE
                            if(task.isSuccessful)
                            {

                            }
                            else
                            {
                                task.exception?.let{
                                    Toast.makeText(this, it.message!!, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                        UpdateDataWithAvatarLinkToThread()
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

    fun EditName()
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
                    UpdateNameInPosts(TV_user_name_database.text.toString())
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
    fun UpdateDataWithAvatarLinkToThread()
    {
        val storageRef= Firebase.storage.reference.child("UserPhotoPictures").child("Thumbnails").
        child(FirebaseAuth.getInstance().uid.toString())
        storageRef.downloadUrl.addOnSuccessListener { task ->
            val imageURL = task.toString()
            UpdateAvatarsInPosts(imageURL)
            //Toast.makeText(applicationContext,imageURL,Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            // Handle any errors
        }
    }
    fun DeleteDataWithAvatarLinkToThread()
    {
        val storageRef= Firebase.storage.reference.child("UserPhotoPictures").child("Thumbnails").
        child(FirebaseAuth.getInstance().uid.toString())
        storageRef.downloadUrl.addOnSuccessListener { task ->
            DeleteAvatarsInPosts()
            //Toast.makeText(applicationContext,imageURL,Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            // Handle any errors
        }
    }
    fun UpdateAvatarInPost(postID:String,subPage: String, imageURL:String)
    {
        FirebaseDatabase.getInstance().getReference("Posts")
            .child(subPage).child(postID).child("avatar").
            setValue(imageURL)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    Log.d(TAG, "Successful")
                }
                else
                {
                    Log.d(TAG, "Failed")
                }
            }
    }
    fun UpdateAvatarInPost(postID:String,subPage: String, imageURL:String, postIDChild: String)
    {
        FirebaseDatabase.getInstance().getReference("Posts")
            .child(subPage).child(postID).child(postIDChild)
            .child("avatar")
            .setValue(imageURL)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    Log.d(TAG, "Successful")
                }
                else
                {
                    Log.d(TAG, "Failed")
                }
            }
    }
    fun UpdateNameInPost(postID:String,subPage: String, name:String)
    {
        FirebaseDatabase.getInstance().getReference("Posts")
            .child(subPage).child(postID).child("name").
            setValue(name)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    Log.d(TAG, "Successful")
                }
                else
                {
                    Log.d(TAG, "Failed")
                }
            }
    }
    fun DeleteAvatarInPost(postID:String,subPage: String)
    {
        val deleteRef = FirebaseDatabase.getInstance().getReference("Posts")
            .child(subPage).child(postID).child("avatar")
        deleteRef.removeValue();
    }
    fun DeleteAvatarInPost(postID:String,subPage: String,postIDChild: String)
    {
        val deleteRef = FirebaseDatabase.getInstance().getReference("Posts")
            .child(subPage).child(postID).child(postIDChild).child("avatar")
        deleteRef.removeValue();
    }
    fun DeleteAvatarsInPosts()
    {
        val database = Firebase.database.reference
        val ordersRef = database.child("Posts").child("Forum").orderByValue()
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {

                    if (ds.child("user_id").getValue(String::class.java).toString()
                            .equals(FirebaseAuth.getInstance().uid.toString()))
                    {
                        val postID=ds.key.toString()
                        DeleteAvatarInPost(postID,"Forum")
                        val ordersRef0 = database.child("Posts").child("Forum")
                            .child(postID)
                            .orderByValue()
                        val valueEventListener0 = object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (ds2 in dataSnapshot.children) {

                                    if (ds2.child("user_id").getValue(String::class.java).toString()
                                            .equals(FirebaseAuth.getInstance().uid.toString()))
                                    {
                                        val postID2=ds2.key.toString()
                                        DeleteAvatarInPost(postID,"Forum",postID2)

                                    }
                                }
                            }
                            override fun onCancelled(databaseError: DatabaseError) {
                                Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
                            }
                        }
                        ordersRef0.addListenerForSingleValueEvent(valueEventListener0)
                    }

                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
            }
        }
        ordersRef.addListenerForSingleValueEvent(valueEventListener)

        val ordersRef2 = database.child("Posts").child("Market").orderByValue()
        val valueEventListener2 = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {

                    if (ds.child("user_id").getValue(String::class.java).toString()
                            .equals(FirebaseAuth.getInstance().uid.toString()))
                    {
                        val postID=ds.key.toString()
                        DeleteAvatarInPost(postID,"Market")
                        val ordersRef0 = database.child("Posts").child("Market")
                            .child(postID)
                            .orderByValue()
                        val valueEventListener0 = object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (ds2 in dataSnapshot.children) {

                                    if (ds2.child("user_id").getValue(String::class.java).toString()
                                            .equals(FirebaseAuth.getInstance().uid.toString()))
                                    {
                                        val postID2=ds2.key.toString()
                                        DeleteAvatarInPost(postID,"Market",postID2)

                                    }
                                }
                            }
                            override fun onCancelled(databaseError: DatabaseError) {
                                Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
                            }
                        }
                        ordersRef0.addListenerForSingleValueEvent(valueEventListener0)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
            }
        }
        ordersRef2.addListenerForSingleValueEvent(valueEventListener2)

        val ordersRef3 = database.child("Posts").child("Projects").orderByValue()
        val valueEventListener3 = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {

                    if (ds.child("user_id").getValue(String::class.java).toString()
                            .equals(FirebaseAuth.getInstance().uid.toString()))
                    {
                        val postID=ds.key.toString()
                        DeleteAvatarInPost(postID,"Projects")

                        val ordersRef0 = database.child("Posts").child("Projects")
                            .child(postID)
                            .orderByValue()
                        val valueEventListener0 = object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (ds2 in dataSnapshot.children) {

                                    if (ds2.child("user_id").getValue(String::class.java).toString()
                                            .equals(FirebaseAuth.getInstance().uid.toString()))
                                    {
                                        val postID2=ds2.key.toString()
                                        DeleteAvatarInPost(postID,"Projects",postID2)

                                    }
                                }
                            }
                            override fun onCancelled(databaseError: DatabaseError) {
                                Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
                            }
                        }
                        ordersRef0.addListenerForSingleValueEvent(valueEventListener0)
                    }

                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
            }
        }
        ordersRef3.addListenerForSingleValueEvent(valueEventListener3)
    }

    fun UpdateNameInPosts(name: String)
    {
        val database = Firebase.database.reference
        val ordersRef = database.child("Posts").child("Forum").orderByValue()
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {

                    if (ds.child("user_id").getValue(String::class.java).toString()
                            .equals(FirebaseAuth.getInstance().uid.toString()))
                    {
                        val postID=ds.key.toString()
                        UpdateNameInPost(postID,"Forum",name)
                    }

                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
            }
        }
        ordersRef.addListenerForSingleValueEvent(valueEventListener)

        val ordersRef2 = database.child("Posts").child("Market").orderByValue()
        val valueEventListener2 = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {

                    if (ds.child("user_id").getValue(String::class.java).toString()
                            .equals(FirebaseAuth.getInstance().uid.toString()))
                    {
                        val postID=ds.key.toString()
                        UpdateNameInPost(postID,"Market",name)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
            }
        }
        ordersRef2.addListenerForSingleValueEvent(valueEventListener2)

        val ordersRef3 = database.child("Posts").child("Projects").orderByValue()
        val valueEventListener3 = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {

                    if (ds.child("user_id").getValue(String::class.java).toString()
                            .equals(FirebaseAuth.getInstance().uid.toString()))
                    {
                        val postID=ds.key.toString()
                        UpdateNameInPost(postID,"Projects",name)
                    }

                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
            }
        }
        ordersRef3.addListenerForSingleValueEvent(valueEventListener3)
    }

    fun UpdateAvatarsInPosts(imageURL: String)
    {
        val database = Firebase.database.reference
        val ordersRef = database.child("Posts").child("Forum").orderByValue()
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {

                    if (ds.child("user_id").getValue(String::class.java).toString()
                            .equals(FirebaseAuth.getInstance().uid.toString()))
                    {
                        val postID=ds.key.toString()
                        UpdateAvatarInPost(postID,"Forum",imageURL)

                        val ordersRef0 = database.child("Posts").child("Forum")
                            .child(postID)
                            .orderByValue()
                        val valueEventListener0 = object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (ds2 in dataSnapshot.children) {

                                    if (ds2.child("user_id").getValue(String::class.java).toString()
                                            .equals(FirebaseAuth.getInstance().uid.toString()))
                                    {
                                        val postID2=ds2.key.toString()
                                        UpdateAvatarInPost(postID,"Forum",imageURL,postID2)

                                    }


                                }
                            }
                            override fun onCancelled(databaseError: DatabaseError) {
                                Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
                            }
                        }
                        ordersRef0.addListenerForSingleValueEvent(valueEventListener0)

                    }


                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
            }
        }
        ordersRef.addListenerForSingleValueEvent(valueEventListener)

        val ordersRef2 = database.child("Posts").child("Market").orderByValue()
        val valueEventListener2 = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {

                    if (ds.child("user_id").getValue(String::class.java).toString()
                            .equals(FirebaseAuth.getInstance().uid.toString()))
                    {
                        val postID=ds.key.toString()
                        UpdateAvatarInPost(postID,"Market",imageURL)
                        val ordersRef0 = database.child("Posts").child("Market")
                            .child(postID)
                            .orderByValue()
                        val valueEventListener0 = object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (ds2 in dataSnapshot.children) {

                                    if (ds2.child("user_id").getValue(String::class.java).toString()
                                            .equals(FirebaseAuth.getInstance().uid.toString()))
                                    {
                                        val postID2=ds2.key.toString()
                                        UpdateAvatarInPost(postID,"Market",imageURL,postID2)

                                    }


                                }
                            }
                            override fun onCancelled(databaseError: DatabaseError) {
                                Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
                            }
                        }
                        ordersRef0.addListenerForSingleValueEvent(valueEventListener0)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
            }
        }
        ordersRef2.addListenerForSingleValueEvent(valueEventListener2)

    val ordersRef3 = database.child("Posts").child("Projects").orderByValue()
    val valueEventListener3 = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (ds in dataSnapshot.children) {

                if (ds.child("user_id").getValue(String::class.java).toString()
                        .equals(FirebaseAuth.getInstance().uid.toString()))
                {
                    val postID=ds.key.toString()
                    UpdateAvatarInPost(postID,"Projects",imageURL)
                    val ordersRef0 = database.child("Posts").child("Projects")
                        .child(postID)
                        .orderByValue()
                    val valueEventListener0 = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (ds2 in dataSnapshot.children) {

                                if (ds2.child("user_id").getValue(String::class.java).toString()
                                        .equals(FirebaseAuth.getInstance().uid.toString()))
                                {
                                    val postID2=ds2.key.toString()
                                    UpdateAvatarInPost(postID,"Projects",imageURL,postID2)

                                }
                            }
                        }
                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
                        }
                    }
                    ordersRef0.addListenerForSingleValueEvent(valueEventListener0)
                }

            }
        }
        override fun onCancelled(databaseError: DatabaseError) {
            Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
        }
    }
    ordersRef3.addListenerForSingleValueEvent(valueEventListener3)
}
    fun CheckNameBusy()
    {
        val name:String=TV_user_name_database.text?.trim().toString()
        Progress_bar_user_settings.visibility= View.VISIBLE
        val database = Firebase.database.reference
        val ordersRef = database.child("Users").orderByValue()
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {

                    if (ds.child("name").getValue(String::class.java).toString()
                            .toLowerCase(Locale.ROOT)
                            .equals(name.toLowerCase(Locale.ROOT))&&
                        ds.child("name").getValue(String::class.java).toString()
                            .toLowerCase(Locale.ROOT)
                            !=(old_nickname.toLowerCase(Locale.ROOT)))
                            {
                                Progress_bar_user_settings.visibility = View.GONE
                                TV_user_name_database.setError(getString(R.string.toast_nickname_busy))
                                TV_user_name_database.requestFocus()
                                return
                            }

                }
                if(!CheckCharacters(name))
                {
                    TV_user_name_database.setError(getString(R.string.hint_name_special_characters_error))
                    TV_user_name_database.requestFocus()
                    Progress_bar_user_settings.visibility = View.GONE
                    return
                }
                EditName()
                Progress_bar_user_settings.visibility = View.GONE
            }
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
                }
            }
            ordersRef.addListenerForSingleValueEvent(valueEventListener)
        }
    fun EditNameButton(view: View)
    {
            CheckNameBusy()


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