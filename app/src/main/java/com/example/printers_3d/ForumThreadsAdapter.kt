package com.example.printers_3d

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import io.grpc.internal.JsonUtil
import kotlinx.android.synthetic.main.layout_item_forum_list_articles.view.*
import kotlinx.android.synthetic.main.layout_item_forum_list_threads.view.*
import java.util.ArrayList

class ForumThreadsAdapter(mCtx: Context, val forum_lists : ArrayList<Forum_List_Thread>, var clickListener: OnForumThreadsItemClickListener,var clickListener2: OnForumThreadsItemClickListener2)
    : RecyclerView.Adapter<ForumThreadsAdapter.ViewHolder>()
{
    val mCtx= mCtx
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        val txtForumAuthor=itemView.txt_forum_list_article_author_thread
        val txtForumTitle=itemView.txt_forum_list_article_title_thread
        val txtForumDate=itemView.txt_forum_list_article_date_thread
        val txt_forum_list_article_dateforum_author_avatar=itemView.txt_forum_list_article_dateforum_author_avatar_thread
        val txt_forum_list_article_dateforum_description=itemView.txt_forum_list_article_description_thread
        val txt_forum_list_article_dateforum_author_thumbnail_thread=itemView.txt_forum_list_article_dateforum_author_thumbnail_thread
        fun initialize(forum_list: Forum_List_Thread, action:OnForumThreadsItemClickListener)
        {

            txtForumAuthor.text=forum_list.author
            txtForumTitle.text= forum_list.title
            txtForumDate.text= forum_list.date

            txt_forum_list_article_dateforum_description.text=forum_list.description

            if(forum_list.avatar!=null)
            {
                Glide.with(txt_forum_list_article_dateforum_author_avatar)
                    .load(forum_list.avatar)
                    .into(txt_forum_list_article_dateforum_author_avatar)
            }
            if(forum_list.imageThumbnail!=null)
            {
                Glide.with(txt_forum_list_article_dateforum_author_thumbnail_thread)
                    .load(forum_list.imageThumbnail)
                    .into(txt_forum_list_article_dateforum_author_thumbnail_thread)
            }

            itemView.setOnClickListener{
                action.OnItemClick(forum_list,adapterPosition)
            }

        }
        fun clickThumbnail(forum_list: Forum_List_Thread, action:OnForumThreadsItemClickListener2)
        {
            itemView.setOnClickListener{
                action.OnItemClick2(forum_list,adapterPosition)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForumThreadsAdapter.ViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.layout_item_forum_list_threads,parent,false)
        return ViewHolder(v)

    }

    override fun getItemCount(): Int {
        return forum_lists.size
    }

    override fun onBindViewHolder(holder: ForumThreadsAdapter.ViewHolder, position: Int) {
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
        holder.clickThumbnail(forum_lists.get(position),clickListener2)

    }
    interface OnForumThreadsItemClickListener
    {
        fun OnItemClick(item:Forum_List_Thread,position: Int)

    }
    interface OnForumThreadsItemClickListener2
    {
        fun OnItemClick2(item:Forum_List_Thread,position: Int)

    }
}