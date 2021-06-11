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
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_forum_thread.*
import kotlinx.android.synthetic.main.activity_forum_thread.rv
import kotlinx.android.synthetic.main.activity_market_thread.*
import kotlinx.android.synthetic.main.activity_start_forum.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.math.BigInteger
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class MarketThreadActivity : AppCompatActivity(), ForumThreadsMarketAdapter.OnForumThreadsItemClickListener,ForumThreadsMarketAdapter.OnForumThreadsItemClickListener2
{
        val list= ArrayList<Market_List_Thread>()
        var generatePostID = ""
        public var i=0
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
        val TAG="ForumThread TAG"
        var isSubPage: String?="Forum"
        lateinit var database: DatabaseReference
        lateinit var appSettingsPrefs: SharedPreferences
        var user_ID: String?=null
        var post_ID: String?=null
        var titleThread: String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_market_thread)
        appSettingsPrefs = getSharedPreferences(
            "AppSettingPrefs",
            Context.MODE_PRIVATE)
        val isNightModeOn:Boolean= appSettingsPrefs.getBoolean("NightMode", true)
        if(isNightModeOn)
        {
            btn_add_image_market.setBackgroundResource(R.drawable.picture_bright)
        }
        else
        {
            btn_add_image_market.setBackgroundResource(R.drawable.picture)
        }
        isSubPage= appSettingsPrefs.getString("PortalSubPage", null)
        portalSubPage = appSettingsPrefs.getString("PortalSubPage", "").toString()
        database = Firebase.database.reference
        Progress_bar_market_thread.visibility= View.VISIBLE
        user_ID = intent.getStringExtra("USER_ID").toString()
        post_ID = intent.getStringExtra("POST_ID").toString()
        titleThread = intent.getStringExtra("TITLE").toString()


        val ordersRef = database.child("Posts").child(isSubPage.toString()).orderByKey().equalTo(post_ID)
        val valueEventListener = object : ValueEventListener {
            @SuppressLint("WrongConstant")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var i=0
                for (ds in dataSnapshot.children) {

                    var imageURLThumbnail :String?=null
                    var imageURLBigPicture:String?=null
                    var avatarURL :String?= null
                    val title=getString(R.string.title)+": "+ds.child("title").getValue(String::class.java)
                    val date = ds.child("date").getValue(String::class.java)
                    val author = getString(R.string.author)+": "+ds.child("name").getValue(String::class.java)
                    val user_id = ds.child("user_id").getValue(String::class.java)
                    val description = ds.child("description").getValue(String::class.java)
                    val prize = ds.child("prize").getValue(String::class.java)
                    val contact = ds.child("contact").getValue(String::class.java)
                    if(ds.child("avatar").getValue(String::class.java)!=null)
                    {
                        avatarURL = ds.child("avatar").getValue(String::class.java)
                    }
                    if(ds.child("BigPictures").getValue(String::class.java)!=null)
                    {
                        imageURLThumbnail = ds.child("Thumbnails").getValue(String::class.java)
                    }
                    if(ds.child("Thumbnails").getValue(String::class.java)!=null)
                    {
                        imageURLBigPicture = ds.child("BigPictures").getValue(String::class.java)
                    }
                    val post_id =ds.key.toString()
                    list.add(i, Market_List_Thread(post_id,user_id,title, author,date,description,avatarURL,imageURLThumbnail,imageURLBigPicture,prize,contact))
                    i++

                    val ordersRef3 = database.child("Posts").child(isSubPage.toString()).child(post_ID.toString()).orderByKey()
                    val valueEventListener3 = object : ValueEventListener {
                        @SuppressLint("WrongConstant")
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (ds2 in dataSnapshot.children) {
                                if(ds2.key.toString().length==20) {
                                    var imageURLThumbnail2: String? = null
                                    var imageURLBigPicture2: String? = null
                                    var avatarURL2: String? = null
                                    val title2 = ""
                                    val date2 = ds2.child("date").getValue(String::class.java)
                                    val author2 = ds2.child("name").getValue(String::class.java)
                                    val user_id2 = ds2.child("user_id").getValue(String::class.java)
                                    val description2 =
                                        ds2.child("description").getValue(String::class.java)
                                    if (ds2.child("avatar").getValue(String::class.java) != null) {
                                        avatarURL2 = ds2.child("avatar").getValue(String::class.java)
                                    }
                                    if (ds2.child("BigPictures")
                                            .getValue(String::class.java) != null
                                    ) {
                                        imageURLThumbnail2 = ds2.child("Thumbnails").getValue(String::class.java)
                                    }
                                    if (ds2.child("Thumbnails").getValue(String::class.java) != null
                                    ) {
                                        imageURLBigPicture2 = ds2.child("BigPictures").getValue(String::class.java)
                                    }
                                    val post_id2 = ds.key.toString()

                                    list.add(
                                        Market_List_Thread(
                                            post_id2,
                                            user_id2,
                                            title2,
                                            author2,
                                            date2,
                                            description2,
                                            avatarURL2,
                                            imageURLThumbnail2,
                                            imageURLBigPicture2,
                                            "",""
                                        )
                                    )
                                    i++
                                }

                            }
                            list.sortBy{it.date}
                            val adapter= ForumThreadsMarketAdapter(applicationContext,list,this@MarketThreadActivity,this@MarketThreadActivity)
                            rv.layoutManager= LinearLayoutManager(
                                applicationContext,
                                LinearLayout.VERTICAL,
                                false
                            ) as RecyclerView.LayoutManager
                            rv.adapter= adapter
                            Progress_bar_market_thread.visibility= View.GONE
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
                            Progress_bar_market_thread.visibility= View.GONE
                        }
                    }
                    ordersRef3.addListenerForSingleValueEvent(valueEventListener3)
                }
                Progress_bar_market_thread.visibility= View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
                Progress_bar_market_thread.visibility= View.GONE
            }
        }
        ordersRef.addListenerForSingleValueEvent(valueEventListener)
    }

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
                            requestPermissions(permissions, MarketThreadActivity.PERMISSION_CODE);
                        } else {
                            //permission already granted
                            val intent = Intent(Intent.ACTION_PICK)
                            intent.type = "image/*"
                            startActivityForResult(intent, MarketThreadActivity.IMAGE_PICK_CODE)
                        }
                    } else {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        startActivityForResult(intent, MarketThreadActivity.IMAGE_PICK_CODE)
                    }
                    //m_text[counter_images] = input.text.toString()
                    m_text=input.text.toString()
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


    fun SendPictureLinkToPost(fileName: String?,pictureSize: String,generatePictureID: String)
    {

        // Toast.makeText(applicationContext,portalSubPage+"\n"+generatePostID+"\n"+pictureSize+"\n"+fileName,Toast.LENGTH_LONG).show()
        val storageRef= Firebase.storage.reference.child("Posts")
            .child(portalSubPage)
            .child(post_ID.toString())
            .child(generatePostID)
            .child(pictureSize)
            .child(fileName.toString())
        storageRef.downloadUrl.addOnSuccessListener { task ->
            val imageURL = task.toString()
            SendPictureLinkToPostPart2(imageURL,pictureSize,generatePictureID)
//            Toast.makeText(applicationContext,imageURL,Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            // Handle any errors
        }

    }

    fun SendPictureLinkToPostPart2( imageURL: String,pictureSize: String,generatePictureID:String)
    {

        //  Toast.makeText(applicationContext,pictureSize +"\n"+generatePostID,Toast.LENGTH_LONG).show()
        FirebaseDatabase.getInstance().getReference("Posts")
            .child(portalSubPage)
            .child(post_ID.toString())
            .child(generatePostID)
            .child(pictureSize)
            .setValue(imageURL)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                }
                else {
                    Toast.makeText(applicationContext,"error1", Toast.LENGTH_LONG).show()
                }
            }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            MarketThreadActivity.PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent, MarketThreadActivity.IMAGE_PICK_CODE)

                } else {
                    //permission from popup denied
                    Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT)
                        .show()
                    m_text=""

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



        if (Et_new_post_description_market.text.trim().isEmpty())
        {
            Et_new_post_description_market.setError(getString(R.string.error_empty_description))
            Et_new_post_description_market.requestFocus()
            return
        }
        Progress_bar_market_thread.visibility = View.VISIBLE
        var md_busy = true
        while (md_busy == true) {
            val STRING_LENGTH = 20;
            val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
            generatePostID = (1..STRING_LENGTH)
                .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("");
            md_busy = CheckMD5Busy(generatePostID)
        }
        val storageRefAuthorFile = FirebaseStorage.getInstance().reference.child("Posts")
            .child(portalSubPage)
            .child(post_ID.toString())
            .child(generatePostID)
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
                .child(post_ID.toString())
                .child(generatePostID)
                .child("Thumbnails")
                .child(m_text)
            val upload = storageRef.putBytes(imageThumbnail[i])
            val storageRefBig =
                FirebaseStorage.getInstance().reference.child("Posts")
                    .child(portalSubPage)
                    .child(post_ID.toString())
                    .child(generatePostID)
                    .child("BigPictures")
                    .child(m_text)
            val uploadBigPicture = storageRefBig.putBytes(imageBigPicture[i])
            upload.addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    uploadBigPicture.addOnCompleteListener { task2 ->
                        //Progress_bar_new_thread.visibility = View.GONE
                        if (task2.isSuccessful) {
                            var pictureID = MakeIncrementID()

                            GlobalScope.async(Dispatchers.IO) {
                                val val1 = async {
                                    SendPictureLinkToPost(
                                        task.result?.storage?.name,
                                        "Thumbnails",
                                        pictureID
                                    )
                                }
                                val val2 = async {
                                    SendPictureLinkToPost(
                                        task.result?.storage?.name,
                                        "BigPictures",
                                        pictureID
                                    )
                                }

                            }



                            //Toast.makeText(applicationContext,m_text[i],Toast.LENGTH_LONG).show()
                            // SendPictureLinkToPost(generatePostID,"BigPicture",m_text[i])
                        } else {
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
        //       this@MarketThreadActivity.name =ds.child("name").getValue(String::class.java).toString()

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
            getString(R.string.new_thread_toast_add_thread),
            Toast.LENGTH_SHORT
        ).show()
        Progress_bar_market_thread.visibility = View.GONE
        finish();
        startActivity(getIntent());
    }
    fun SaveTextDataToPost(imageURL:String)
    {
        val name=appSettingsPrefs.getString("username", "")
        val sdf = SimpleDateFormat("dd/M/yyyy HH:mm:ss")
        val currentDate = sdf.format(Date())
        val description = Et_new_post_description_market.text.toString()
        FirebaseDatabase.getInstance().getReference("Posts")
            .child(portalSubPage)
            .child(post_ID.toString())
            .child(generatePostID)
            .setValue(
                Post(
                    FirebaseAuth.getInstance().uid.toString(),
                    name,
                    titleThread,
                    description,
                    currentDate,
                    imageURL
                )
            )
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Et_new_post_description_market.text.clear()
                    TV_picture_names_post_market.setText("")
                    this.finish()
                    startActivity(getIntent())

                } else {
                    Toast.makeText(this, getString(R.string.new_thread_error), Toast.LENGTH_LONG).show();
                }
            }
    }

    fun SaveTextDataToPost()
    {
        var name=appSettingsPrefs.getString("username", "")
        //var name="Hubert_Krupa"
        val sdf = SimpleDateFormat("dd/M/yyyy HH:mm:ss")
        val currentDate = sdf.format(Date())
        val description = Et_new_post_description_market.text.toString()
        FirebaseDatabase.getInstance().getReference("Posts")
            .child(portalSubPage)
            .child(post_ID.toString())
            .child(generatePostID)
            .setValue(
                Post(
                    FirebaseAuth.getInstance().uid.toString(),
                    name,
                    titleThread,
                    description,
                    currentDate
                )
            )
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    Et_new_post_description_market.text.clear()
                    TV_picture_names_post_market.setText("")
                    this.finish()
                    startActivity(getIntent())
                } else {
                    Toast.makeText(this, getString(R.string.new_thread_error), Toast.LENGTH_LONG).show();
                }
            }
    }
    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == MarketThreadActivity.IMAGE_PICK_CODE) {
            val imageUrii = data?.data as Uri
            val imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUrii)
            val baos = ByteArrayOutputStream()
            val resizedBitmap: Bitmap? = getResizedBitmap(imageBitmap, 220, 260) //96 128
            resizedBitmap?.compress(Bitmap.CompressFormat.PNG, 100, baos)
            imageThumbnail.add(baos.toByteArray())
            val imageBitmapBig = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUrii)
            val baosBig = ByteArrayOutputStream()
            val resizedBitmapBig: Bitmap? = getResizedBitmap(imageBitmapBig, 780, 1040)
            resizedBitmapBig?.compress(Bitmap.CompressFormat.PNG, 100, baosBig)
            imageBigPicture.add(baosBig.toByteArray())
            TV_picture_names_post_market.setText("")

            TV_picture_names_post_market.text=m_text+ ".png"
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
            .child(post_ID.toString())
            .child(generatePostID)
            .child("images")
            .child("Thumbnails")
            .orderByKey()
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {

                    Toast.makeText(applicationContext,ds.key.toString(), Toast.LENGTH_SHORT).show()
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

    fun showAlertDialogButtonClicked(forum_list: Market_List_Thread, position: Int) {

        // setup the alert builder
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.choose_one_of_the_options))

        // add a list
        val arrayDialog = arrayOf(
            getString(R.string.show_big_picture), getString(R.string.show_user_profile),
            getString(R.string.edit_post_message), getString(R.string.delete_post_message)
        )
        builder.setItems(
            arrayDialog
        ) { dialog, which ->
            when (which) {
                0 -> {
                    intent = Intent(applicationContext, BigPictureActivity::class.java)
                    intent.putExtra("BigPictureFromThread", forum_list.imageBigPicture)
                    startActivity(intent)

                }
                1 -> {
                    intent = Intent(applicationContext, UserSettingsShowActivity::class.java)
                    intent.putExtra("USER_ID", forum_list.user_id)
                    startActivity(intent)

                }
                2 -> {
                    if (position > 0) {
                        if (forum_list.user_id.equals(FirebaseAuth.getInstance().uid.toString())) {
                            intent = Intent(applicationContext, EditPostActivity::class.java)
                            intent.putExtra("USER_ID", forum_list.user_id)
                            intent.putExtra("POST_ID_PARENT", post_ID.toString())
                            intent.putExtra("POST_ID_CHILD", forum_list.post_id)
                            intent.putExtra("POST_DESCRIPTION", forum_list.description)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                applicationContext, getString(R.string.foreign_post_author),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            applicationContext, getString(R.string.not_permission_to_update_thread),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }
                3 -> {
                    if (position > 0) {
                        if (forum_list.user_id.equals(FirebaseAuth.getInstance().uid.toString())) {
                            val deleteRef = FirebaseDatabase.getInstance().getReference("Posts")
                                .child(portalSubPage).child(post_ID.toString())
                                .child(forum_list.post_id.toString())
                            deleteRef.removeValue()
                            this.finish()
                            startActivity(getIntent())

                        } else {
                            Toast.makeText(
                                applicationContext, getString(R.string.foreign_post_author),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            applicationContext, getString(R.string.not_permission_to_delete_thread),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        // create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }

    override fun OnItemClick(forum_list: Market_List_Thread, position: Int) {

    }
    override fun OnItemClick2(forum_list: Market_List_Thread, position: Int) {
        showAlertDialogButtonClicked(forum_list, position)


    }
    override fun onResume() {  // After a pause OR at startup
        super.onResume()
        //Refresh your stuff here
    }

}