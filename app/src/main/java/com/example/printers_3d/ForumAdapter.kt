package com.example.printers_3d

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.provider.ContactsContract
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.firebase.ui.auth.viewmodel.email.EmailLinkSendEmailHandler
import kotlinx.android.synthetic.main.activity_big_picture.*
import kotlinx.android.synthetic.main.layout_item_forum_list_articles.view.*

import java.util.ArrayList

class ForumAdapter(mCtx: Context,val forum_lists : ArrayList<Forum_List>,var clickListener: OnForumItemClickListener )
    : RecyclerView.Adapter<ForumAdapter.ViewHolder>()
{
    val mCtx= mCtx
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val txtForumAuthor=itemView.txt_forum_list_article_author
        val txtForumTitle=itemView.txt_forum_list_article_title
        val txtForumDate=itemView.txt_forum_list_article_date
        val txt_forum_list_article_dateforum_author_avatar=itemView.txt_forum_list_article_dateforum_author_avatar
        fun initialize(forum_list: Forum_List, action:OnForumItemClickListener)
        {
            txtForumAuthor.text=forum_list.author
            txtForumTitle.text= forum_list.title
            txtForumDate.text= forum_list.date
            if(forum_list.image!=null)
            {
                itemView.Progress_bar_user_photo_threads.visibility = View.VISIBLE
                Glide.with(txt_forum_list_article_dateforum_author_avatar)
                    .load(forum_list.image)
                    .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(p0: GlideException?, p1: Any?, p2: Target<Drawable>?, p3: Boolean): Boolean {
                        itemView.Progress_bar_user_photo_threads.visibility = View.GONE
                        return true
                    }
                    override fun onResourceReady(p0: Drawable?, p1: Any?, p2: Target<Drawable>?, p3: DataSource?, p4: Boolean): Boolean {
                        itemView.Progress_bar_user_photo_threads.visibility = View.GONE
                        return false
                    }
                })
                    .into(txt_forum_list_article_dateforum_author_avatar)
            }

            itemView.setOnClickListener{
                action.OnItemClick(forum_list,adapterPosition)
            }

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForumAdapter.ViewHolder {
        val v=LayoutInflater.from(parent.context).inflate(R.layout.layout_item_forum_list_articles,parent,false)
        return ViewHolder(v)

    }

    override fun getItemCount(): Int {
        return forum_lists.size
    }

    override fun onBindViewHolder(holder: ForumAdapter.ViewHolder, position: Int) {
       // val forum_list: Forum_List= forum_lists[position]
        //holder.txtForumAuthor.text=forum_list.author
        //holder.txtForumTitle.text= forum_list.title
        //holder.txtForumDate.text= forum_list.date
        //if(forum_list.image!=null)
        //{
         //   Glide.with(holder.txt_forum_list_article_dateforum_author_avatar)
          //      .load(forum_list.image)
          //      .into(holder.txt_forum_list_article_dateforum_author_avatar)
        //}
        holder.initialize(forum_lists.get(position),clickListener)

    }
    interface OnForumItemClickListener
    {
        fun OnItemClick(item:Forum_List,position: Int)

    }

    
}

/*private const val TYPE_TEXT: Int=0
private const val TYPE_AVATAR: Int=1


class ForumAdapter(var forumListItems : List<Forum_List>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    class TextViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        fun bind(forumList: Forum_List)
        {
           /*val txtForumAuthor=itemView.txt_forum_list_article_author
            val txtForumTitle=itemView.txt_forum_list_article_title
            val txtForumDate=itemView.txt_forum_list_article_date*/
            itemView.txt_forum_list_article_author.text=forumList.author
            itemView.txt_forum_list_article_title.text=forumList.title
            itemView.txt_forum_list_article_date.text=forumList.date


        }
    }
    class AvatarViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        fun bind(forumList: Forum_List)
        {
            Glide.with(itemView.context).load(forumList.image)
                .into(itemView.txt_forum_list_article_dateforum_author_avatar)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType== TYPE_TEXT)
        {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.layout_item_forum_list_articles, parent, false
            )
            return TextViewHolder(view)
        }
        else
        {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.layout_item_forum_list_articles, parent, false
            )
            return AvatarViewHolder(view)
        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position)== TYPE_TEXT)
        {
            (holder as TextViewHolder).bind(forumListItems[position])
        }
        else
        {
            (holder as AvatarViewHolder).bind(forumListItems[position])
        }

    }

    override fun getItemCount(): Int {
        return forumListItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return if(forumListItems[position].type == 0L)
        {
            TYPE_TEXT
        }
        else
        {
            TYPE_AVATAR
        }

    }
}
*/