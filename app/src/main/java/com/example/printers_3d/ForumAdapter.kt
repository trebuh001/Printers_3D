package com.example.printers_3d

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.provider.ContactsContract
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_item_forum_list_articles.view.*

import java.util.ArrayList

class ForumAdapter(mCtx: Context,val forum_lists : ArrayList<Forum_List>)
    : RecyclerView.Adapter<ForumAdapter.ViewHolder>()
{
    val mCtx= mCtx
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val txtForumAuthor=itemView.txt_forum_list_article_author
        val txtForumTitle=itemView.txt_forum_list_article_title
        val txtForumDate=itemView.txt_forum_list_article_date
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForumAdapter.ViewHolder {
        val v=LayoutInflater.from(parent.context).inflate(R.layout.layout_item_forum_list_articles,parent,false)
        return ViewHolder(v)

    }

    override fun getItemCount(): Int {
        return forum_lists.size
    }

    override fun onBindViewHolder(holder: ForumAdapter.ViewHolder, position: Int) {
        val forum_list: Forum_List= forum_lists[position]
        holder.txtForumAuthor.text=forum_list.author
        holder.txtForumTitle.text= forum_list.title
        holder.txtForumDate.text= forum_list.date
    }
}


class Utils {

    companion object {
        fun startActivity(context: Context, clas: Class<*>) {
            val intent = Intent(context, clas)
            context.startActivity(intent)
        }
    }

}