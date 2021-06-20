package com.example.printers_3d

import android.annotation.SuppressLint
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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_edit_post.*
import kotlinx.android.synthetic.main.activity_forum_thread.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class EditPostActivity : AppCompatActivity() {

    val list= ArrayList<Forum_List_Thread>()

    public var i=0
    var post_id=""
    var m_text : String = ""
    private var counter_images=0
    private lateinit var imageUri: Uri
    private var imageThumbnail: MutableList<ByteArray> = mutableListOf<ByteArray>()
    private var imageBigPicture: MutableList<ByteArray> = mutableListOf<ByteArray>()
    private  val REQUEST_IMAGE_CAPTURE=100
    var portalSubPage:String=""
    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;
    }
    val TAG="EditPost TAG"
    var isSubPage: String?="Forum"
    lateinit var database: DatabaseReference
    lateinit var appSettingsPrefs: SharedPreferences
    var user_ID: String?=null
    var post_IDParent: String?=null
    var post_IDChild: String?=null
    var post_Description: String?=null
    var post_Thumbnail : String?=null
    var post_BigPicture : String?=null

    var titleThread: String?=null
    var post_Position : String="0"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_post)
        appSettingsPrefs= getSharedPreferences(
            "AppSettingPrefs",
            Context.MODE_PRIVATE
        )
        val isNightModeOn:Boolean= appSettingsPrefs.getBoolean("NightMode", true)
        if(isNightModeOn)
        {
            btn_edit_image.setBackgroundResource(R.drawable.picture_bright)
        }
        else
        {
            btn_edit_image.setBackgroundResource(R.drawable.picture)
        }
        isSubPage= appSettingsPrefs.getString("PortalSubPage", null)
        portalSubPage = appSettingsPrefs.getString("PortalSubPage", "").toString()
        database = Firebase.database.reference
        Progress_bar_edit_thread.visibility= View.VISIBLE
        user_ID= intent.getStringExtra("USER_ID").toString()
        post_IDParent = intent.getStringExtra("POST_ID_PARENT").toString()
        post_IDChild = intent.getStringExtra("POST_ID_CHILD").toString()
        post_Description = intent.getStringExtra("POST_DESCRIPTION").toString()
        if(intent.getStringExtra("POST_THUMBNAIL")!=null && intent.getStringExtra("POST_BIG_PICTURE")!=null)
        {
            post_Thumbnail=intent.getStringExtra("POST_THUMBNAIL").toString()
            post_BigPicture=intent.getStringExtra("POST_BIG_PICTURE").toString()
        }
        Et_edit_thread_description.setText(post_Description)
        Progress_bar_edit_thread.visibility= View.GONE
    }

    @SuppressLint("SetTextI18n")
    fun AddPictureButton(v: View)
    {
        /* if(i>9)
         {
             Toast.makeText(applicationContext,getString(R.string.too_many_pictures),Toast.LENGTH_LONG).show()
             return
         }*/
        var input = EditText(applicationContext)
        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    val str_input = input.text.toString().trim()
                    val pattern: Pattern = Pattern.compile("[^A-Za-z0-9_ ]")
                    val matcher: Matcher = pattern.matcher(str_input)
                    if (matcher.find()) {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.file_special_characters_toast),
                            Toast.LENGTH_LONG
                        ).show()
                        return@OnClickListener
                    }
                    if (str_input.isEmpty() || str_input.length == 0 || str_input.equals("")) {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.empty_filename),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@OnClickListener
                    }
                    /*  for (i in 0 until m_text.size) {
                          if (str_input.equals(m_text[i])) {
                              Toast.makeText(
                                  applicationContext,
                                  getString(R.string.filename_exist),
                                  Toast.LENGTH_SHORT
                              ).show()
                              return@OnClickListener
                          }
                      }*/
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED
                        ) {
                            //permission denied
                            val permissions =
                                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            //show popup to request runtime permission
                            requestPermissions(permissions, EditPostActivity.PERMISSION_CODE);
                        } else {
                            //permission already granted
                            val intent = Intent(Intent.ACTION_PICK)
                            intent.type = "image/*"
                            startActivityForResult(intent, EditPostActivity.IMAGE_PICK_CODE)
                        }
                    } else {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        startActivityForResult(intent, EditPostActivity.IMAGE_PICK_CODE)
                    }
                    //m_text[counter_images] = input.text.toString()
                    m_text = input.text.toString()
                    counter_images++

                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    /*  val intent=Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
                        pictureIntent.resolveActivity(this.packageManager!!).also{
                            startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE)
                        }
                    }*/
                }

            }
        }
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        input.setHint(getString(R.string.enter_text))
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setMessage(getString(R.string.new_thread_dialog_message)).
        setPositiveButton(
            getString(R.string.confirm),
            dialogClickListener

        )
            .setNegativeButton(
                getString(R.string.cancel),
                dialogClickListener
            ).show()
    }


    fun SendPictureLinkToPost(fileName: String?, pictureSize: String)
    {
        if(post_Thumbnail!=null && post_BigPicture!=null)
        {
            val photoRef=
                FirebaseStorage.getInstance().getReferenceFromUrl(post_Thumbnail.toString())
            photoRef.delete()
            val photoRef2=
                FirebaseStorage.getInstance().getReferenceFromUrl(post_BigPicture.toString())
            photoRef2.delete()
        }

        // Toast.makeText(applicationContext,portalSubPage+"\n"+generatePostID+"\n"+pictureSize+"\n"+fileName,Toast.LENGTH_LONG).show()
     /*   if(post_Position.toInt()==0)
        {
            val storageRef= Firebase.storage.reference.child("Posts")
                .child(portalSubPage)
                .child(post_IDParent.toString())
                .child(pictureSize)
                .child(fileName.toString())
            storageRef.downloadUrl.addOnSuccessListener { task ->
                val imageURL = task.toString()
                SendPictureLinkToPostPart2(imageURL, pictureSize, generatePictureID)
//            Toast.makeText(applicationContext,imageURL,Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                // Handle any errors
            }
        }*/
      //  else {
            val storageRef = Firebase.storage.reference.child("Posts")
                .child(portalSubPage)
                .child(post_IDParent.toString())
                .child(post_IDChild.toString())
                .child(pictureSize)
                .child(fileName.toString())
            storageRef.downloadUrl.addOnSuccessListener { task ->
                val imageURL = task.toString()
                SendPictureLinkToPostPart2(imageURL, pictureSize)
//            Toast.makeText(applicationContext,imageURL,Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                // Handle any errors
            }
      //  }


    }

    fun SendPictureLinkToPostPart2(imageURL: String, pictureSize: String)
    {

        //  Toast.makeText(applicationContext,pictureSize +"\n"+generatePostID,Toast.LENGTH_LONG).show()
       /* if(post_Position.toInt()==0)
        {
            FirebaseDatabase.getInstance().getReference("Posts")
                .child(portalSubPage)
                .child(post_IDParent.toString())
                .child(pictureSize)
                .setValue(imageURL)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                    }
                    else {
                        Toast.makeText(applicationContext, "error1", Toast.LENGTH_LONG).show()
                    }
                }
        }*/
        //else
       // {
            FirebaseDatabase.getInstance().getReference("Posts")
                .child(portalSubPage)
                .child(post_IDParent.toString())
                .child(post_IDChild.toString())
                .child(pictureSize)
                .setValue(imageURL)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "", Toast.LENGTH_LONG).show()
                        val gotoScreenVar = Intent(this, StartForumActivity::class.java)
                        gotoScreenVar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(gotoScreenVar)
                    }
                    else {
                        Toast.makeText(applicationContext, "error1", Toast.LENGTH_LONG).show()
                    }
                }
     //   }

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            EditPostActivity.PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent, EditPostActivity.IMAGE_PICK_CODE)

                } else {
                    //permission from popup denied
                    Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT)
                        .show()
                    m_text = ""

                }
            }
        }
    }
    fun SaveDataWithAvatarLinkToThread()
    {

        val storageRef= Firebase.storage.reference.child("UserPhotoPictures").child("Thumbnails").
        child(FirebaseAuth.getInstance().uid.toString())
        storageRef.downloadUrl.addOnSuccessListener { task ->
            val imageURL = task.toString()
            SaveTextDataToPost(imageURL)

            //Toast.makeText(applicationContext,imageURL,Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            // Handle any errors
            SaveTextDataToPost()
        }

    }
    fun SaveThreadButton(v: View) {



        if (Et_edit_thread_description.text.trim().isEmpty())
        {
            Et_edit_thread_description.setError(getString(R.string.error_empty_description))
            Et_edit_thread_description.requestFocus()
            return
        }
        Progress_bar_edit_thread.visibility = View.VISIBLE
        var md_busy = true



        val storageRefAuthorFile = FirebaseStorage.getInstance().reference.child("Posts")
            .child(portalSubPage)
            .child(post_IDParent.toString())
            .child(post_IDChild.toString())
            .child("AuthorPost")
        val name_user= FirebaseAuth.getInstance().uid.toString()
        val inputStream: InputStream = name_user.byteInputStream()
        val uploadAuthorFile = storageRefAuthorFile.putStream(inputStream)


        uploadAuthorFile.addOnCompleteListener { task ->
            if (task.isSuccessful) {

            } else {
                task.exception?.let {
                    Toast.makeText(this, it.message!!, Toast.LENGTH_LONG).show()
                }
            }
        }
        //i=0
        //while (i < counter_images) {
        if(m_text.length>0) {
            val storageRef = FirebaseStorage.getInstance().reference.child("Posts")
                .child(portalSubPage)
                .child(post_IDParent.toString())
                .child(post_IDChild.toString())
                .child("Thumbnails")
                .child(m_text)
            val upload = storageRef.putBytes(imageThumbnail[i])
            val storageRefBig =
                FirebaseStorage.getInstance().reference.child("Posts")
                    .child(portalSubPage)
                    .child(post_IDParent.toString())
                    .child(post_IDChild.toString())
                    .child("BigPictures")
                    .child(m_text)
            val uploadBigPicture = storageRefBig.putBytes(imageBigPicture[i])
            upload.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    uploadBigPicture.addOnCompleteListener { task2 ->
                        //Progress_bar_new_thread.visibility = View.GONE
                        if (task2.isSuccessful) {
                                    SendPictureLinkToPost(
                                        task2.result?.storage?.name,
                                        "Thumbnails")
                                    SendPictureLinkToPost(
                                        task2.result?.storage?.name,
                                        "BigPictures")
                        }
                        //Toast.makeText(applicationContext,m_text[i],Toast.LENGTH_LONG).show()
                            // SendPictureLinkToPost(generatePostID,"BigPicture",m_text[i])
                        else {
                            task.exception?.let {
                                Toast.makeText(this, it.message!!, Toast.LENGTH_LONG).show()
                            }
                        }

                    }
                } else {

                }
            }
        }
        // i++
        //}

        SaveDataWithAvatarLinkToThread()
        //SaveTextDataToPost()
        //val ordersRef = database.child("Users").orderByKey().equalTo(FirebaseAuth.getInstance().uid.toString())
        //val valueEventListener = object : ValueEventListener {
        //   override fun onDataChange(dataSnapshot: DataSnapshot) {
        //   for (ds in dataSnapshot.children)
        //    {
        //       this@EditPostActivity.name =ds.child("name").getValue(String::class.java).toString()

        //   }
        //}

        // override fun onCancelled(databaseError: DatabaseError) {
        //    Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
        // }

        //}
        //ordersRef.addListenerForSingleValueEvent(valueEventListener)
        counter_images=0
        // m_text=""
        //   imageThumbnail.clear()
        //  imageBigPicture.clear()
        Toast.makeText(
            applicationContext,
            getString(R.string.new_thread_toast_edit_thread),
            Toast.LENGTH_SHORT
        ).show()
        Progress_bar_edit_thread.visibility = View.GONE


    }
    fun SaveTextDataToPost(imageURL: String)
    {
        Et_edit_thread_description.text.clear()
        TV_picture_names_edit_post.setText("")
        FirebaseDatabase.getInstance().getReference("Posts")
            .child(portalSubPage)
            .child(post_IDParent.toString())
            .child(post_IDChild.toString())
            .child("description")
            .setValue((getString(R.string.edited) + " " + Et_edit_thread_description.text.toString()))
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                //    val gotoScreenVar = Intent(this, StartForumActivity::class.java)
                 //   gotoScreenVar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                  //  startActivity(gotoScreenVar)
                } else {
                    Toast.makeText(this, getString(R.string.new_thread_error), Toast.LENGTH_LONG).show();
                }
            }
        FirebaseDatabase.getInstance().getReference("Answers")
            .child(post_IDChild.toString())
            .setValue(post_IDParent.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                } else {
                    Toast.makeText(this, getString(R.string.new_thread_error), Toast.LENGTH_LONG).show();
                }
            }
        FirebaseDatabase.getInstance().getReference("Posts")
            .child(portalSubPage)
            .child(post_IDParent.toString())
            .child(post_IDChild.toString())
            .child("Thumbnails")
            .setValue("abcdef")
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //    val gotoScreenVar = Intent(this, StartForumActivity::class.java)
                    //   gotoScreenVar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    //  startActivity(gotoScreenVar)
                } else {
                    Toast.makeText(this, getString(R.string.new_thread_error), Toast.LENGTH_LONG).show();
                }
            }
        FirebaseDatabase.getInstance().getReference("Posts")
            .child(portalSubPage)
            .child(post_IDParent.toString())
            .child(post_IDChild.toString())
            .child("BigPictures")
            .setValue(imageURL)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //    val gotoScreenVar = Intent(this, StartForumActivity::class.java)
                    //   gotoScreenVar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    //  startActivity(gotoScreenVar)
                } else {
                    Toast.makeText(this, getString(R.string.new_thread_error), Toast.LENGTH_LONG).show();
                }
            }



    }

    fun SaveTextDataToPost()
    {
        TV_picture_names_edit_post.setText("")
        Et_edit_thread_description.text.clear()
        FirebaseDatabase.getInstance().getReference("Posts")
            .child(portalSubPage)
            .child(post_IDParent.toString())
            .child(post_IDChild.toString())
            .child("description")
            .setValue((getString(R.string.edited) + " " + Et_edit_thread_description.text.toString()))
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                } else {
                    Toast.makeText(this, getString(R.string.new_thread_error), Toast.LENGTH_LONG).show();
                }
            }
        FirebaseDatabase.getInstance().getReference("Answers")
            .child(post_IDChild.toString())
            .setValue(post_IDParent.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                } else {
                    Toast.makeText(this, getString(R.string.new_thread_error), Toast.LENGTH_LONG).show();
                }
            }
    }
    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == EditPostActivity.IMAGE_PICK_CODE) {
            val imageUrii = data?.data as Uri
            val imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUrii)
            val baos = ByteArrayOutputStream()
            var x=0
            var y=0
            x=imageBitmap.width
            y=imageBitmap.height
            if(x>y)
            {
                val resizedBitmap: Bitmap? = getResizedBitmap(imageBitmap, 800, 600) //96 128
                resizedBitmap?.compress(Bitmap.CompressFormat.PNG, 100, baos)
                imageThumbnail.add(baos.toByteArray())
                val imageBitmapBig = MediaStore.Images.Media.getBitmap(
                    this.contentResolver,
                    imageUrii
                )
                val baosBig = ByteArrayOutputStream()
                val resizedBitmapBig: Bitmap? = getResizedBitmap(imageBitmapBig, 800, 600) //96 128
                resizedBitmapBig?.compress(Bitmap.CompressFormat.PNG, 100, baosBig)
                //resizedBitmapBig?.compress(Bitmap.CompressFormat.PNG, 100, baosBig)
                imageBigPicture.add(baosBig.toByteArray())

            }
            else
            {
                val resizedBitmap: Bitmap? = getResizedBitmap(imageBitmap, 780, 1040) //96 128
                resizedBitmap?.compress(Bitmap.CompressFormat.PNG, 100, baos)
                imageThumbnail.add(baos.toByteArray())
                val imageBitmapBig = MediaStore.Images.Media.getBitmap(
                    this.contentResolver,
                    imageUrii
                )
                val baosBig = ByteArrayOutputStream()
                val resizedBitmapBig: Bitmap? = getResizedBitmap(imageBitmapBig, 780, 1040) //96 128
                resizedBitmapBig?.compress(Bitmap.CompressFormat.PNG, 100, baosBig)
                //resizedBitmapBig?.compress(Bitmap.CompressFormat.PNG, 100, baosBig)
                imageBigPicture.add(baosBig.toByteArray())
            }
            TV_picture_names_edit_post.setText("")

            TV_picture_names_edit_post.text=m_text+ ".png"
            Toast.makeText(
                applicationContext,
                getString(R.string.new_thread_toast_add_picture),
                Toast.LENGTH_SHORT
            ).show()
        }
        else
        {
            m_text=""
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
    fun md5(input: String): String
    {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
    fun CheckMD5Busy(md: String):Boolean
    {
        var md_busy=false
        val database = Firebase.database.reference
        val ordersRef = database.child("Posts").orderByValue()
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {

                    if (ds.child("name").getValue(String::class.java).toString().equals(md))
                    {
                        md_busy=true
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
            }
        }
        ordersRef.addListenerForSingleValueEvent(valueEventListener)
        return md_busy
    }
    fun CheckMD5PictureBusy(md: String):Boolean
    {
        var md_busy=false
        val database = Firebase.database.reference
        val ordersRef = database.child("Posts")
            .child(portalSubPage)
            .child(post_IDParent.toString())
            .child(post_IDChild.toString())
            .child("images")
            .child("Thumbnails")
            .orderByKey()
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {

                    Toast.makeText(applicationContext, ds.key.toString(), Toast.LENGTH_SHORT).show()
                    if (ds.key.toString().equals(md))
                    {
                        md_busy=true
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
            }
        }
        ordersRef.addListenerForSingleValueEvent(valueEventListener)
        return md_busy
    }
    fun MakeIncrementID():String
    {
        var generatePictureID="0"
        var md_busy = true
        while (md_busy == true) {
            generatePictureID=(generatePictureID.toInt()+1).toString()
            md_busy = CheckMD5PictureBusy(generatePictureID)
        }
        return generatePictureID
    }
}