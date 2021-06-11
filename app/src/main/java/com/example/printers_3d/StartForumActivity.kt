package com.example.printers_3d

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_start_forum.*
import java.util.*


@Suppress("NAME_SHADOWING")
class StartForumActivity : AppCompatActivity(), ForumAdapter.OnForumItemClickListener {
    val TAG="StartForum TAG"
    var isSubPage: String?="Forum"
    lateinit var database: DatabaseReference
    var notification: NotificationCompat.Builder? = null
    var notificationChannel: NotificationChannel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_forum)
        database = Firebase.database.reference

        var appSettingsPrefs: SharedPreferences = getSharedPreferences(
            "AppSettingPrefs",
            Context.MODE_PRIVATE
        )
        val isNightModeOn:Boolean= appSettingsPrefs.getBoolean("NightMode", true)
        isSubPage= appSettingsPrefs.getString("PortalSubPage", null)
        if(isSubPage.equals("Forum"))
        {
            txt_forum_title.setText(getString(R.string.start_forum_title))

        }
        else if (isSubPage.equals("Market"))
        {
            txt_forum_title.setText(getString(R.string.start_market_title))
        }
        else if (isSubPage.equals("Projects"))
        {
            txt_forum_title.setText(getString(R.string.start_projects_title))
        }
        else
        {
            txt_forum_title.setText(getString(R.string.portal))
        }

        if(isNightModeOn)
        {
            btn_forum.setBackgroundResource(R.drawable.forum_bright)
            btn_market.setBackgroundResource(R.drawable.market_bright)
            btn_projects.setBackgroundResource(R.drawable.tools_bright)
            btn_new_thread.setBackgroundResource(R.drawable.thread_bright)
            btn_loupe.setBackgroundResource(R.drawable.loupe_bright)

        }
        else
        {
            btn_forum.setBackgroundResource(R.drawable.forum)
            btn_market.setBackgroundResource(R.drawable.market)
            btn_projects.setBackgroundResource(R.drawable.tools)
            btn_new_thread.setBackgroundResource(R.drawable.thread)
            btn_loupe.setBackgroundResource(R.drawable.loupe)
        }


    }

    fun Browser(view: View)
    {
        val intent = Intent(this, StartForumActivity::class.java)
        var input = EditText(applicationContext)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.browse_dialog_message))

        // add a list
        val arrayDialog = arrayOf(
            getString(R.string.title_in_threads), getString(R.string.author_in_threads),
            getString(R.string.description_in_threads), getString(R.string.author_in_posts),
            getString(R.string.description_in_posts), getString(R.string.undo_browse)
        )
        builder.setItems(
            arrayDialog
        ) { dialog, which ->
            when (which) {

                0 -> {
                    intent.putExtra("BROWSE_TITLE", input.text.toString())
                    intent.putExtra(
                        "BROWSER_INFO", (getString(R.string.browser) + ": "
                                + getString(R.string.title_in_threads)) + ": " + input.text.toString()
                    )
                    getIntent().removeExtra("BROWSE_AUTHOR")
                    getIntent().removeExtra("BROWSE_DESCRIPTION")
                    //   getIntent().removeExtra("BROWSE_TITLE_IN_POSTS")
                    getIntent().removeExtra("BROWSE_AUTHOR_IN_POSTS")
                    getIntent().removeExtra("BROWSE_DESCRIPTION_IN_POSTS")

                    finish()
                    startActivity(intent)

                }
                1 -> {
                    intent.putExtra("BROWSE_AUTHOR", input.text.toString())
                    intent.putExtra(
                        "BROWSER_INFO", (getString(R.string.browser) + ": "
                                + getString(R.string.author_in_threads)) + ": " + input.text.toString()
                    )
                    getIntent().removeExtra("BROWSE_TITLE")
                    getIntent().removeExtra("BROWSE_DESCRIPTION")
                    //   getIntent().removeExtra("BROWSE_TITLE_IN_POSTS")
                    getIntent().removeExtra("BROWSE_AUTHOR_IN_POSTS")
                    getIntent().removeExtra("BROWSE_DESCRIPTION_IN_POSTS")
                    finish()
                    startActivity(intent)

                }
                2 -> {
                    intent.putExtra("BROWSE_DESCRIPTION", input.text.toString())
                    intent.putExtra(
                        "BROWSER_INFO", (getString(R.string.browser) + ": "
                                + getString(R.string.description_in_threads)) + ": " + input.text.toString()
                    )
                    getIntent().removeExtra("BROWSE_TITLE");
                    getIntent().removeExtra("BROWSE_AUTHOR");
                    //    getIntent().removeExtra("BROWSE_TITLE_IN_POSTS");
                    getIntent().removeExtra("BROWSE_AUTHOR_IN_POSTS");
                    getIntent().removeExtra("BROWSE_DESCRIPTION_IN_POSTS");
                    finish()
                    startActivity(intent)
                }

                3 -> {
                    intent.putExtra("BROWSE_AUTHOR_IN_POSTS", input.text.toString())
                    intent.putExtra(
                        "BROWSER_INFO", (getString(R.string.browser) + ": "
                                + getString(R.string.author_in_posts)) + ": " + input.text.toString()
                    )
                    getIntent().removeExtra("BROWSE_TITLE");
                    getIntent().removeExtra("BROWSE_AUTHOR");
                    getIntent().removeExtra("BROWSE_DESCRIPTION");
                    //    getIntent().removeExtra("BROWSE_TITLE_IN_POSTS");
                    getIntent().removeExtra("BROWSE_DESCRIPTION_IN_POSTS");
                    finish()
                    startActivity(intent)
                }
                4 -> {
                    intent.putExtra("BROWSE_DESCRIPTION_IN_POSTS", input.text.toString())
                    intent.putExtra(
                        "BROWSER_INFO", (getString(R.string.browser) + ": "
                                + getString(R.string.description_in_posts)) + ": " + input.text.toString()
                    )
                    getIntent().removeExtra("BROWSE_TITLE");
                    getIntent().removeExtra("BROWSE_AUTHOR");
                    getIntent().removeExtra("BROWSE_DESCRIPTION");
                    //   getIntent().removeExtra("BROWSE_TITLE_IN_POSTS");
                    getIntent().removeExtra("BROWSE_AUTHOR_IN_POSTS");
                    finish()
                    startActivity(intent)

                }
                5 -> {
                    intent.putExtra("BROWSER_INFO", " ")
                    intent.removeExtra("BROWSE_TITLE")
                    intent.removeExtra("BROWSE_AUTHOR")
                    intent.removeExtra("BROWSE_DESCRIPTION")
                    intent.removeExtra("BROWSE_TITLE_IN_POSTS");
                    intent.removeExtra("BROWSE_AUTHOR_IN_POSTS")
                    intent.removeExtra("BROWSE_DESCRIPTION_IN_POSTS")
                    finish()
                    startActivity(intent)
                }

            }
        }
     //   val builder: AlertDialog.Builder = AlertDialog.Builder(this)
       input.setHint(getString(R.string.enter_text))
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
       /* builder.setMessage(getString(R.string.browse_dialog_message)).
        setPositiveButton(
            getString(R.string.title),
            dialogClickListener)
            .setNegativeButton(
                getString(R.string.author),
                dialogClickListener
            ).setNeutralButton(
            getString(R.string.description),
            dialogClickListener
        ).show()*/
        val dialog = builder.create()
        dialog.show()
    }

    fun Forum(view: View)
    {
        var appSettingsPrefs: SharedPreferences = getSharedPreferences(
            "AppSettingPrefs",
            Context.MODE_PRIVATE
        )
        var sharedPrefsEdit: SharedPreferences.Editor= appSettingsPrefs.edit()
        sharedPrefsEdit.putString("PortalSubPage", "Forum")
        sharedPrefsEdit.apply()
        btn_forum.background.alpha=100
        btn_market.background.alpha=255
        btn_projects.background.alpha=255
        finish();
        startActivity(getIntent());
    }
    fun Market(view: View)
    {
        var appSettingsPrefs: SharedPreferences = getSharedPreferences(
            "AppSettingPrefs",
            Context.MODE_PRIVATE
        )
        var sharedPrefsEdit: SharedPreferences.Editor = appSettingsPrefs.edit()
        sharedPrefsEdit.putString("PortalSubPage", "Market")
        sharedPrefsEdit.apply()
        finish();
        startActivity(getIntent());
        btn_market.background.alpha=100
        btn_forum.background.alpha=255
        btn_projects.background.alpha=255

    }
    fun Projects(view: View)
    {
        var appSettingsPrefs: SharedPreferences = getSharedPreferences(
            "AppSettingPrefs",
            Context.MODE_PRIVATE
        )
        var sharedPrefsEdit: SharedPreferences.Editor= appSettingsPrefs.edit()
        btn_projects.background.alpha=100
        btn_market.background.alpha=255
        btn_forum.background.alpha=255
        sharedPrefsEdit.putString("PortalSubPage", "Projects")
        sharedPrefsEdit.apply()
        finish()
        startActivity(getIntent())
    }
    fun NewThread(view: View)
    {
        if(isSubPage.equals("Forum"))
        {
            val intent = Intent(this, NewThreadActivity::class.java)
            startActivity(intent)
        }
        else if(isSubPage.equals("Market"))
        {
            val intent = Intent(this, NewThreadMarketActivity::class.java)
            startActivity(intent)
        }
        else if(isSubPage.equals("Projects"))
        {
            val intent = Intent(this, NewThreadProjectsActivity::class.java)
            startActivity(intent)
        }

    }


    @SuppressLint("WrongConstant")
    private fun viewForum()
    {
        val extras = intent.extras
        if(extras?.containsKey("BROWSER_INFO")==true)
        {
            txt_start_forum_browse_information.setText(extras.getString("BROWSER_INFO").toString())
        }
        Progress_bar_start_forum.visibility=View.VISIBLE
        val list= ArrayList<Forum_List>()
        val ordersRef = database.child("Posts").child(isSubPage.toString()).orderByKey()
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var i=0;
                for (ds in dataSnapshot.children) {
                    val post_id = ds.key.toString()
                    var imageURL: String? = null
                    if (extras?.containsKey("BROWSE_TITLE") == true) {
                        if (ds.child("title").getValue(String::class.java).toString().toLowerCase(
                                Locale.ROOT
                            )
                                .contains(
                                    extras?.getString("BROWSE_TITLE").toString().toLowerCase(
                                        Locale.ROOT
                                    )
                                )
                        ) {

                            var title = ds.child("title").getValue(String::class.java)
                            if (title?.length!! > 25) {
                                title = title.substring(0, 25) + "..."
                            }
                            val date = ds.child("date").getValue(String::class.java)
                            val author = ds.child("name").getValue(String::class.java)
                            val user_id = ds.child("user_id").getValue(String::class.java)
                            if (ds.child("avatar").getValue(String::class.java) != null) {
                                imageURL = ds.child("avatar").getValue(String::class.java)
                            }
                            val post_id = ds.key.toString()
                            list.add(i, Forum_List(post_id, user_id, title, author, date, imageURL))
                            i++
                        }
                    }
                    else if (extras?.containsKey("BROWSE_AUTHOR") == true) {
                        if (ds.child("name").getValue(String::class.java).toString().toLowerCase(
                                Locale.ROOT
                            )
                                .contains(
                                    extras?.getString("BROWSE_AUTHOR").toString().toLowerCase(
                                        Locale.ROOT
                                    )
                                )
                        ) {

                            var title = ds.child("title").getValue(String::class.java)
                            if (title?.length!! > 20) {
                                title = title.substring(0, 20) + "..."
                            }
                            val date = ds.child("date").getValue(String::class.java)
                            val author = ds.child("name").getValue(String::class.java)
                            val user_id = ds.child("user_id").getValue(String::class.java)
                            if (ds.child("avatar").getValue(String::class.java) != null) {
                                imageURL = ds.child("avatar").getValue(String::class.java)
                            }
                            val post_id = ds.key.toString()
                            list.add(i, Forum_List(post_id, user_id, title, author, date, imageURL))
                            i++
                        }
                    }
                    else if (extras?.containsKey("BROWSE_DESCRIPTION") == true) {
                        if (ds.child("description").getValue(String::class.java).toString().toLowerCase(
                                Locale.ROOT
                            )
                                .contains(
                                    extras?.getString("BROWSE_DESCRIPTION").toString().toLowerCase(
                                        Locale.ROOT
                                    )
                                )
                        ) {

                            var title = ds.child("title").getValue(String::class.java)
                            if (title?.length!! > 20) {
                                title = title.substring(0, 20) + "..."
                            }
                            val date = ds.child("date").getValue(String::class.java)
                            val author = ds.child("name").getValue(String::class.java)
                            val user_id = ds.child("user_id").getValue(String::class.java)
                            if (ds.child("avatar").getValue(String::class.java) != null) {
                                imageURL = ds.child("avatar").getValue(String::class.java)
                            }
                            val post_id = ds.key.toString()
                            list.add(i, Forum_List(post_id, user_id, title, author, date, imageURL))
                            i++
                        }
                    }
                    else if (extras?.containsKey("BROWSE_TITLE_IN_POSTS") == true) {

                        if (ds.child("title").getValue(String::class.java).toString().toLowerCase(
                                Locale.ROOT
                            )
                                .contains(
                                    extras?.getString("BROWSE_TITLE_IN_POSTS").toString()
                                        .toLowerCase(
                                            Locale.ROOT
                                        )
                                )
                        ) {
                            val post_id = ds.key.toString()
                            var title = ds.child("title").getValue(String::class.java)
                            if (title?.length!! > 25) {
                                title = title.substring(0, 25) + "..."
                            }
                            val date = ds.child("date").getValue(String::class.java)
                            val author = ds.child("name").getValue(String::class.java)
                            val user_id = ds.child("user_id").getValue(String::class.java)
                            if (ds.child("avatar").getValue(String::class.java) != null) {
                                imageURL = ds.child("avatar").getValue(String::class.java)
                            }

                            list.add(
                                i,
                                Forum_List(post_id, user_id, title, author, date, imageURL)
                            )
                            i++

                        } else{

                            val ordersRef0 = database.child("Posts").child(isSubPage.toString()).child(
                                post_id
                            ).orderByKey()
                            val valueEventListener0 = object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    var i = 0;
                                    for (ds2 in dataSnapshot.children) {


                                        if (ds2.child("title")
                                                .getValue(String::class.java).toString()
                                                .toLowerCase(
                                                    Locale.ROOT
                                                )
                                                .contains(
                                                    extras?.getString("BROWSE_TITLE_IN_POSTS")
                                                        .toString()
                                                        .toLowerCase(
                                                            Locale.ROOT
                                                        )
                                                )
                                        ) {
                                            val post_id = ds2.key.toString()
                                            var title =
                                                ds2.child("title").getValue(String::class.java)
                                            if (title?.length!! > 25) {
                                                title = title?.substring(0, 25) + "..."
                                            }
                                            val date =
                                                ds2.child("date").getValue(String::class.java)
                                            val author =
                                                ds2.child("name").getValue(String::class.java)
                                            val user_id =
                                                ds2.child("user_id").getValue(String::class.java)
                                            if (ds2.child("avatar")
                                                    .getValue(String::class.java) != null
                                            ) {
                                                imageURL =
                                                    ds2.child("avatar").getValue(String::class.java)
                                            }

                                            list.add(
                                                i,
                                                Forum_List(
                                                    post_id,
                                                    user_id,
                                                    title,
                                                    author,
                                                    date,
                                                    imageURL
                                                )
                                            )
                                            i++
                                        }
                                    }
                                }
                                    override fun onCancelled(databaseError: DatabaseError) {
                                        Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
                                        Progress_bar_start_forum.visibility= View.GONE
                                    }
                                }
                                ordersRef0.addListenerForSingleValueEvent(valueEventListener0)
                    }
                    }
                    else if (extras?.containsKey("BROWSE_AUTHOR_IN_POSTS") == true) {

                        if (ds.child("name").getValue(String::class.java).toString().toLowerCase(
                                Locale.ROOT
                            )
                                .contains(
                                    extras?.getString("BROWSE_AUTHOR_IN_POSTS").toString()
                                        .toLowerCase(
                                            Locale.ROOT
                                        )
                                )
                        ) {
                            val post_id = ds.key.toString()
                            var title = ds.child("title").getValue(String::class.java)
                            if (title?.length!! > 25) {
                                title = title?.substring(0, 25) + "..."
                            }
                            val date = ds.child("date").getValue(String::class.java)
                            val author = ds.child("name").getValue(String::class.java)
                            val user_id = ds.child("user_id").getValue(String::class.java)
                            if (ds.child("avatar").getValue(String::class.java) != null) {
                                imageURL = ds.child("avatar").getValue(String::class.java)
                            }

                            list.add(
                                i,
                                Forum_List(post_id, user_id, title, author, date, imageURL)
                            )
                            i++

                        } else{

                            val ordersRef0 = database.child("Posts").child(isSubPage.toString()).child(
                                post_id
                            ).orderByKey()
                            val valueEventListener0 = object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    var i = 0;
                                    for (ds2 in dataSnapshot.children) {


                                        if (ds2.child("name")
                                                .getValue(String::class.java).toString()
                                                .toLowerCase(
                                                    Locale.ROOT
                                                )
                                                .contains(
                                                    extras?.getString("BROWSE_AUTHOR_IN_POSTS")
                                                        .toString()
                                                        .toLowerCase(
                                                            Locale.ROOT
                                                        )
                                                )
                                        ) {
                                            val post_id = ds2.key.toString()
                                            var title =
                                                ds2.child("title").getValue(String::class.java)
                                            if (title?.length!! > 25) {
                                                title = title?.substring(0, 25) + "..."
                                            }
                                            val date =
                                                ds2.child("date").getValue(String::class.java)
                                            val author =
                                                ds2.child("name").getValue(String::class.java)
                                            val user_id =
                                                ds2.child("user_id").getValue(String::class.java)
                                            if (ds2.child("avatar")
                                                    .getValue(String::class.java) != null
                                            ) {
                                                imageURL =
                                                    ds2.child("avatar").getValue(String::class.java)
                                            }

                                            list.add(
                                                i,
                                                Forum_List(
                                                    post_id,
                                                    user_id,
                                                    title,
                                                    author,
                                                    date,
                                                    imageURL
                                                )
                                            )
                                            i++
                                        }
                                    }
                                }
                                override fun onCancelled(databaseError: DatabaseError) {
                                    Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
                                    Progress_bar_start_forum.visibility= View.GONE
                                }
                            }
                            ordersRef0.addListenerForSingleValueEvent(valueEventListener0)
                        }
                    }



                    else if (extras?.containsKey("BROWSE_DESCRIPTION_IN_POSTS") == true) {

                        if (ds.child("description").getValue(String::class.java).toString().toLowerCase(
                                Locale.ROOT
                            )
                                .contains(
                                    extras?.getString("BROWSE_DESCRIPTION_IN_POSTS").toString()
                                        .toLowerCase(
                                            Locale.ROOT
                                        )
                                )
                        ) {
                            val post_id = ds.key.toString()
                            var title = ds.child("title").getValue(String::class.java)
                            if (title?.length!! > 25) {
                                title = title?.substring(0, 25) + "..."
                            }
                            val date = ds.child("date").getValue(String::class.java)
                            val author = ds.child("name").getValue(String::class.java)
                            val user_id = ds.child("user_id").getValue(String::class.java)
                            if (ds.child("avatar").getValue(String::class.java) != null) {
                                imageURL = ds.child("avatar").getValue(String::class.java)
                            }

                            list.add(
                                i,
                                Forum_List(post_id, user_id, title, author, date, imageURL)
                            )
                            i++

                        } else{

                            val ordersRef0 = database.child("Posts").child(isSubPage.toString()).child(
                                post_id
                            ).orderByKey()
                            val valueEventListener0 = object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    var i = 0;
                                    for (ds2 in dataSnapshot.children) {


                                        if (ds2.child("description")
                                                .getValue(String::class.java).toString()
                                                .toLowerCase(
                                                    Locale.ROOT
                                                )
                                                .contains(
                                                    extras?.getString("BROWSE_DESCRIPTION_IN_POSTS")
                                                        .toString()
                                                        .toLowerCase(
                                                            Locale.ROOT
                                                        )
                                                )
                                        ) {
                                            val post_id = ds2.key.toString()
                                            var title =
                                                ds2.child("title").getValue(String::class.java)
                                            if (title?.length!! > 25) {
                                                title = title?.substring(0, 25) + "..."
                                            }
                                            val date =
                                                ds2.child("date").getValue(String::class.java)
                                            val author =
                                                ds2.child("name").getValue(String::class.java)
                                            val user_id =
                                                ds2.child("user_id").getValue(String::class.java)
                                            if (ds2.child("avatar")
                                                    .getValue(String::class.java) != null
                                            ) {
                                                imageURL =
                                                    ds2.child("avatar").getValue(String::class.java)
                                            }

                                            list.add(
                                                i,
                                                Forum_List(
                                                    post_id,
                                                    user_id,
                                                    title,
                                                    author,
                                                    date,
                                                    imageURL
                                                )
                                            )
                                            i++
                                        }
                                    }
                                }
                                override fun onCancelled(databaseError: DatabaseError) {
                                    Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
                                    Progress_bar_start_forum.visibility= View.GONE
                                }
                            }
                            ordersRef0.addListenerForSingleValueEvent(valueEventListener0)
                        }
                    }

                    else if(extras==null || extras?.getString("BROWSER_INFO").equals(" "))
                    {
                        var title = ds.child("title").getValue(String::class.java)
                        if (title?.length!! > 25) {
                            title = title?.substring(0, 25) + "..."
                        }
                        val date = ds.child("date").getValue(String::class.java)
                        val author = ds.child("name").getValue(String::class.java)
                        val user_id = ds.child("user_id").getValue(String::class.java)
                        if (ds.child("avatar").getValue(String::class.java) != null) {
                            imageURL = ds.child("avatar").getValue(String::class.java)
                        }
                        val post_id = ds.key.toString()
                        list.add(i, Forum_List(post_id, user_id, title, author, date, imageURL))
                        i++
                    }

                }

                list.sortByDescending{it.date}
                val adapter= ForumAdapter(applicationContext, list, this@StartForumActivity)
                rv.layoutManager= LinearLayoutManager(
                    applicationContext,
                    LinearLayout.VERTICAL,
                    false
                ) as RecyclerView.LayoutManager
                rv.adapter= adapter
                Progress_bar_start_forum.visibility= View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.getMessage()) //Don't ignore errors!
                Progress_bar_start_forum.visibility= View.GONE
            }
        }
        ordersRef.addListenerForSingleValueEvent(valueEventListener)

    }


    override fun onResume()
    {
        viewForum()
        super.onResume()
    }

    interface MyCallback {
        fun onCallback(value: String?)
    }

    override fun OnItemClick(forum_list: Forum_List, position: Int) {
        //Toast.makeText(applicationContext,forum_list.post_id,Toast.LENGTH_LONG).show()
        var intent= Intent(applicationContext, ForumThreadActivity::class.java)
        if(isSubPage=="Forum")
        {
            intent = Intent(applicationContext, ForumThreadActivity::class.java)
            intent.putExtra("POST_ID", forum_list.post_id)
            intent.putExtra("USER_ID", forum_list.user_id)
            intent.putExtra("TITLE", forum_list.title)
            startActivity(intent)
        }
        if(isSubPage=="Market")
        {
            intent = Intent(applicationContext, MarketThreadActivity::class.java)
            intent.putExtra("POST_ID", forum_list.post_id)
            intent.putExtra("USER_ID", forum_list.user_id)
            intent.putExtra("TITLE", forum_list.title)
            startActivity(intent)
        }
        if(isSubPage=="Projects")
        {
            intent = Intent(applicationContext, ForumThreadActivity::class.java)
            intent.putExtra("POST_ID", forum_list.post_id)
            intent.putExtra("USER_ID", forum_list.user_id)
            intent.putExtra("TITLE", forum_list.title)
            startActivity(intent)
        }

    }


}


