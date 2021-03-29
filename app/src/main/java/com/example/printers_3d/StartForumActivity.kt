package com.example.printers_3d

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.play.core.assetpacks.v
import kotlinx.android.synthetic.main.activity_start_forum.*
import java.util.ArrayList

class StartForumActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_forum)

    }
    fun Forum(view: View)
    {
        txt_forum_title.setText(getString(R.string.start_forum_title))
        btn_forum.background.alpha=100
        btn_market.background.alpha=255
        btn_projects.background.alpha=255


    }
    fun Market(view: View)
    {
        txt_forum_title.setText(getString(R.string.start_market_title))
        btn_market.background.alpha=100
        btn_forum.background.alpha=255
        btn_projects.background.alpha=255
    }
    fun Projects(view: View)
    {
        txt_forum_title.setText(getString(R.string.start_projects_title))
        btn_projects.background.alpha=100
        btn_market.background.alpha=255
        btn_forum.background.alpha=255
    }
    fun NewThread(view: View)
    {

    }


    @SuppressLint("WrongConstant")
    private fun viewForum()
    {
        var list= ArrayList<Forum_List>()

        list.add(0,Forum_List("Tytuł strony1","Autor strony1","data"))
        list.add(1,Forum_List("Tytuł strony1","Autor strony1","data"))
        list.add(2,Forum_List("Tytuł strony1","Autor strony1","data"))
        list.add(3,Forum_List("Tytuł strony1","Autor strony1","data"))
        val adapter= ForumAdapter(this,list)
        val rv : RecyclerView = findViewById(R.id.rv)
        rv.layoutManager= LinearLayoutManager(this, LinearLayout.VERTICAL,false) as RecyclerView.LayoutManager
        rv.adapter= adapter
    }

    override fun onResume()
    {
        viewForum()
        super.onResume()
    }

}


